package com.example.sumativa1

import com.example.sumativa1.data.model.AuthRepository
import com.example.sumativa1.testfakes.FakeMessageDao
import com.example.sumativa1.testfakes.FakeUserDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryAuthTest {

    private lateinit var repo: AuthRepository

    @Before
    fun setUp() {
        val userDao = FakeUserDao()
        val messageDao = FakeMessageDao()
        repo = AuthRepository(userDao, messageDao)
    }

    @Test
    fun register_and_login_success() = runBlocking {
        assertTrue(repo.register("Ariel", "ariel@test.com", "1234").isSuccess)
        val login = repo.login("ariel@test.com", "1234")
        assertTrue(login.isSuccess)
        assertEquals("Ariel", login.getOrThrow().name)
    }

    @Test
    fun duplicate_email_fails() = runBlocking {
        assertTrue(repo.register("Uno", "dup@test.com", "x").isSuccess)
        val second = repo.register("Dos", "dup@test.com", "y")
        assertTrue(second.isFailure)
    }

    @Test
    fun wrong_password_fails() = runBlocking {
        repo.register("Ariel", "a@test.com", "1234")
        val bad = repo.login("a@test.com", "9999")
        assertTrue(bad.isFailure)
    }
}
