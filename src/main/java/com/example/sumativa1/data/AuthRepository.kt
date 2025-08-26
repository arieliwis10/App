package com.example.sumativa1.data

public object AuthRepository {
    private val users = mutableListOf<User>()


    fun registerUser(email: String, pass: String): Boolean {
        if (users.any { it.email == email }) {
            println("AuthRepo: Registro fallido - Email ya existe: $email")
            return false
        }
        users.add(User(email = email, pass = pass))
        println("AuthRepo: Usuario registrado exitosamente: $email")

        if (users.size == 1) {
            users.add(User(email = "test@example.com", pass = "password123"))
            users.add(User(email = "user@example.com", pass = "securepass"))
            println("AuthRepo: Usuarios de prueba añadidos.")
        }
        println("AuthRepo: Lista actual de usuarios: ${users.joinToString { it.email }}")
        return true
    }

    fun loginUser(email: String, pass: String): Boolean {
        val user = users.find { it.email == email && it.pass == pass }
        if (user != null) {
            println("AuthRepo: Login exitoso para: $email")
        } else {
            println("AuthRepo: Login fallido para: $email")
            println("AuthRepo: Usuarios conocidos: ${users.joinToString { it.email + " (Pass: " + it.pass + ")" }}")
        }
        return user != null
    }

    fun requestPasswordRecovery(email: String): Boolean {
        val userExists = users.any { it.email == email }
        if (userExists) {
            println("AuthRepo: Solicitud de recuperación para email existente: $email. (Simulado: se enviaría correo)")
        } else {
            println("AuthRepo: Solicitud de recuperación para email NO existente: $email.")
        }
        return userExists
    }
}
