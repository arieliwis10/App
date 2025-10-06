package com.example.sumativa1.data.model

import android.content.Context
import com.example.sumativa1.data.db.AppDataBase
import com.example.sumativa1.data.db.MessageDao
import com.example.sumativa1.data.db.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AuthRepository(
    private val userDao: UserDao,
    private val msgDao: MessageDao
) {
    // 👇 Constructor de conveniencia para producción (como el que ya usabas)
    constructor(context: Context) : this(
        AppDataBase.get(context).userDao(),
        AppDataBase.get(context).messageDao()
    )

    private var currentUser: User? = null

    // ---------- Auth ----------
    suspend fun register(name: String, email: String, pass: String): Result<Unit> = runCatching {
        if (userDao.getByEmail(email) != null) error("Ya existe un usuario con ese correo")
        userDao.insert(User(name = name, email = email, password = pass))
    }

    suspend fun login(email: String, pass: String): Result<User> = runCatching {
        val u = userDao.login(email, pass) ?: error("Credenciales inválidas")
        currentUser = u
        u
    }

    fun logout() { currentUser = null }

    fun currentUser(): User? = currentUser

    // ---------- Mensajes ----------
    suspend fun addMensaje(texto: String, lat: Double?, lng: Double?) {
        val u = currentUser ?: error("No hay sesión activa")
        msgDao.insert(Message(userId = u.id, text = texto, lat = lat, lng = lng))
    }

    suspend fun getMensajes(): List<Message> {
        val u = currentUser ?: return emptyList()
        return msgDao.getAllByUser(u.id)
    }

    fun observeMensajes(): Flow<List<Message>> {
        val u = currentUser ?: return flowOf(emptyList())
        return msgDao.observeAllByUser(u.id)
    }

    suspend fun deleteMensaje(m: Message) {
        msgDao.delete(m)
    }
}
