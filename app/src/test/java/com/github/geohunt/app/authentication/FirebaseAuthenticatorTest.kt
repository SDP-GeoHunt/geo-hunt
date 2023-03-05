package com.github.geohunt.app.authentication

import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FirebaseAuthenticatorTest {
    @Test
    fun userIsNullIfNotLoggedIn() {
        val mockedFirebaseAuth = mock<FirebaseAuth> {
            on { currentUser } doReturn null
        }
        val mockedUi = mock<AuthUI>()

        val auth = FirebaseAuthenticator(mockedFirebaseAuth, mockedUi)

        assert(auth.user == null)
    }

    @Test
    fun userIsCorrectlyPassed() {
        val mockedUser = mock<FirebaseUser> {
            on { displayName } doReturn("Display name")
            on  { uid } doReturn("Uid")
        }
        val mockedFirebaseAuth = mock<FirebaseAuth> {
            on { currentUser } doReturn mockedUser
        }
        val mockedUi = mock<AuthUI>()

        val auth = FirebaseAuthenticator(mockedFirebaseAuth, mockedUi)

        assert(auth.user?.displayName.equals("Display name"))
        assert(auth.user?.uid.equals("Uid"))
    }

    @Test
    fun signOutCallsFirebaseSignOut() {
        val mockedUser = mock<FirebaseUser> {
            on { displayName } doReturn("Display name")
            on  { uid } doReturn("Uid")
        }
        val mockedFirebaseAuth = mock<FirebaseAuth> {
            on { currentUser } doReturn mockedUser
        }
        val mockedTask = mock<Task<Void>> {
            on { addOnCompleteListener { any() }} doReturn it
        }
        val mockedFirebaseAuthUi = mock<AuthUI> {
            on { signOut(any()) } doReturn(mockedTask)
        }
        val auth = FirebaseAuthenticator(mockedFirebaseAuth, mockedFirebaseAuthUi)

        auth.signOut(mock())
        verify(mockedFirebaseAuthUi).signOut(any())
    }

}