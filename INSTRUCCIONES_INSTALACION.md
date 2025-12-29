# 📋 INSTRUCCIONES DE INSTALACIÓN - Sistema Inmobiliario

## ⚠️ IMPORTANTE - LEER ANTES DE EMPEZAR

Este documento contiene las instrucciones **PASO A PASO** para instalar y ejecutar el proyecto sin problemas.

---

## 🎯 MÉTODO RÁPIDO (Recomendado)

### Opción A: Instalar TODO desde cero (Usuario INMO nuevo)

Si quieres empezar completamente desde cero, sigue estos pasos:

#### 1️⃣ Conectarse como SYSTEM/SYSDBA

```bash
sqlplus system/tu_password@localhost:1521/XEPDB1
```

O usando SQL Developer como usuario SYSTEM.

#### 2️⃣ Ejecutar el script completo

```sql
@D:\2_UMG\6to._Semestre\Base_De_Datos_I\inmo_oracle_app_fixed_2\db\INSTALAR_COMPLETO.sql
```

Este script hará **TODO AUTOMÁTICAMENTE**:
- ✅ Eliminar usuario INMO si existe
- ✅ Crear usuario INMO con password: inmo123
- ✅ Otorgar todos los permisos necesarios
- ✅ Crear las 8 tablas
- ✅ Crear los 11 triggers
- ✅ Insertar datos de prueba
- ✅ Verificar la instalación

**¡LISTO! Con este solo paso ya tienes todo configurado.**

---

### Opción B: Si el usuario INMO ya existe

Si ya tienes el usuario INMO creado y solo quieres recrear las tablas:

#### 1️⃣ Conectarse como usuario INMO

```bash
sqlplus INMO/inmo123@localhost:1521/XEPDB1
```

#### 2️⃣ Ejecutar el script bootstrap.sql

```sql
@D:\2_UMG\6to._Semestre\Base_De_Datos_I\inmo_oracle_app_fixed_2\db\bootstrap.sql
```

---

## 🔧 CONFIGURACIÓN DEL PROYECTO JAVA

### 1️⃣ Verificar hibernate.cfg.xml

El archivo `src/main/resources/hibernate.cfg.xml` ya está configurado correctamente:

```xml
<property name="hibernate.connection.url">jdbc:oracle:thin:@//localhost:1521/XEPDB1</property>
<property name="hibernate.connection.username">INMO</property>
<property name="hibernate.connection.password">inmo123</property>
```

**Si tu Oracle está en otro puerto o instancia, modifica la URL.**

Ejemplos:
- Oracle XE (puerto 1521): `jdbc:oracle:thin:@//localhost:1521/XEPDB1`
- Oracle Standard (puerto 1521): `jdbc:oracle:thin:@//localhost:1521/ORCL`
- Oracle con SID: `jdbc:oracle:thin:@localhost:1521:XE`

### 2️⃣ Verificar que todas las entidades están mapeadas

El archivo `hibernate.cfg.xml` ya incluye todas las entidades:

```xml
<mapping class="com.inmo.domain.Usuario"/>
<mapping class="com.inmo.domain.Agente"/>
<mapping class="com.inmo.domain.Vendedor"/>
<mapping class="com.inmo.domain.Comprador"/>
<mapping class="com.inmo.domain.Inmueble"/>
<mapping class="com.inmo.domain.Oferta"/>
<mapping class="com.inmo.domain.Contraoferta"/>
<mapping class="com.inmo.domain.Acuerdo"/>
```

---

## 🚀 COMPILAR Y EJECUTAR

### 1️⃣ Abrir terminal en la carpeta del proyecto

```bash
cd D:\2_UMG\6to._Semestre\Base_De_Datos_I\inmo_oracle_app_fixed_2
```

### 2️⃣ Compilar el proyecto

```bash
mvn clean compile
```

**Debe decir: BUILD SUCCESS**

### 3️⃣ Ejecutar la aplicación

```bash
mvn javafx:run
```

---

## 👤 USUARIOS PARA PROBAR

Una vez que la aplicación se abra, puedes loguearte con estos usuarios:

| Email | Password | Rol | Descripción |
|-------|----------|-----|-------------|
| admin@inmo.test | 123456 | ADMIN | Administrador - acceso total |
| agente1@inmo.test | 123456 | AGENTE | Juan Pérez - agente inmobiliario |
| vendedor1@inmo.test | 123456 | VENDEDOR | María García - vendedor |
| comprador1@inmo.test | 123456 | COMPRADOR | Carlos López - comprador |

---

## 📊 VERIFICAR QUE TODO ESTÉ CORRECTO

### En Oracle (SQL*Plus o SQL Developer)

Conectarse como INMO:

```sql
-- Ver todas las tablas
SELECT table_name FROM user_tables ORDER BY table_name;

-- Debe mostrar:
-- ACUERDO
-- AGENTE
-- COMPRADOR
-- CONTRAOFERTA
-- INMUEBLE
-- OFERTA
-- USUARIO
-- VENDEDOR

-- Verificar datos
SELECT COUNT(*) FROM USUARIO;    -- Debe ser 4
SELECT COUNT(*) FROM AGENTE;     -- Debe ser 2
SELECT COUNT(*) FROM VENDEDOR;   -- Debe ser 2
SELECT COUNT(*) FROM COMPRADOR;  -- Debe ser 3
SELECT COUNT(*) FROM INMUEBLE;   -- Debe ser 5
SELECT COUNT(*) FROM OFERTA;     -- Debe ser 3

-- Verificar triggers
SELECT trigger_name FROM user_triggers ORDER BY trigger_name;
-- Debe mostrar 11 triggers
```

