package com.github.geohunt.app.authentication;

import android.content.Context;

import androidx.activity.ComponentActivity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Authenticator<UserType> {
    /**
     * Invites the user to authenticate by creating an intent.
     */
    CompletableFuture<Optional<UserType>> authenticate(ComponentActivity activity);

    CompletableFuture<Void> signOut(Context context);
}
