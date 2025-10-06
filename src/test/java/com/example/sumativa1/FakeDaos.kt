package com.example.sumativa1.testfakes

import com.example.sumativa1.data.db.MessageDao
import com.example.sumativa1.data.db.UserDao
import com.example.sumativa1.data.model.Message
import com.example.sumativa1.data.model.User
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicInteger

// ===== FakeUserDao =====
class FakeUserDao : UserDao {
    private val autoId = AtomicInteger(1)
    private val data = mutableListOf<User>()

    override suspend fun insert(user: User): Long {
        val id = autoId.getAndIncrement()
        data += user.copy(id = id)
        return id.toLong()
    }

    override suspend fun update(user: User) {
        val i = data.indexOfFirst { it.id == user.id }
        if (i >= 0) data[i] = user
    }

    override suspend fun delete(user: User) {
        data.removeIf { it.id == user.id }
    }

    override suspend fun getAll(): List<User> = data.sortedByDescending { it.id }

    override suspend fun getById(id: Int): User? = data.firstOrNull { it.id == id }

    override suspend fun getByEmail(email: String): User? =
        data.firstOrNull { it.email.equals(email, ignoreCase = true) }

    override suspend fun login(email: String, password: String): User? =
        data.firstOrNull { it.email.equals(email, ignoreCase = true) && it.password == password }
}

// ===== FakeMessageDao =====
class FakeMessageDao : MessageDao {
    private val autoId = AtomicInteger(1)
    private val data = mutableListOf<Message>()

    override suspend fun insert(m: Message): Long {
        val id = autoId.getAndIncrement()
        data += m.copy(id = id)
        return id.toLong()
    }

    override suspend fun getAllByUser(userId: Int): List<Message> =
        data.filter { it.userId == userId }.sortedByDescending { it.createdAt }

    override fun observeAllByUser(userId: Int) =
        flow { emit(getAllByUser(userId)) }

    override suspend fun delete(m: Message) {
        data.removeIf { it.id == m.id }
    }
}