---

## 🔍 SOLUCIÓN DE PROBLEMAS COMUNES

### ❌ Error: "ORA-01017: invalid username/password"

**Causa:** El usuario INMO no existe o la contraseña es incorrecta.

**Solución:**
1. Ejecutar el script INSTALAR_COMPLETO.sql como SYSTEM
2. O crear el usuario manualmente:

```sql
-- Como SYSTEM
CREATE USER INMO IDENTIFIED BY inmo123;
GRANT CONNECT, RESOURCE TO INMO;
GRANT UNLIMITED TABLESPACE TO INMO;
```

---

### ❌ Error: "Table or view does not exist"

**Causa:** Las tablas no se han creado.

**Solución:**
```sql
-- Conectar como INMO
sqlplus INMO/inmo123@localhost:1521/XEPDB1

-- Ejecutar el script
@D:\ruta\db\INSTALAR_COMPLETO.sql
```

---

### ❌ Error al compilar: "cannot find symbol Access.can"

**Causa:** El archivo Access.java no tiene el método actualizado.

**Solución:** Ya está corregido en la última versión. Hacer:

```bash
mvn clean compile
```

---

### ❌ Error: "Could not connect to Oracle database"

**Posibles causas:**
1. Oracle no está corriendo
2. Puerto incorrecto
3. Instancia/SID incorrecta

**Solución:**

1. Verificar que Oracle esté corriendo:
   - Windows: Services → Oracle services deben estar "Running"
   - Linux: `ps -ef | grep oracle`

2. Verificar puerto y servicio:
   ```sql
   -- Como SYSDBA
   SELECT name, value FROM v$parameter WHERE name = 'service_names';
   SELECT name, value FROM v$parameter WHERE name = 'local_listener';
   ```

3. Probar conexión con SQL*Plus:
   ```bash
   sqlplus INMO/inmo123@localhost:1521/XEPDB1
   ```

4. Si no funciona, ajustar la URL en hibernate.cfg.xml

---

### ❌ La aplicación se cierra inmediatamente

**Causa:** Error en la configuración de Hibernate o base de datos.

**Solución:**

1. Revisar la consola para ver el error
2. Verificar que hibernate.cfg.xml tenga las credenciales correctas
3. Probar conexión manual:
   ```bash
   sqlplus INMO/inmo123@localhost:1521/XEPDB1
   ```

---

## 📝 CHECKLIST ANTES DE EJECUTAR

Marca cada paso conforme lo completes:

- [ ] Oracle Database está instalado y corriendo
- [ ] Script INSTALAR_COMPLETO.sql ejecutado exitosamente
- [ ] Usuario INMO creado con password: inmo123
- [ ] 8 tablas creadas (verificado con SELECT table_name FROM user_tables)
- [ ] Datos de prueba insertados (4 usuarios, 2 agentes, etc.)
- [ ] Java 21 instalado
- [ ] Maven instalado y en el PATH
- [ ] hibernate.cfg.xml tiene las credenciales correctas
- [ ] mvn clean compile ejecutado sin errores (BUILD SUCCESS)
- [ ] mvn javafx:run ejecuta la aplicación

---

## 🎯 RESUMEN EJECUTIVO

### Para instalar desde CERO:

```bash
# 1. Conectar como SYSTEM
sqlplus system/password@localhost:1521/XEPDB1

# 2. Ejecutar script
@D:\2_UMG\6to._Semestre\Base_De_Datos_I\inmo_oracle_app_fixed_2\db\INSTALAR_COMPLETO.sql

# 3. Salir de SQL*Plus
exit

# 4. Compilar proyecto
cd D:\2_UMG\6to._Semestre\Base_De_Datos_I\inmo_oracle_app_fixed_2
mvn clean compile

# 5. Ejecutar aplicación
mvn javafx:run

# 6. Login con: admin@inmo.test / 123456
```

---

## 📞 ¿NECESITAS AYUDA?

Si después de seguir todos los pasos sigues teniendo problemas:

1. Revisa los logs de la consola
2. Verifica que Oracle esté corriendo
3. Verifica que puedas conectarte con SQL*Plus manualmente
4. Revisa que hibernate.cfg.xml tenga las credenciales correctas

---

## ✅ CONFIRMACIÓN FINAL

Si todo está correcto, deberías poder:

1. ✅ Ejecutar `mvn clean compile` sin errores
2. ✅ Ejecutar `mvn javafx:run` y ver la ventana de login
3. ✅ Loguearte con admin@inmo.test / 123456
4. ✅ Ver el menú principal con todas las opciones
5. ✅ Navegar por Usuarios, Agentes, Vendedores, Compradores, Inmuebles, Ofertas
6. ✅ Ejecutar consultas e informes

**¡Si puedes hacer todo esto, el sistema está 100% funcional!**

---

**Última actualización:** Noviembre 2, 2025
**Versión del proyecto:** 1.0.0-FINAL
**Estado:** COMPLETADO Y PROBADO
