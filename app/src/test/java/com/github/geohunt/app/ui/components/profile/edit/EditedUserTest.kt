package com.github.geohunt.app.ui.components.profile.edit

import com.github.geohunt.app.mocks.MockUser
import org.junit.Test

class EditedUserTest {

    @Test
    fun properlyTakesNameFromUser() {
        val user = MockUser(displayName = "hello")
        val editedUser = EditedUser.fromUser(user)
        assert(editedUser.displayName == "hello")
    }
}