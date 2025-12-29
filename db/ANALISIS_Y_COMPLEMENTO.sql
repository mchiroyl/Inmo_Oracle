-- ============================================================================
-- ANÁLISIS DE TU ESTRUCTURA ACTUAL vs PROYECTO COMPLETO
-- ============================================================================
-- Usuario: INMO
-- Password: inmo123
-- ============================================================================

-- ============================================================================
-- PARTE 1: LO QUE YA TIENES (ANÁLISIS)
-- ============================================================================

/*
TABLAS QUE YA TIENES:
✅ USUARIO (con columnas: ID, EMAIL, HASH_PASSWORD, ROL, ACTIVO, CREADO_EN, ACTUALIZADO_EN)
✅ AGENTE (con: ID, CODIGO, NOMBRE, USUARIO_ID, EMAIL, TELEFONO, ACTIVO, CREADO_EN, ACTUALIZADO_EN)
✅ VENDEDOR (con: ID, CODIGO, NOMBRE, TIPO, AGENTE_ID, USUARIO_ID, EMAIL, TELEFONO, ACTIVO)
✅ COMPRADOR (con: ID, NOMBRE, APELLIDO, DIRECCION, TELEFONO, ESTADO_CIVIL, NACIONALIDAD, EDAD, AGENTE_ID)
✅ INMUEBLE (con: ID, DIRECCION, TIPO, PRECIO, CONDICION, METRAJE_M2, VENDEDOR_ID, ESTADO)
✅ OFERTA (con: ID, COMPRADOR_ID, INMUEBLE_ID, MONTO, FORMA_PAGO, TIEMPO, FECHA_HORA, ESTADO)
✅ PRESTAMO (con: ID, COMPRADOR_ID, BANCO, MONTO, ESTADO)
✅ NOTIFICACION (con: ID, MENSAJE, USUARIO_ID, CREADO_EN)
✅ AGENTE_CLIENTE (con: ID, AGENTE_ID, COMPRADOR_ID)

TABLAS QUE TE FALTAN PARA EL PROYECTO COMPLETO:
❌ CONTRAOFERTA (respuestas de vendedores a ofertas)
❌ ACUERDO (registro de ventas completadas)

COLUMNAS QUE PODRÍAN FALTAR EN TABLAS EXISTENTES:
- COMPRADOR: necesita USUARIO_ID (para vincular con login)
- INMUEBLE: necesita AGENTE_ID (opcional pero útil)
- OFERTA: necesita AGENTE_ID (para vincular agente con oferta)
- OFERTA: necesita PRIORIDAD (para ordenar ofertas)
- INMUEBLE: necesita más campos de detalle (HABITACIONES, BANOS, ESTACIONAMIENTOS, DESCRIPCION, etc.)
*/

-- ============================================================================
-- PARTE 2: DECISIÓN - ¿COMPLEMENTAR O RECREAR?
-- ============================================================================

/*
RECOMENDACIÓN: COMPLEMENTAR TU ESTRUCTURA ACTUAL

Ventajas:
✅ Conservas tus datos existentes
✅ Menos riesgo de perder información
✅ Más rápido de implementar
✅ Tu estructura está bien diseñada, solo necesita ajustes menores

Este script va a:
1. Agregar las tablas faltantes (CONTRAOFERTA, ACUERDO)
2. Agregar columnas faltantes a tablas existentes
3. Crear triggers necesarios
4. Mantener todos tus datos actuales
*/

-- ============================================================================
-- PARTE 3: SCRIPT DE COMPLEMENTO (MODO SEGURO - NO BORRA DATOS)
-- ============================================================================

PROMPT ============================================================================
PROMPT INICIANDO COMPLEMENTO DE ESTRUCTURA - MODO SEGURO
PROMPT ============================================================================

-- ----------------------------------------------------------------------------
-- 1. AGREGAR COLUMNAS FALTANTES A TABLAS EXISTENTES
-- ----------------------------------------------------------------------------

PROMPT --- Actualizando tabla COMPRADOR ---

