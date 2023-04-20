<h1 align="center"> GeoHunt - Android Java Project</h1>

<div align="center">
    <a href="https://cirrus-ci.com/github/SDP-GeoHunt/geo-hunt"><img src="https://api.cirrus-ci.com/github/SDP-GeoHunt/geo-hunt.svg" /></a>
    <a href="https://codeclimate.com/github/SDP-GeoHunt/geo-hunt/maintainability"><img src="https://api.codeclimate.com/v1/badges/76d4967c5d3d48e7022f/maintainability"/></a>
    <a href="https://codeclimate.com/github/SDP-GeoHunt/geo-hunt/test_coverage"><img src="https://api.codeclimate.com/v1/badges/76d4967c5d3d48e7022f/test_coverage" /></a>
</div>

The GeoHunt project is an Android game developed in Java that encourages users to explore new locations
by taking pictures of their surroundings and challenging other users to find and reach the location
where the picture was taken. Users can register and create their account, take pictures and upload them
to the game's server, view a list of their active challenges, and view the leaderboard to see who has the
most points. The game is designed to be fun, interactive, and engaging, and to provide users with an opportunity
to discover new places and environments while enjoying a game. Overall, the GeoHunt project is a unique and
innovative way to explore the world around us while having fun with other players.


## How to Play

1. Users take a picture of their current location and upload it to the game's server.
2. The game assigns the uploaded picture to another user, who must find and go to the location where the picture was taken.
3. The closer the user gets to the actual location, the higher the points they earn.
4. Users can view the game's leaderboard to see who has the most points.

## Features

* Users can register and create their account
* Users can take pictures and upload them to the game's server
* Users can view a list of their active challenges.
* Users can view the leaderboard to see who has the most points.
* Users can report inappropriate content.

## Contributing Guidelines

Thank you for considering contributing to our private repository! To ensure the confidentiality
and security of our codebase, we have some guidelines for contributing that we ask you to follow:

1. Make sure your code follows the project's code style and best practices.
2. Write clear and concise commit messages that describe the changes you made.
3. When adding new features or fixing bugs, make sure to write tests and ensure all existing tests pass.
4. If your contribution changes any existing functionality, make sure to update the project's documentation accordingly.
5. Submit a pull request and wait for contributors to review your changes.

## Code Style and Best Practices

* Use Java coding standards and best practices. See the [Google Style Guide](https://google.github.io/styleguide/javaguide.html) for further information.
* Follow the project's code style guide and best practices.
* Keep your code clean, concise, and easy to read.
* Use meaningful variable names and comments to explain your code.
* Use proper exception handling to ensure code reliability.
* Write tests to ensure the reliability of your code.
* Avoid using third-party libraries unless necessary.
* Keep code modular and follow separation of concerns.

## Populate the database
A small python script is provided to populate the database with Point-Of-Interrest using open-street-map API along with a small web-scrapper script. To use it, have `python 3` installed along with the following libraries : `Pillow`, `firebase_admin`, `requests`, `tqdm`, `pickle`, `zlib`. You then need to generated the private admin key for firebase. Notice with this key, anyone has an admin access to all features of firebase [check this tutorial](https://firebase.google.com/docs/admin/setup). Once done you can use this script with
```bash
python3 scrap-point-of-interrest.py <top-dd> <left-bbox> <bottom-bbox> <right-bbox> <path-to-auth-file>
```
Where `<...dd>` correspond to the degree notation of the top/left/bottom/right corner



