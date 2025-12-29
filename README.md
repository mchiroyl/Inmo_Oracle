# Sistema Inmobiliario - Aplicación de Escritorio Java/JavaFX

Sistema completo de gestión inmobiliaria que conecta compradores, vendedores y agentes para facilitar transacciones de bienes raíces.

## 📋 Características Principales

### Módulos Implementados

1. **Gestión de Usuarios**
   - Login con autenticación segura (BCrypt)
   - 4 roles: ADMIN, AGENTE, VENDEDOR, COMPRADOR
   - Control de acceso por permisos

2. **Gestión de Agentes**
   - Registro con antigüedad en empresa
   - Contador de inmuebles vendidos
   - Asignación a vendedores y compradores

3. **Gestión de Vendedores**
   - Vinculación con agentes
   - Administración de inmuebles
   - Respuesta a ofertas con contraofertas

4. **Gestión de Compradores**
   - Datos completos: nombre, apellido, dirección, teléfono, estado civil, nacionalidad, edad
   - Creación de ofertas sobre inmuebles
   - Seguimiento de contraofertas

5. **Gestión de Inmuebles**
   - Campos completos: dirección, tipo, precio, metraje, antigüedad, modelo, material
   - Condición: NUEVO, SEMI_NUEVO, USADO, REMODELADO
   - Estados: DISPONIBLE, EN_NEGOCIACION, VENDIDO, RETIRADO
   - Búsqueda avanzada por múltiples criterios

6. **Sistema de Ofertas y Contraofertas**
   - Ofertas con monto, forma de pago, tiempo
   - Priorización automática (efectivo y menor tiempo = mayor prioridad)
   - Contraofertas del vendedor
   - Notificación de estados
   - Flujo completo de negociación

7. **Sistema de Acuerdos**
   - Registro de ventas completadas
   - Actualización automática de contador de agente
   - Cambio de estado de inmueble a VENDIDO

8. **Consultas e Informes**
   - Inmuebles vendidos por agente
   - Personas a las que vendió un agente
   - Historial de ofertas por inmueble
   - Ofertas activas por comprador
   - Estadísticas de agentes
   - Búsqueda de inmuebles por criterios
   - Estado de contraofertas
   - Acuerdos completados
   - Top 5 inmuebles más caros
   - Compradores sin ofertas

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **JavaFX 21.0.4** (interfaz gráfica)
- **Hibernate 6.5.2** (ORM)
- **Oracle Database** (base de datos)
- **HikariCP 5.1.0** (pool de conexiones)
- **jBCrypt 0.4** (encriptación)
- **Maven** (gestión de dependencias)

## 📦 Estructura del Proyecto

```
inmo_oracle_app_fixed_2/
├── db/
│   └── bootstrap.sql          # Script completo de base de datos
├── src/
│   └── main/
│       ├── java/
│       │   └── com/inmo/
│       │       ├── app/       # Clase principal
│       │       ├── config/    # Configuración Hibernate
│       │       ├── dao/       # Acceso a datos
│       │       ├── domain/    # Entidades JPA
│       │       ├── dto/       # Objetos de transferencia
│       │       ├── security/  # Autenticación y permisos
│       │       ├── ui/        # Controladores JavaFX
│       │       └── util/      # Utilidades
│       └── resources/
│           ├── hibernate.cfg.xml    # Configuración Hibernate
│           └── ui/                  # Archivos FXML
├── pom.xml                    # Configuración Maven
└── README.md                  # Este archivo
```

## 🚀 Instalación y Configuración

### Requisitos Previos

1. **Java Development Kit (JDK) 21 o superior**
   - Descargar de: https://www.oracle.com/java/technologies/downloads/

2. **Oracle Database** (11g o superior)
   - Puedes usar Oracle XE (Express Edition)
   - O tener acceso a una instancia de Oracle

3. **Maven 3.8+**
   - Descargar de: https://maven.apache.org/download.cgi

