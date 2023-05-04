package com.github.geohunt.app.ui.components.profile.edit

import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User
import org.junit.Test

class EditedUserTest {

    @Test
    fun properlyTakesNameFromUser() {
        val user = User(id = "zef", displayName = "hello", profilePictureUrl = "")
        val editedUser = EditedUser.fromUser(user)
        assert(editedUser.newDisplayName == "hello")
    }
}