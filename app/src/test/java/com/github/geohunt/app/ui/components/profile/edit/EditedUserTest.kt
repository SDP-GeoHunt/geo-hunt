package com.github.geohunt.app.ui.components.profile.edit

import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.model.database.api.EditedUser
import org.junit.Test

class EditedUserTest {

    @Test
    fun properlyTakesNameFromUser() {
        val user = MockUser(displayName = "hello")
        val editedUser = EditedUser(user.name)
        assert(editedUser.displayName == "hello")
    }
}