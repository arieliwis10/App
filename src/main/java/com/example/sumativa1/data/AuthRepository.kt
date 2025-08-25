package com.example.sumativa1.data

// Definición de AuthRepository como un object (Singleton)
public object AuthRepository { // <--- Añadido 'public' explícitamente
    private val users = mutableListOf<User>()

    // Simulación de registro de usuario
    fun registerUser(email: String, pass: String): Boolean {
        if (users.any { it.email == email }) {
            println("AuthRepo: Registro fallido - Email ya existe: $email")
            return false // Email ya existe
        }
        users.add(User(email = email, pass = pass))
        println("AuthRepo: Usuario registrado exitosamente: $email")
        // Simular algunos usuarios de prueba
        if (users.size == 1) { // Solo añadir la primera vez para no duplicar en caliente
            users.add(User(email = "test@example.com", pass = "password123"))
            users.add(User(email = "user@example.com", pass = "securepass"))
            println("AuthRepo: Usuarios de prueba añadidos.")
        }
        println("AuthRepo: Lista actual de usuarios: ${users.joinToString { it.email }}")
        return true
    }

    // Simulación de login de usuario
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

    // Simulación de recuperación de contraseña (solo verifica si el usuario existe)
    fun requestPasswordRecovery(email: String): Boolean {
        val userExists = users.any { it.email == email }
        if (userExists) {
            println("AuthRepo: Solicitud de recuperación para email existente: $email. (Simulado: se enviaría correo)")
        } else {
            println("AuthRepo: Solicitud de recuperación para email NO existente: $email.")
        }
        return userExists // Devuelve true si el usuario existe, simulando que se podría enviar un correo.
    }
}