4. **IDE recomendado**
   - IntelliJ IDEA, Eclipse, o NetBeans

### Paso 1: Configurar la Base de Datos

1. Conecta a Oracle como usuario con permisos de creación:

```bash
sqlplus system/password@localhost:1521/XEPDB1
```

2. Ejecuta el script de base de datos:

```sql
@D:\ruta\al\proyecto\db\bootstrap.sql
```

O copia y pega el contenido del archivo `db/bootstrap.sql` en SQL Developer o SQL*Plus.

El script creará:
- 8 tablas: USUARIO, AGENTE, VENDEDOR, COMPRADOR, INMUEBLE, OFERTA, CONTRAOFERTA, ACUERDO
- Triggers para auditoría y lógica de negocio
- Datos de prueba iniciales

### Paso 2: Configurar la Conexión

Edita el archivo `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:XEPDB1</property>
<property name="hibernate.connection.username">TU_USUARIO</property>
<property name="hibernate.connection.password">TU_PASSWORD</property>
```

Ajusta según tu configuración de Oracle.

### Paso 3: Compilar el Proyecto

En la terminal, desde la raíz del proyecto:

```bash
mvn clean install
```

### Paso 4: Ejecutar la Aplicación

```bash
mvn javafx:run
```

O desde tu IDE, ejecuta la clase `MainApp.java`.

## 👤 Usuarios de Prueba

El sistema incluye usuarios de prueba (todos con password: **123456**):