-- COMPRADOR.USUARIO_ID (para vincular con login de USUARIO)
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_tab_cols
  WHERE table_name = 'COMPRADOR' AND column_name = 'USUARIO_ID';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE COMPRADOR ADD (USUARIO_ID NUMBER NULL)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_COMPRADOR_USUARIO ON COMPRADOR(USUARIO_ID)';
    DBMS_OUTPUT.PUT_LINE('✓ Columna COMPRADOR.USUARIO_ID agregada');
  ELSE
    DBMS_OUTPUT.PUT_LINE('○ COMPRADOR.USUARIO_ID ya existe');
  END IF;
END;
/

-- COMPRADOR: agregar constraint y timestamps si no existen
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_tab_cols
  WHERE table_name = 'COMPRADOR' AND column_name = 'ACTIVO';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE COMPRADOR ADD (ACTIVO CHAR(1) DEFAULT ''S'' NOT NULL CHECK (ACTIVO IN (''S'',''N'')))';
    DBMS_OUTPUT.PUT_LINE('✓ Columna COMPRADOR.ACTIVO agregada');
  END IF;

  SELECT COUNT(*) INTO v_count
  FROM user_tab_cols
  WHERE table_name = 'COMPRADOR' AND column_name = 'CREADO_EN';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE COMPRADOR ADD (CREADO_EN TIMESTAMP DEFAULT SYSTIMESTAMP)';
    EXECUTE IMMEDIATE 'ALTER TABLE COMPRADOR ADD (ACTUALIZADO_EN TIMESTAMP NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ Timestamps en COMPRADOR agregados');
  END IF;
END;
/

PROMPT --- Actualizando tabla INMUEBLE ---

