# Installing for development

1. Clone the repository
2. Open the project with your favorite IDE such as [Android Studio](https://developer.android.com/studio)
3. Wait for Gradle to sync.
4. Build the project (you may need the Google Maps API key. See the note below).
5. It should work like a charm!

## Google Maps API key

Before being able to build the project, you need to define the Google Maps API key for the projet.

Inside the file `/local.properties`, add the following line:

```
MAPS_API_KEY = <key>
```

It should now work like a charm!

Note for SDP graders: if you want the key, check the private channel of the team.

# Firebase Emulator

The firebase emulator is already setup-ed on the CI. You can install one on your local machine with [this tutorial](https://firebase.google.com/docs/functions/local-emulator).

The app uses Firebase Auth, Storage and Realtime database. All configurations files are already in the `/emulator` folder.

Obviously, the app does not support to be ran with the Firebase emulator without changing manually its code.
