package com.inmo.ui;

import com.inmo.config.HibernateUtil;
import com.inmo.security.Access;
import com.inmo.security.Session;
import com.inmo.domain.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ConsultasController {

    @FXML private ComboBox<String> cboTipoConsulta;
    @FXML private TextField txtParametro;
    @FXML private Button btnEjecutar;
    @FXML private TableView<Map<String, Object>> tableResultados;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblTitulo;

    private ObservableList<Map<String, Object>> dataResultados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Usuario usuario = Session.getCurrent().orElse(null);
        if (usuario == null) {
            mostrarError("Sesión inválida");
            return;
        }

        configurarConsultas();
        configurarTabla();
    }

    private void configurarConsultas() {
        ObservableList<String> consultas = FXCollections.observableArrayList(
            "Inmuebles vendidos por agente",
            "Personas a las que vendió un agente",
            "Historial de ofertas por inmueble",
            "Ofertas activas por comprador",
            "Estadísticas de agentes",
            "Búsqueda de inmuebles por criterios",
            "Estado de contraofertas",
            "Acuerdos completados",
            "Top 5 inmuebles más caros",
            "Compradores sin ofertas"
        );
        cboTipoConsulta.setItems(consultas);

        cboTipoConsulta.valueProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                actualizarDescripcion(nuevo);
            }
        });
    }

    private void configurarTabla() {
        tableResultados.setItems(dataResultados);
    }

    private void actualizarDescripcion(String consulta) {
        switch (consulta) {
            case "Inmuebles vendidos por agente" ->
                txtDescripcion.setText("Muestra todos los inmuebles vendidos por un agente específico.\n" +
                    "Parámetro: ID o nombre del agente");

            case "Personas a las que vendió un agente" ->
                txtDescripcion.setText("Lista de compradores a los que un agente ha vendido propiedades.\n" +
                    "Parámetro: ID del agente");

            case "Historial de ofertas por inmueble" ->
                txtDescripcion.setText("Muestra todas las ofertas realizadas sobre un inmueble específico.\n" +
                    "Parámetro: ID del inmueble");

            case "Ofertas activas por comprador" ->
                txtDescripcion.setText("Lista todas las ofertas activas de un comprador.\n" +
                    "Parámetro: ID del comprador");

            case "Estadísticas de agentes" ->
                txtDescripcion.setText("Muestra estadísticas de todos los agentes: cantidad vendida, antigüedad, etc.\n" +
                    "Parámetro: ninguno");

            case "Búsqueda de inmuebles por criterios" ->
                txtDescripcion.setText("Busca inmuebles por tipo, precio máximo, etc.\n" +
                    "Parámetro: tipo de inmueble (CASA, DEPARTAMENTO, TERRENO)");

            case "Estado de contraofertas" ->
                txtDescripcion.setText("Muestra todas las contraofertas con su estado.\n" +
                    "Parámetro: estado (PENDIENTE, ACEPTADA, RECHAZADA)");

            case "Acuerdos completados" ->
                txtDescripcion.setText("Lista todos los acuerdos de venta completados.\n" +
                    "Parámetro: ninguno");

            case "Top 5 inmuebles más caros" ->
                txtDescripcion.setText("Muestra los 5 inmuebles con precio más alto disponibles.\n" +
                    "Parámetro: ninguno");

            case "Compradores sin ofertas" ->
                txtDescripcion.setText("Lista compradores registrados que no han realizado ninguna oferta.\n" +
                    "Parámetro: ninguno");
        }
    }

    @FXML
    public void onEjecutar() {
        String consulta = cboTipoConsulta.getValue();
        if (consulta == null) {
            mostrarAdvertencia("Seleccione un tipo de consulta");
            return;
        }

        try {
            dataResultados.clear();
            tableResultados.getColumns().clear();

            switch (consulta) {
                case "Inmuebles vendidos por agente" -> consultaInmueblesVendidosPorAgente();
                case "Personas a las que vendió un agente" -> consultaPersonasVendidasPorAgente();
                case "Historial de ofertas por inmueble" -> consultaHistorialOfertasPorInmueble();
                case "Ofertas activas por comprador" -> consultaOfertasActivasPorComprador();
                case "Estadísticas de agentes" -> consultaEstadisticasAgentes();
                case "Búsqueda de inmuebles por criterios" -> consultaBusquedaInmueblesPorCriterios();
                case "Estado de contraofertas" -> consultaEstadoContraofertas();
                case "Acuerdos completados" -> consultaAcuerdosCompletados();
                case "Top 5 inmuebles más caros" -> consultaTop5InmueblesMasCaros();
                case "Compradores sin ofertas" -> consultaCompradoresSinOfertas();
            }

            mostrarInfo("Consulta ejecutada. Resultados: " + dataResultados.size() + " registros");

        } catch (Exception e) {
            mostrarError("Error al ejecutar consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void consultaInmueblesVendidosPorAgente() {
        String parametro = txtParametro.getText().trim();
        if (parametro.isEmpty()) {
            mostrarAdvertencia("Ingrese el ID del agente");
            return;
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT i.id as inmuebleId, i.direccion as direccion, i.tipo as tipo, " +
                "i.precio as precio, a.estado as estadoAcuerdo, a.fechaCierre as fechaCierre " +
                "FROM Acuerdo a JOIN a.inmueble i JOIN a.agente ag " +
                "WHERE ag.id = :agenteId AND a.estado = 'COMPLETADO' " +
                "ORDER BY a.fechaCierre DESC"
            );
            query.setParameter("agenteId", Long.parseLong(parametro));

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Inmueble ID", "Dirección", "Tipo", "Precio", "Estado", "Fecha Cierre"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4],
                    "col6", row[5]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaPersonasVendidasPorAgente() {
        String parametro = txtParametro.getText().trim();
        if (parametro.isEmpty()) {
            mostrarAdvertencia("Ingrese el ID del agente");
            return;
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT DISTINCT c.id as compradorId, c.nombre as nombre, c.apellido as apellido, " +
                "c.email as email, c.telefono as telefono " +
                "FROM Acuerdo a JOIN a.comprador c JOIN a.agente ag " +
                "WHERE ag.id = :agenteId AND a.estado = 'COMPLETADO' " +
                "ORDER BY c.apellido, c.nombre"
            );
            query.setParameter("agenteId", Long.parseLong(parametro));

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Comprador ID", "Nombre", "Apellido", "Email", "Teléfono"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3] != null ? row[3] : "",
                    "col5", row[4] != null ? row[4] : ""
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaHistorialOfertasPorInmueble() {
        String parametro = txtParametro.getText().trim();
        if (parametro.isEmpty()) {
            mostrarAdvertencia("Ingrese el ID del inmueble");
            return;
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT o.id as ofertaId, c.nombre as comprador, o.monto as monto, " +
                "o.formaPago as formaPago, o.estado as estado, o.prioridad as prioridad " +
                "FROM Oferta o JOIN o.comprador c JOIN o.inmueble i " +
                "WHERE i.id = :inmuebleId " +
                "ORDER BY o.prioridad ASC"
            );
            query.setParameter("inmuebleId", Long.parseLong(parametro));

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Oferta ID", "Comprador", "Monto", "Forma Pago", "Estado", "Prioridad"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4],
                    "col6", row[5]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaOfertasActivasPorComprador() {
        String parametro = txtParametro.getText().trim();
        if (parametro.isEmpty()) {
            mostrarAdvertencia("Ingrese el ID del comprador");
            return;
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT o.id as ofertaId, i.direccion as inmueble, i.tipo as tipo, " +
                "o.monto as monto, o.estado as estado " +
                "FROM Oferta o JOIN o.inmueble i JOIN o.comprador c " +
                "WHERE c.id = :compradorId AND o.estado IN ('PENDIENTE', 'CONTRAOFERTADA') " +
                "ORDER BY o.id DESC"
            );
            query.setParameter("compradorId", Long.parseLong(parametro));

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Oferta ID", "Inmueble", "Tipo", "Monto", "Estado"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaEstadisticasAgentes() {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT a.id as id, a.nombre as nombre, a.email as email, " +
                "a.antiguedadEmpresa as antiguedad, a.cantidadVendidos as vendidos " +
                "FROM Agente a WHERE a.activo = 'S' " +
                "ORDER BY a.cantidadVendidos DESC"
            );

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "ID", "Nombre", "Email", "Antigüedad (años)", "Total Vendidos"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaBusquedaInmueblesPorCriterios() {
        String tipo = txtParametro.getText().trim().toUpperCase();
        if (tipo.isEmpty()) {
            tipo = "%";
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT i.id as id, i.direccion as direccion, i.tipo as tipo, " +
                "i.precio as precio, i.metraje as metraje, i.condicion as condicion, " +
                "i.habitaciones as habitaciones, i.estado as estado " +
                "FROM Inmueble i " +
                "WHERE i.estado = 'DISPONIBLE' AND (i.tipo LIKE :tipo OR :tipo = '%') " +
                "ORDER BY i.precio ASC"
            );
            query.setParameter("tipo", "%" + tipo + "%");

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "ID", "Dirección", "Tipo", "Precio", "Metraje", "Condición", "Habitaciones", "Estado"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4] != null ? row[4] : "",
                    "col6", row[5] != null ? row[5] : "",
                    "col7", row[6] != null ? row[6] : "",
                    "col8", row[7]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaEstadoContraofertas() {
        String estado = txtParametro.getText().trim().toUpperCase();
        if (estado.isEmpty()) {
            estado = "PENDIENTE";
        }

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT c.id as id, o.id as ofertaId, v.nombre as vendedor, " +
                "c.monto as monto, c.estado as estado, c.fechaContraoferta as fecha " +
                "FROM Contraoferta c JOIN c.oferta o JOIN c.vendedor v " +
                "WHERE c.estado = :estado " +
                "ORDER BY c.fechaContraoferta DESC"
            );
            query.setParameter("estado", estado);

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Contraoferta ID", "Oferta ID", "Vendedor", "Monto", "Estado", "Fecha"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4],
                    "col6", row[5]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaAcuerdosCompletados() {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT a.id as id, i.direccion as inmueble, c.nombre as comprador, " +
                "v.nombre as vendedor, ag.nombre as agente, a.montoFinal as monto, " +
                "a.fechaCierre as fechaCierre " +
                "FROM Acuerdo a JOIN a.inmueble i JOIN a.comprador c JOIN a.vendedor v " +
                "LEFT JOIN a.agente ag " +
                "WHERE a.estado = 'COMPLETADO' " +
                "ORDER BY a.fechaCierre DESC"
            );

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "Acuerdo ID", "Inmueble", "Comprador", "Vendedor", "Agente", "Monto Final", "Fecha Cierre"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4] != null ? row[4] : "N/A",
                    "col6", row[5],
                    "col7", row[6]
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaTop5InmueblesMasCaros() {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT i.id as id, i.direccion as direccion, i.tipo as tipo, " +
                "i.precio as precio, i.metraje as metraje, i.habitaciones as habitaciones " +
                "FROM Inmueble i " +
                "WHERE i.estado = 'DISPONIBLE' " +
                "ORDER BY i.precio DESC"
            );
            query.setMaxResults(5);

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "ID", "Dirección", "Tipo", "Precio", "Metraje", "Habitaciones"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3],
                    "col5", row[4] != null ? row[4] : "",
                    "col6", row[5] != null ? row[5] : ""
                );
                dataResultados.add(fila);
            }
        }
    }

    private void consultaCompradoresSinOfertas() {
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                "SELECT c.id as id, c.nombre as nombre, c.apellido as apellido, " +
                "c.telefono as telefono " +
                "FROM Comprador c " +
                "WHERE c.activo = 'S' AND NOT EXISTS " +
                "(SELECT 1 FROM Oferta o WHERE o.comprador.id = c.id) " +
                "ORDER BY c.id DESC"
            );

            List<Object[]> resultados = query.getResultList();

            configurarColumnasGenericas(
                "ID", "Nombre", "Apellido", "Teléfono"
            );

            for (Object[] row : resultados) {
                Map<String, Object> fila = Map.of(
                    "col1", row[0],
                    "col2", row[1],
                    "col3", row[2],
                    "col4", row[3] != null ? row[3] : ""
                );
                dataResultados.add(fila);
            }
        }
    }

    private void configurarColumnasGenericas(String... titulos) {
        tableResultados.getColumns().clear();

        for (int i = 0; i < titulos.length; i++) {
            final String colKey = "col" + (i + 1);
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(titulos[i]);

            column.setCellValueFactory(cellData -> {
                Object value = cellData.getValue().get(colKey);
                return new javafx.beans.property.SimpleStringProperty(
                    value != null ? value.toString() : ""
                );
            });

            column.setPrefWidth(150);
            tableResultados.getColumns().add(column);
        }
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void onLimpiar() {
        tableResultados.getItems().clear();
        tableResultados.getColumns().clear();
        mostrarInfo("Tabla limpiada");
    }
}