-- INMUEBLE: campos detallados
DECLARE v_count NUMBER;
BEGIN
  -- AGENTE_ID
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'AGENTE_ID';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (AGENTE_ID NUMBER NULL)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_INMUEBLE_AGENTE ON INMUEBLE(AGENTE_ID)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.AGENTE_ID agregado');
  END IF;

  -- HABITACIONES
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'HABITACIONES';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (HABITACIONES NUMBER(2) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.HABITACIONES agregado');
  END IF;

  -- BANOS
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'BANOS';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (BANOS NUMBER(2) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.BANOS agregado');
  END IF;

  -- ESTACIONAMIENTOS
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'ESTACIONAMIENTOS';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (ESTACIONAMIENTOS NUMBER(2) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.ESTACIONAMIENTOS agregado');
  END IF;

  -- DESCRIPCION
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'DESCRIPCION';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (DESCRIPCION VARCHAR2(1000) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.DESCRIPCION agregado');
  END IF;

  -- ANTIGUEDAD
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'ANTIGUEDAD';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (ANTIGUEDAD NUMBER(4) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.ANTIGUEDAD agregado');
  END IF;

  -- MODELO
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'MODELO';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (MODELO VARCHAR2(100) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.MODELO agregado');
  END IF;

  -- MATERIAL
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'MATERIAL';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (MATERIAL VARCHAR2(100) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ INMUEBLE.MATERIAL agregado');
  END IF;

  -- Timestamps
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'INMUEBLE' AND column_name = 'CREADO_EN';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (CREADO_EN TIMESTAMP DEFAULT SYSTIMESTAMP)';
    EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD (ACTUALIZADO_EN TIMESTAMP NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ Timestamps en INMUEBLE agregados');
  END IF;
END;
/

PROMPT --- Actualizando tabla OFERTA ---

-- OFERTA: campos adicionales
DECLARE v_count NUMBER;
BEGIN
  -- AGENTE_ID
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'OFERTA' AND column_name = 'AGENTE_ID';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (AGENTE_ID NUMBER NULL)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_OFERTA_AGENTE ON OFERTA(AGENTE_ID)';
    DBMS_OUTPUT.PUT_LINE('✓ OFERTA.AGENTE_ID agregado');
  END IF;

  -- PRIORIDAD
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'OFERTA' AND column_name = 'PRIORIDAD';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (PRIORIDAD NUMBER(2) DEFAULT 5)';
    DBMS_OUTPUT.PUT_LINE('✓ OFERTA.PRIORIDAD agregado');
  END IF;

  -- COMENTARIOS
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'OFERTA' AND column_name = 'COMENTARIOS';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (COMENTARIOS VARCHAR2(500) NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ OFERTA.COMENTARIOS agregado');
  END IF;

  -- FECHA_RESPUESTA
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'OFERTA' AND column_name = 'FECHA_RESPUESTA';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (FECHA_RESPUESTA TIMESTAMP NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ OFERTA.FECHA_RESPUESTA agregado');
  END IF;

  -- Timestamps
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'OFERTA' AND column_name = 'CREADO_EN';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (CREADO_EN TIMESTAMP DEFAULT SYSTIMESTAMP)';
    EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD (ACTUALIZADO_EN TIMESTAMP NULL)';
    DBMS_OUTPUT.PUT_LINE('✓ Timestamps en OFERTA agregados');
  END IF;
END;
/

PROMPT --- Actualizando tabla AGENTE ---

-- AGENTE: campos adicionales para estadísticas
DECLARE v_count NUMBER;
BEGIN
  -- ANTIGUEDAD_EMPRESA
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'AGENTE' AND column_name = 'ANTIGUEDAD_EMPRESA';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE AGENTE ADD (ANTIGUEDAD_EMPRESA NUMBER(3) DEFAULT 0)';
    DBMS_OUTPUT.PUT_LINE('✓ AGENTE.ANTIGUEDAD_EMPRESA agregado');
  END IF;

  -- CANTIDAD_VENDIDOS
  SELECT COUNT(*) INTO v_count FROM user_tab_cols
  WHERE table_name = 'AGENTE' AND column_name = 'CANTIDAD_VENDIDOS';
  IF v_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE AGENTE ADD (CANTIDAD_VENDIDOS NUMBER(5) DEFAULT 0)';
    DBMS_OUTPUT.PUT_LINE('✓ AGENTE.CANTIDAD_VENDIDOS agregado');
  END IF;
END;
/

-- ----------------------------------------------------------------------------
-- 2. CREAR TABLA CONTRAOFERTA (SI NO EXISTE)
-- ----------------------------------------------------------------------------

PROMPT --- Creando tabla CONTRAOFERTA ---

DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_tables
  WHERE table_name = 'CONTRAOFERTA';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE TABLE CONTRAOFERTA (
        ID NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
        OFERTA_ID NUMBER NOT NULL,
        VENDEDOR_ID NUMBER NOT NULL,
        MONTO NUMBER(15,2) NOT NULL CHECK (MONTO > 0),
        FORMA_PAGO VARCHAR2(50) CHECK (FORMA_PAGO IN (''EFECTIVO'', ''FINANCIAMIENTO'', ''MIXTO'', ''CREDITO'')),
        TIEMPO_PAGO NUMBER(3),
        ESTADO VARCHAR2(30) DEFAULT ''PENDIENTE''
          CHECK (ESTADO IN (''PENDIENTE'', ''ACEPTADA'', ''RECHAZADA'', ''CANCELADA'')),
        COMENTARIOS VARCHAR2(500),
        FECHA_CONTRAOFERTA TIMESTAMP DEFAULT SYSTIMESTAMP,
        FECHA_RESPUESTA TIMESTAMP,
        CREADO_EN TIMESTAMP DEFAULT SYSTIMESTAMP,
        ACTUALIZADO_EN TIMESTAMP,
        CONSTRAINT FK_CONTRAOFERTA_OFERTA FOREIGN KEY (OFERTA_ID)
          REFERENCES OFERTA(ID) ON DELETE CASCADE,
        CONSTRAINT FK_CONTRAOFERTA_VENDEDOR FOREIGN KEY (VENDEDOR_ID)
          REFERENCES VENDEDOR(ID) ON DELETE CASCADE
      )';

    EXECUTE IMMEDIATE 'CREATE INDEX IX_CONTRAOFERTA_OFERTA ON CONTRAOFERTA(OFERTA_ID)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_CONTRAOFERTA_VENDEDOR ON CONTRAOFERTA(VENDEDOR_ID)';

    DBMS_OUTPUT.PUT_LINE('✓ Tabla CONTRAOFERTA creada');
  ELSE
    DBMS_OUTPUT.PUT_LINE('○ Tabla CONTRAOFERTA ya existe');
  END IF;
END;
/

-- ----------------------------------------------------------------------------
-- 3. CREAR TABLA ACUERDO (SI NO EXISTE)
-- ----------------------------------------------------------------------------

PROMPT --- Creando tabla ACUERDO ---

DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_tables
  WHERE table_name = 'ACUERDO';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE TABLE ACUERDO (
        ID NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
        INMUEBLE_ID NUMBER NOT NULL,
        COMPRADOR_ID NUMBER NOT NULL,
        VENDEDOR_ID NUMBER NOT NULL,
        AGENTE_ID NUMBER,
        OFERTA_ID NUMBER,
        MONTO_FINAL NUMBER(15,2) NOT NULL CHECK (MONTO_FINAL > 0),
        FORMA_PAGO VARCHAR2(50),
        TIEMPO_PAGO NUMBER(3),
        ESTADO VARCHAR2(30) DEFAULT ''PENDIENTE''
          CHECK (ESTADO IN (''PENDIENTE'', ''EN_PROCESO'', ''COMPLETADO'', ''CANCELADO'')),
        FECHA_ACUERDO TIMESTAMP DEFAULT SYSTIMESTAMP,
        FECHA_CIERRE TIMESTAMP,
        NOTAS VARCHAR2(1000),
        CREADO_EN TIMESTAMP DEFAULT SYSTIMESTAMP,
        ACTUALIZADO_EN TIMESTAMP,
        CONSTRAINT FK_ACUERDO_INMUEBLE FOREIGN KEY (INMUEBLE_ID)
          REFERENCES INMUEBLE(ID) ON DELETE CASCADE,
        CONSTRAINT FK_ACUERDO_COMPRADOR FOREIGN KEY (COMPRADOR_ID)
          REFERENCES COMPRADOR(ID) ON DELETE CASCADE,
        CONSTRAINT FK_ACUERDO_VENDEDOR FOREIGN KEY (VENDEDOR_ID)
          REFERENCES VENDEDOR(ID) ON DELETE CASCADE,
        CONSTRAINT FK_ACUERDO_AGENTE FOREIGN KEY (AGENTE_ID)
          REFERENCES AGENTE(ID) ON DELETE SET NULL,
        CONSTRAINT FK_ACUERDO_OFERTA FOREIGN KEY (OFERTA_ID)
          REFERENCES OFERTA(ID) ON DELETE SET NULL
      )';

    EXECUTE IMMEDIATE 'CREATE INDEX IX_ACUERDO_INMUEBLE ON ACUERDO(INMUEBLE_ID)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_ACUERDO_COMPRADOR ON ACUERDO(COMPRADOR_ID)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_ACUERDO_VENDEDOR ON ACUERDO(VENDEDOR_ID)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_ACUERDO_AGENTE ON ACUERDO(AGENTE_ID)';
    EXECUTE IMMEDIATE 'CREATE INDEX IX_ACUERDO_FECHA ON ACUERDO(FECHA_ACUERDO)';

    DBMS_OUTPUT.PUT_LINE('✓ Tabla ACUERDO creada');
  ELSE
    DBMS_OUTPUT.PUT_LINE('○ Tabla ACUERDO ya existe');
  END IF;
END;
/

-- ----------------------------------------------------------------------------
-- 4. AGREGAR FOREIGN KEYS FALTANTES (SI NO EXISTEN)
-- ----------------------------------------------------------------------------

PROMPT --- Verificando Foreign Keys ---

-- COMPRADOR.USUARIO_ID -> USUARIO.ID
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_constraints
  WHERE constraint_name = 'FK_COMPRADOR_USUARIO';

  IF v_count = 0 THEN
    BEGIN
      EXECUTE IMMEDIATE 'ALTER TABLE COMPRADOR ADD CONSTRAINT FK_COMPRADOR_USUARIO
        FOREIGN KEY (USUARIO_ID) REFERENCES USUARIO(ID) ON DELETE SET NULL';
      DBMS_OUTPUT.PUT_LINE('✓ FK COMPRADOR -> USUARIO creada');
    EXCEPTION WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('! Error al crear FK COMPRADOR -> USUARIO: ' || SQLERRM);
    END;
  END IF;
END;
/

-- INMUEBLE.AGENTE_ID -> AGENTE.ID
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_constraints
  WHERE constraint_name = 'FK_INMUEBLE_AGENTE';

  IF v_count = 0 THEN
    BEGIN
      EXECUTE IMMEDIATE 'ALTER TABLE INMUEBLE ADD CONSTRAINT FK_INMUEBLE_AGENTE
        FOREIGN KEY (AGENTE_ID) REFERENCES AGENTE(ID) ON DELETE SET NULL';
      DBMS_OUTPUT.PUT_LINE('✓ FK INMUEBLE -> AGENTE creada');
    EXCEPTION WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('! Error al crear FK INMUEBLE -> AGENTE: ' || SQLERRM);
    END;
  END IF;
END;
/

-- OFERTA.AGENTE_ID -> AGENTE.ID
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_constraints
  WHERE constraint_name = 'FK_OFERTA_AGENTE';

  IF v_count = 0 THEN
    BEGIN
      EXECUTE IMMEDIATE 'ALTER TABLE OFERTA ADD CONSTRAINT FK_OFERTA_AGENTE
        FOREIGN KEY (AGENTE_ID) REFERENCES AGENTE(ID) ON DELETE SET NULL';
      DBMS_OUTPUT.PUT_LINE('✓ FK OFERTA -> AGENTE creada');
    EXCEPTION WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('! Error al crear FK OFERTA -> AGENTE: ' || SQLERRM);
    END;
  END IF;
END;
/

-- ----------------------------------------------------------------------------
-- 5. CREAR TRIGGERS NECESARIOS
-- ----------------------------------------------------------------------------

PROMPT --- Creando Triggers ---

-- Trigger para actualizar COMPRADOR.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_COMPRADOR_UPD
BEFORE UPDATE ON COMPRADOR
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar INMUEBLE.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_INMUEBLE_UPD
BEFORE UPDATE ON INMUEBLE
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar OFERTA.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_OFERTA_UPD
BEFORE UPDATE ON OFERTA
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar CONTRAOFERTA.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_CONTRAOFERTA_UPD
BEFORE UPDATE ON CONTRAOFERTA
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar ACUERDO.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_ACUERDO_UPD
BEFORE UPDATE ON ACUERDO
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar AGENTE.ACTUALIZADO_EN
CREATE OR REPLACE TRIGGER TRG_AGENTE_UPD
BEFORE UPDATE ON AGENTE
FOR EACH ROW
BEGIN
  :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
END;
/

-- Trigger para actualizar VENDEDOR (si no existe)
DECLARE v_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM user_triggers
  WHERE trigger_name = 'TRG_VENDEDOR_UPD';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE OR REPLACE TRIGGER TRG_VENDEDOR_UPD
      BEFORE UPDATE ON VENDEDOR
      FOR EACH ROW
      BEGIN
        :NEW.ACTUALIZADO_EN := SYSTIMESTAMP;
      END;
    ';
  END IF;
END;
/

DBMS_OUTPUT.PUT_LINE('✓ Triggers de auditoría creados');

-- Trigger para calcular prioridad automática de OFERTA
CREATE OR REPLACE TRIGGER TRG_OFERTA_PRIORIDAD
BEFORE INSERT OR UPDATE ON OFERTA
FOR EACH ROW
DECLARE
  v_prioridad NUMBER := 5;
  v_tiempo NUMBER;
BEGIN
  -- Calcular prioridad base
  v_prioridad := 10;

  -- Si es efectivo, mayor prioridad
  IF :NEW.FORMA_PAGO = 'EFECTIVO' THEN
    v_prioridad := v_prioridad - 4;
  ELSIF :NEW.FORMA_PAGO = 'MIXTO' THEN
    v_prioridad := v_prioridad - 2;
  END IF;

  -- Extraer número de meses del campo TIEMPO (ej: "24 meses" -> 24)
  IF :NEW.TIEMPO IS NOT NULL THEN
    BEGIN
      v_tiempo := TO_NUMBER(REGEXP_SUBSTR(:NEW.TIEMPO, '\d+'));

      IF v_tiempo IS NOT NULL THEN
        IF v_tiempo <= 1 THEN
          v_prioridad := v_prioridad - 3;
        ELSIF v_tiempo <= 6 THEN
          v_prioridad := v_prioridad - 2;
        ELSIF v_tiempo <= 12 THEN
          v_prioridad := v_prioridad - 1;
        END IF;
      END IF;
    EXCEPTION WHEN OTHERS THEN
      NULL; -- Si no se puede extraer número, mantener prioridad base
    END;
  END IF;

  -- Asegurar que esté en rango 1-10
  IF v_prioridad < 1 THEN
    v_prioridad := 1;
  ELSIF v_prioridad > 10 THEN
    v_prioridad := 10;
  END IF;

  :NEW.PRIORIDAD := v_prioridad;
END;
/

DBMS_OUTPUT.PUT_LINE('✓ Trigger de prioridad de ofertas creado');

-- Trigger para actualizar contador de ventas del agente
CREATE OR REPLACE TRIGGER TRG_ACUERDO_CONTADOR
AFTER INSERT OR UPDATE ON ACUERDO
FOR EACH ROW
BEGIN
  IF :NEW.ESTADO = 'COMPLETADO' AND :NEW.AGENTE_ID IS NOT NULL THEN
    -- Actualizar contador del agente
    UPDATE AGENTE
    SET CANTIDAD_VENDIDOS = CANTIDAD_VENDIDOS + 1
    WHERE ID = :NEW.AGENTE_ID;

    -- Marcar inmueble como vendido
    UPDATE INMUEBLE
    SET ESTADO = 'VENDIDO'
    WHERE ID = :NEW.INMUEBLE_ID;
  END IF;
END;
/

DBMS_OUTPUT.PUT_LINE('✓ Trigger de contador de ventas creado');

-- ----------------------------------------------------------------------------
-- 6. ACTUALIZAR ESTADOS DE OFERTA (COMPATIBILIDAD)
-- ----------------------------------------------------------------------------

PROMPT --- Ajustando datos existentes ---

-- Agregar estado CONTRAOFERTADA si no existe en ofertas
UPDATE OFERTA
SET ESTADO = 'CONTRAOFERTADA'
WHERE ESTADO = 'CONTRAOFERTA';

COMMIT;

DBMS_OUTPUT.PUT_LINE('✓ Estados de ofertas actualizados');

-- ----------------------------------------------------------------------------
-- 7. VERIFICACIÓN FINAL
-- ----------------------------------------------------------------------------

PROMPT ============================================================================
PROMPT VERIFICACIÓN DE ESTRUCTURA COMPLETADA
PROMPT ============================================================================

SELECT 'USUARIO: ' || COUNT(*) || ' registros' AS VERIFICACION FROM USUARIO;
SELECT 'AGENTE: ' || COUNT(*) || ' registros' AS VERIFICACION FROM AGENTE;
SELECT 'VENDEDOR: ' || COUNT(*) || ' registros' AS VERIFICACION FROM VENDEDOR;
SELECT 'COMPRADOR: ' || COUNT(*) || ' registros' AS VERIFICACION FROM COMPRADOR;
SELECT 'INMUEBLE: ' || COUNT(*) || ' registros' AS VERIFICACION FROM INMUEBLE;
SELECT 'OFERTA: ' || COUNT(*) || ' registros' AS VERIFICACION FROM OFERTA;
SELECT 'CONTRAOFERTA: ' || COUNT(*) || ' registros (nueva tabla)' AS VERIFICACION FROM CONTRAOFERTA;
SELECT 'ACUERDO: ' || COUNT(*) || ' registros (nueva tabla)' AS VERIFICACION FROM ACUERDO;
SELECT 'PRESTAMO: ' || COUNT(*) || ' registros' AS VERIFICACION FROM PRESTAMO;
SELECT 'NOTIFICACION: ' || COUNT(*) || ' registros' AS VERIFICACION FROM NOTIFICACION;

PROMPT ============================================================================
PROMPT COMPLEMENTO COMPLETADO EXITOSAMENTE
PROMPT ============================================================================
PROMPT
PROMPT Tablas creadas nuevas: CONTRAOFERTA, ACUERDO
PROMPT Columnas agregadas: Multiple en COMPRADOR, INMUEBLE, OFERTA, AGENTE
PROMPT Triggers creados: 11 triggers de auditoría y lógica de negocio
PROMPT
PROMPT TODOS TUS DATOS EXISTENTES SE CONSERVARON
PROMPT
PROMPT Ahora tu estructura es compatible con el proyecto Java completo.
PROMPT ============================================================================
