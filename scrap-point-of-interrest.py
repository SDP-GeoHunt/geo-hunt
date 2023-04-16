from PIL import Image, ImageOps
from lxml import html
from datetime import datetime, timezone
from firebase_admin import credentials, db, storage, initialize_app
import xml.etree.ElementTree as ET
import requests 
import re
import sys
import zlib
import pickle
import base64
import os
import tqdm
import time

# The id of the user under which all challenges scrapped here will be registered
POI_AUTHOR_ID = '0'

# Utilities for save/restoring variable to file and caching them
def pickle_load(filename):
    with open(filename, 'rb') as file:
        return  pickle.load(file)

def pickle_store(filename, data):
    with open(filename, 'wb') as file:
        pickle.dump(data, file)

def pickle_cache(filename, callback):
    if os.path.exists(filename):
        return pickle_load(filename)
    else:
        data = callback()
        pickle_store(filename, data)
        return data

# Special exception whenever a request is not successful (status_code != 400)
class RequestException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)  

# Convert the degree-minutes-second representation back to degree
def dms2dd(degrees, minutes, seconds, direction):
    dd = float(degrees) + float(minutes)/60 + float(seconds)/(60*60)
    if direction == 'E' or direction == 'S':
        dd *= -1
    return dd

# Convert degree to degree-minute-second string presentation
def dd2dms(deg, direction = 'lat'):
    d = int(deg)
    md = abs(deg - d) * 60
    m = int(md)
    sd = (md - m) * 60
    sd = int(sd * 10) / 10

    if direction == 'long':
        return f"{abs(d)}° {m}' {sd}''{'S' if d < 0 else 'N'}"
    else:
        return f"{abs(d)}° {m}' {sd}''{'W' if d < 0 else 'E'}"

# Parse a degree-minute-second formatted location back to degree latitude, longitude 2-tuple
def parse_dms(dms):
    parts = re.split('[^\d\w]+', dms)
    lat = dms2dd(parts[0], parts[1], parts[2], parts[3]) 
    long = dms2dd(parts[4], parts[5], parts[6], parts[7]) 
    return (lat, long)

