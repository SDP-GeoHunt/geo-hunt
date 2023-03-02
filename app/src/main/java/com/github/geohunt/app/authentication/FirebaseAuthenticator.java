package com.github.geohunt.app.authentication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A firebase authenticator.
 * Should be instantiated using the .getInstance() method
 */
public class FirebaseAuthenticator implements Authenticator<FirebaseUser> {
    private FirebaseAuthenticator() { }

    /**
     * Tries to log in the user.
     * If the user is not logged in, creates a new activity from Firebase UI to login the user.
     * Otherwise, returns the logged in user.
     * @param activity The activity from which the authentication is asked.
     * @return A completable future which will either 1. complete exceptionally if the login triggered an exception
     * 2. complete successfully with the user if the user has been successfully signed in
     */
    @Override
    public CompletableFuture<FirebaseUser> authenticate(ComponentActivity activity) {
        final CompletableFuture<FirebaseUser> user = new CompletableFuture<>();

        // Checks if already signed-in
        final FirebaseUser alreadyLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();

        if (alreadyLoggedInUser != null) {
            user.complete(alreadyLoggedInUser);
            return user;
        }

        final ActivityResultLauncher<Intent> signInLauncher = activity.registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    user.complete(FirebaseAuth.getInstance().getCurrentUser());
                } else {
                    assert(result.getIdpResponse() != null);

                    user.completeExceptionally(result.getIdpResponse().getError());
                }
            }
        );

        createSignInIntent(signInLauncher);

        return user;
    }

    /**
     * Signs out the user and completes the future when the user has been
     * successfully signed out.
     * @param context The context from which the signing out is asked (usually an activity).
     * @return A completable future which will be completed when the user has been successfully signed out.
     */
    @Override
    public CompletableFuture<Void> signOut(Context context) {
        final CompletableFuture<Void> callback = new CompletableFuture<>();

        AuthUI.getInstance().signOut(context).addOnCompleteListener(s -> callback.complete(null));

        return callback;
    }


    private void createSignInIntent(ActivityResultLauncher<Intent> signInLauncher) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public static FirebaseAuthenticator self = new FirebaseAuthenticator();
    public static FirebaseAuthenticator getInstance() {
        return self;
    }
}
