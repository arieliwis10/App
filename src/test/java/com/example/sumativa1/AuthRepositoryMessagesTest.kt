package com.example.sumativa1

import com.example.sumativa1.data.model.AuthRepository
import com.example.sumativa1.testfakes.FakeMessageDao
import com.example.sumativa1.testfakes.FakeUserDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryMessagesTest {

    private lateinit var repo: AuthRepository

    @Before
    fun setUp() = runBlocking {
        val userDao = FakeUserDao()
        val messageDao = FakeMessageDao()
        repo = AuthRepository(userDao, messageDao)

        assertTrue(repo.register("Ariel", "msg@test.com", "1234").isSuccess)
        assertTrue(repo.login("msg@test.com", "1234").isSuccess)
    }

    @Test
    fun add_and_list_messages_for_current_user() = runBlocking {
        repo.addMensaje("Hola", null, null)
        Thread.sleep(5)
        repo.addMensaje("Mundo", -33.0, -70.0)

        val list = repo.getMensajes()
        assertEquals(2, list.size)
        assertEquals("Mundo", list.first().text)
    }

    @Test
    fun delete_message() = runBlocking {
        repo.addMensaje("Para borrar", null, null)
        val first = repo.getMensajes().first()
        repo.deleteMensaje(first)
        assertEquals(0, repo.getMensajes().size)
    }
}