# Download an image at a given url in a given file, if the script detect that
# The given url is instead an HTML page, will download the first image contained within body
def download_image(url, file_name, depth = 0):
    print(f'Downloading {url}...')
    time.sleep(1)
    res = requests.get(url, stream = True, headers={ 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0' })
    if res.status_code == 200:
        if 'content-type' in res.headers:
            tree = html.fromstring(res.content)
            res.close()
            if 'html' in res.headers['content-type']:
                print('Html page detected, scraping and downloading first image')
                if depth > 0:
                    raise Exception('Maximum number of recursive jump reached')
                result = [url for url in tree.xpath('body//img[@alt]/@src'.format(url.split('/')[-1])) if not '.svg.png' in url and url.startswith('https://')]
                if len(result) == 0:
                    raise Exception('No image could be found matching the requirements')
                download_image(result[0], file_name, depth + 1)
                return

        with open(file_name,'wb') as f:
            for chunk in res:
                f.write(chunk)
        res.close()
        print('Image sucessfully Downloaded:', file_name)
    elif res.status_code == 403: # Forbidden
        res.close()
        if depth > 5:
            raise Exception('Maximum number of retrial jump reached')
        tm = (2 << depth)
        print(f'Forbidden, retrying in {tm} second')
        time.sleep(tm)
        download_image(url, file_name, depth + 1)
    else:
        res.close()
        print('Image Couldn\'t be retrieved')
        raise Exception(f'Image download Failure: {url}: {res.status_code}')

# Perform a request to the open-street-map API (overpass-api) for all nodes in a certain
# Bounding box region that can be a POI (Point-Of-Interest). Check the query to see
# How such points are defined
def osm_map_dd(top, left, bottom, right):
    filename = f'bin/overpass-{left}-{bottom}-{right}-{top}.bin'

    def callback():
        query = '''
        (
            nwr[tourism="attraction"][name~".*"][image~".*"]({0});
            nwr[tourism="artwork"][name~".*"][image~".*"]({0});
            nwr[tourism="alpine_hut"][name~".*"][image~".*"]({0});
            nwr[tourism="aquarium"][name~".*"][image~".*"]({0});
            nwr[amenity="monastery"][name~".*"][image~".*"]({0});
            nwr[amenity="grave_yard"][name~".*"][image~".*"]({0});
            nwr[amenity="clock"][name~".*"][image~".*"]({0});
            nwr[amenity="townhall"][name~".*"][image~".*"]({0});
            nwr[amenity="theatre"][name~".*"][image~".*"]({0});
            nwr[amenity="fountain"][name~".*"][image~".*"]({0});
            nwr[amenity="conference_centre"][name~".*"][image~".*"]({0});
            nwr[amenity="community_centre"][name~".*"][image~".*"]({0});
            nwr[amenity="cinema"][name~".*"][image~".*"]({0});
            nwr[amenity="casino"][name~".*"][image~".*"]({0});
            nwr[amenity="arts_centre"][name~".*"][image~".*"]({0});
            nwr[amenity="university"][name~".*"][image~".*"]({0});
            nwr[amenity="place_of_worship"][name~".*"][image~".*"]({0});
            nwr[barrier="city_wall"][name~".*"][image~".*"]({0});
            nwr[building="commercial"][name~".*"][image~".*"]({0});
            nwr[building="industrial"][name~".*"][image~".*"]({0});
            nwr[geological~".*"][name~".*"][image~".*"]({0});
        );
        out;
        '''.format(f'{bottom},{left},{top},{right}').replace(' ', '').replace('\n', '').replace('\t','').replace('\r','')
        endpoint = f'https://overpass-api.de/api/interpreter?data={requests.utils.quote(query)}'
        print(f'Fetching {dd2dms(top, "long")} {dd2dms(left, "lat")} to {dd2dms(bottom, "long")} {dd2dms(right, "lat")} at url: {endpoint}')
        re = requests.get(endpoint)
        if re.status_code != 200:
            print('Fail to retrieve the required data: {}'.format(str(re.headers)))
            raise RequestException("")
        
        return re.content
    
    return pickle_cache(filename, callback)

# Perform a recursive request to the open-street-map API (overpass-api) for all nodes 
# in a certain bounding box. Notice that if a call fail due to too many object being
# Fetch this method will subdivide the region in 4 smaller one and retry the same operation
def recursive_open_map_dd(callback, top, left, bottom, right, bar_updater=None):
    bar = None
    area = abs(top - bottom) * abs(left - right)
    if bar_updater == None:
        bar = tqdm.tqdm(total=100, desc=f'Fetching an area {area}')
        bar_updater = lambda x: bar.update((x * 100.0) / area)

    # First try to fetch the region of interest using API
    try:
        nodes = ET.fromstring(osm_map_dd(top, left, bottom, right))

        for node in nodes:
            d = {}
            for tag in node:
                if tag.tag == 'tag':
                    d[tag.attrib['k']] = tag.attrib['v']
                if tag.tag == 'nd':
                    pass
            
            if ('lon' in node.attrib) and ('lat' in node.attrib):
                callback(dict(list(d.items()) + [('lon', node.attrib['lon']), ('lat', node.attrib['lat'])]))

        bar_updater(area)

    except RequestException as e:
        # Subdivision of the region in 4 smaller one
        middle_long = (top + bottom) / 2
        middle_lat = (left + right) / 2

        # Perform all subdivision calls
        recursive_open_map_dd(callback, top, left, middle_long, middle_lat, bar_updater)
        recursive_open_map_dd(callback, top, middle_lat, middle_long, right, bar_updater)
        recursive_open_map_dd(callback, middle_long, left, bottom, middle_lat, bar_updater)
        recursive_open_map_dd(callback, middle_long, middle_lat, bottom, right, bar_updater)

    if bar:
        bar.close()

# Compute the geo-hash has done in the database for a given latitude-longitude
def geoHash(lat, long):
    lat = round(lat * 10.0) 
    long = round(long * 10.0) 
    print((lat, long))
    bytes = lat.to_bytes(8, 'big') + long.to_bytes(8, 'big')
    return hex(zlib.crc32(bytes) & 0xffffffff)[2:]

# Main function that fetches all POI in a certain bounding-box and register
# them to the database. The fifth argument should be the path to the json
# file containing the admin-credential for firebase. PAY ATTENTION TO NOT
# PUSH THIS FILE TO THE VERSION CONTROL REPOSITORY BECAUSE THESE PERMISSIONS
# ENABLE ANYONE TO ACCESS EVERYTHING IN THE DATABASE
def main(top, left, bottom, right, path_to_auth_json):
    all_nodes = []
    def filter_callback(attributes):
        all_nodes.append(attributes)

    # Retrieving POI on the specified region
    print('Retrieving POI on the specified region...')
    re = recursive_open_map_dd(filter_callback, top, left, bottom, right)

    # Ask confirmation of the user
    print('There are {} entries in the database'.format(len(all_nodes)))
    r = input('Are you sure you want to continue (pushing all of these to geohunt) [Y/n] : ')
    if r.upper() != 'Y':
        print('Cancelling')
        return

    # Connecting to firebase and setting up authentification as admin
    print('Connecting to firebase')
    cred = credentials.Certificate(path_to_auth_json)

    # Initialize the app with a service account, granting admin privileges
    initialize_app(cred, {
        'databaseURL': 'https://geohunt-1-default-rtdb.europe-west1.firebasedatabase.app/',
        'storageBucket': 'geohunt-1.appspot.com'
    })

    bucket = storage.bucket()
    
    # For each node, download the corresponding image and 
    # try registering it to firebase
    for poi in all_nodes:
        try:
            # Compute the geo-hash for the current challenge
            print(f'Adding poi: {poi["name"]}')
            latitude = float(poi['lat'])
            longitude = float(poi['lon'])
            hash = geoHash(latitude, longitude)

            # Download the image 
            base_path = 'bin/' + base64.urlsafe_b64encode(poi['image'].encode('utf-8')).decode('utf-8')
            file_path = base_path + '.' + poi['image'].split('.')[-1]
            new_path = base_path + '.jpeg'
            download_image(poi['image'], file_path)

            # Image re-formatting does two things:
            #  - resize it if bigger than the biggest pixel density supported (1024 * 1024)
            #  - convert the format to a JPEG
            img = Image.open(file_path)
            if img.width * img.height > 1024 * 1024:
                img = ImageOps.contain(img, (1024,1024))
            img.save(new_path)

            # Push the challenge metadata to the realtime database
            new_ref = db.reference(f'challenges/{hash}').push()
            challengeId = hash + new_ref.key

            utc_dt = datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z') # A "bit" hacky but hey if it works
            description = None
            if 'description' in poi:
                description = str(poi['description'])

            new_ref.set({
                'authorId': POI_AUTHOR_ID,
                'expirationDate': None,
                'description': description,
                'location': { 'latitude': latitude, 'longitude': longitude },
                'publishedDate': utc_dt
            })

            # Upload the image to firebase storage
            blob = bucket.blob(f'images/challenges-{challengeId}.jpeg')
            blob.upload_from_filename(new_path)

        except KeyboardInterrupt as e:
            print('Process was interrupted')
            return

        except Exception as e:
            print('Exception: {} was thrown.\nSkipping poi {}'.format(e, poi['name']))

# Main entry point for this script using argument provided when running the script
# These are <top-bbox> <left-bbox> <bottom-bbox> <right-bbox> <path-to-auth-file>
if __name__ == '__main__':
    if len(sys.argv) != 6:
        print(f'Incorrect usage: should be python {sys.argv[0]} <top-bbox> <left-bbox> <bottom-bbox> <right-bbox> <path-to-auth-file>')
        sys.exit(1)
    if not os.path.exists(sys.argv[5]):
        print(f'No such file or directory "{sys.argv[5]}"')
        sys.exit(1)

    os.makedirs('bin', exist_ok=True)
    main(float(sys.argv[1]), float(sys.argv[2]), float(sys.argv[3]), float(sys.argv[4]), sys.argv[5])

