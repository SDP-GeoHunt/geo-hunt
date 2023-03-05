package com.github.geohunt.app.authentication

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FirebaseUserAdapterTest {
    @Test
    fun displayNameIsCorrectlyAdapted() {
        val firebaseUser = mock<FirebaseUser> {
            on { displayName } doReturn "display name"
        }

        val adapter = FirebaseUserAdapter(firebaseUser)

        assert(adapter.displayName.equals("display name"))
    }

    @Test
    fun uidIsCorrectlyAdapted() {
        val firebaseUser = mock<FirebaseUser> {
            on { uid } doReturn "uid"
        }
        val adapter = FirebaseUserAdapter(firebaseUser)
        assert(adapter.uid.equals("uid"))
    }

    @Test
    fun settingDisplayNameMakesTheProperRequest() {
        val mockVoidTask = mock<Task<Void>>()
        val firebaseUser = mock<FirebaseUser> {
            on { updateProfile(any()) } doReturn mockVoidTask
        }
        val adapter = FirebaseUserAdapter(firebaseUser)

        adapter.displayName = "Machin"
        verify(firebaseUser).updateProfile(Mockito.argThat { that -> that.displayName.equals("Machin") })
    }
}