| Email | Rol | Descripción |
|-------|-----|-------------|
| admin@inmo.test | ADMIN | Administrador con acceso total |
| agente1@inmo.test | AGENTE | Juan Pérez (Agente #1) |
| vendedor1@inmo.test | VENDEDOR | María García (Vendedor #1) |
| comprador1@inmo.test | COMPRADOR | Carlos López (Comprador #1) |

## 📊 Consultas Disponibles

El módulo de consultas incluye:

### 1. Inmuebles vendidos por agente
Muestra todos los inmuebles que un agente ha vendido exitosamente.

**Parámetro:** ID del agente

**Ejemplo:** `1` (para Juan Pérez)

### 2. Personas a las que vendió un agente
Lista de compradores únicos que han comprado a través de un agente.

**Parámetro:** ID del agente

### 3. Historial de ofertas por inmueble
Todas las ofertas realizadas sobre un inmueble específico, ordenadas por prioridad.

**Parámetro:** ID del inmueble

### 4. Ofertas activas por comprador
Ofertas en estado PENDIENTE o CONTRAOFERTADA de un comprador.

**Parámetro:** ID del comprador

### 5. Estadísticas de agentes
Resumen de todos los agentes con antigüedad y cantidad vendida.

**Parámetro:** ninguno

### 6. Búsqueda de inmuebles por criterios
Filtro avanzado por tipo, precio, condición, etc.

**Parámetro:** Tipo (CASA, DEPARTAMENTO, TERRENO)

### 7. Estado de contraofertas
Lista de contraofertas por estado.

**Parámetro:** Estado (PENDIENTE, ACEPTADA, RECHAZADA)

### 8. Acuerdos completados
Todas las ventas exitosamente cerradas.

**Parámetro:** ninguno

### 9. Top 5 inmuebles más caros
Los 5 inmuebles disponibles con mayor precio.

**Parámetro:** ninguno

### 10. Compradores sin ofertas
Compradores registrados que no han realizado ninguna oferta.

**Parámetro:** ninguno

## 🔄 Flujo de Negociación

1. **Vendedor** registra un inmueble
2. **Comprador** busca inmuebles y realiza una oferta
3. Sistema calcula prioridad automática (efectivo = alta prioridad)
4. **Vendedor** recibe notificación y puede:
   - Aceptar la oferta
   - Rechazar la oferta
   - Crear una contraoferta
5. Si hay contraoferta, el **Comprador** puede aceptarla o rechazarla
6. Al aceptarse una oferta, se crea un **Acuerdo**
7. El agente completa el acuerdo
8. El contador de inmuebles vendidos del agente se incrementa automáticamente

## 🗄️ Estructura de Base de Datos

### Tablas Principales

- **USUARIO**: Autenticación y roles
- **AGENTE**: Agentes inmobiliarios
- **VENDEDOR**: Propietarios de inmuebles
- **COMPRADOR**: Clientes interesados
- **INMUEBLE**: Propiedades en venta
- **OFERTA**: Propuestas de compra
- **CONTRAOFERTA**: Respuestas de vendedores
- **ACUERDO**: Ventas completadas

### Triggers Implementados

1. **TRG_*_UPD**: Actualización automática de fechas
2. **TRG_OFERTA_PRIORIDAD**: Cálculo de prioridad de ofertas
3. **TRG_ACUERDO_CONTADOR**: Actualización de contador de ventas

## 🎯 Permisos y Roles

### ADMIN
- Acceso total al sistema
- Gestión de usuarios
- Visualización de todas las transacciones

### AGENTE
- Gestión de compradores y vendedores asignados
- Visualización de ofertas de su cartera
- Gestión de acuerdos

### VENDEDOR
- Gestión de sus inmuebles
- Visualización de ofertas sobre sus inmuebles
- Creación de contraofertas
- Aceptación/rechazo de ofertas

### COMPRADOR
- Búsqueda de inmuebles
- Creación de ofertas
- Visualización de sus ofertas
- Respuesta a contraofertas

## 🐛 Solución de Problemas

### Error de conexión a Oracle

```
Causa: No se puede conectar a la base de datos
Solución: Verifica que Oracle esté corriendo y las credenciales sean correctas
```

### Error al iniciar Hibernate

```
Causa: Configuración incorrecta en hibernate.cfg.xml
Solución: Verifica la URL, usuario y contraseña de la base de datos
```

### Tablas no encontradas

```
Causa: Script de base de datos no ejecutado
Solución: Ejecuta db/bootstrap.sql en tu instancia de Oracle
```

## 📝 Notas de Desarrollo

- La aplicación usa Hibernate en modo `update`, por lo que las entidades se sincronizan automáticamente
- Los triggers de Oracle manejan la lógica de negocio crítica
- El sistema de prioridades de ofertas funciona automáticamente:
  - Efectivo + 1 mes = Prioridad 1 (máxima)
  - Financiamiento + 24 meses = Prioridad 10 (mínima)

## 📄 Requisitos del Proyecto Académico

Este proyecto cumple con:

✅ **FASE 1**: Diagrama Entidad-Relación completo
✅ **FASE 2**: Prototipo funcional con CRUD completo
✅ **FASE FINAL**:
   - Informes implementados
   - 10 consultas funcionales
   - Sistema de ofertas/contraofertas
   - Sistema de acuerdos
   - Búsqueda avanzada
   - Notificaciones de estado

## 🤝 Créditos

- **Desarrollo**: Sistema Inmobiliario UMG
- **Curso**: Base de Datos I
- **Tecnología**: JavaFX + Hibernate + Oracle

## 📞 Soporte

Para problemas o preguntas:
1. Revisa la sección de "Solución de Problemas"
2. Verifica que todos los requisitos estén instalados
3. Consulta los logs en la consola de la aplicación

---

**Última actualización**: Diciembre 2025

**Versión**: 1.0.0-FINAL

## 🔒 Consideraciones de Seguridad

- Las contraseñas se almacenan con hash BCrypt (no reversible)
- Se recomienda cambiar las credenciales de base de datos en `hibernate.cfg.xml` antes de desplegar en producción
- Para producción, considerar usar variables de entorno para credenciales sensibles
- El pool de conexiones está limitado a 5 conexiones máximas con HikariCP
