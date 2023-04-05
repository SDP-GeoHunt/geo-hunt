package com.github.geohunt.app.ui.components.profile.edit

import com.github.geohunt.app.mocks.MockUser
import org.junit.Test

class EditedUserTest {

    @Test
    fun appliesProperlyNameChanges() {
        val user = MockUser(displayName = "hello")
        val editedUser = EditedUser.fromUser(user)
        editedUser.displayName = "ok"
        val newUser = editedUser.applyUpdates(user)
        assert(newUser.displayName == "ok")
    }
}