package com.inmo.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * GENERADOR DE HASH BCRYPT
 * 
 * Ejecuta este programa para generar el hash correcto de cualquier contraseña.
 * El hash generado es seguro y se puede guardar en la base de datos.
 */
public class GeneradorHashBCrypt {
    
    public static void main(String[] args) {
        // Contraseña a hashear (cámbiala si deseas otra contraseña)
        String passwordAdmin = "1234";
        
        // Generar hash BCrypt (10 rondas = seguridad + velocidad balanceada)
        String hashGenerado = BCrypt.hashpw(passwordAdmin, BCrypt.gensalt(10));
        
        System.out.println("════════════════════════════════════════════");
        System.out.println("GENERADOR DE HASH BCRYPT");
        System.out.println("════════════════════════════════════════════");
        System.out.println("Contraseña: " + passwordAdmin);
        System.out.println("────────────────────────────────────────────");
        System.out.println("HASH BCRYPT GENERADO:");
        System.out.println(hashGenerado);
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        System.out.println("📋 USA ESTE HASH EN bootstrap.sql:");
        System.out.println("INSERT INTO USUARIO (EMAIL, HASH_PASSWORD, NOMBRE, ROL, ACTIVO)");
        System.out.println("VALUES ('admin@inmo.test', '" + hashGenerado + "', 'Administrador', 'ADMIN', 'S');");
        System.out.println();
        System.out.println("✓ Cómo verificar que es correcto:");
        System.out.println("  - La contraseña se hashea diferente cada vez (normal en BCrypt)");
        System.out.println("  - Comienza con: $2a$ o $2b$");
        System.out.println("  - Tiene ~60 caracteres");
        
        // Verificar que el hash funciona
        boolean esValido = BCrypt.checkpw(passwordAdmin, hashGenerado);
        System.out.println();
        System.out.println("✓ Verificación: " + (esValido ? "CORRECTA" : "ERROR"));
    }
}