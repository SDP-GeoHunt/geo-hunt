package com.github.geohunt.app.model.database

import com.github.geohunt.app.ConstantStrings
import com.github.geohunt.app.model.InvalidLazyRef
import com.github.geohunt.app.model.database.firebase.FirebasePOIUserRef
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class TestFirebasePOIUserRef {

    @Test
    fun firebasePOIUserRefDoesNotThrowWhenFetching() {
        val poiUser = FirebasePOIUserRef(ConstantStrings.UUID_0)
        poiUser.fetch()
    }

    @Test
    fun firebasePOIUserRefAlwaysResultInSuccess() = runTest {
        val poiUser = FirebasePOIUserRef(ConstantStrings.UUID_0)
        val resultingTask = poiUser.fetch()
        resultingTask.await()
        assertThat(resultingTask.isComplete, equalTo(true))
        assertThat(resultingTask.isSuccessful, equalTo(true))
    }

    @Test
    fun firebasePOIUserFieldsAreCorrect() = runTest {
        val poiUser = FirebasePOIUserRef(ConstantStrings.UUID_0)
        val user = poiUser.fetch().await()

        assertThat(user.isPOIUser, equalTo(true))
        assertThat(user.uid, equalTo(ConstantStrings.UUID_0))
        assertThat(user.profilePicture, instanceOf(InvalidLazyRef::class.java))
        assertThat(user.score, equalTo(0))
        assertThat(user.displayName, nullValue())
    }
}