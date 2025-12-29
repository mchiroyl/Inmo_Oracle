package com.inmo.ui;

import com.inmo.dao.*;
import com.inmo.domain.*;
import com.inmo.security.Access;
import com.inmo.security.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OfertasController {

    @FXML private TableView<Oferta> tableOfertas;
    @FXML private TableColumn<Oferta, Long> colId;
    @FXML private TableColumn<Oferta, String> colInmueble;
    @FXML private TableColumn<Oferta, String> colComprador;
    @FXML private TableColumn<Oferta, BigDecimal> colMonto;
    @FXML private TableColumn<Oferta, String> colEstado;
    @FXML private TableColumn<Oferta, Integer> colPrioridad;

    @FXML private ComboBox<Inmueble> cboInmueble;
    @FXML private ComboBox<Comprador> cboComprador;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> cboEstado;
    @FXML private TextArea txtComentarios;
    @FXML private Label lblPrioridad;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNueva;
    @FXML private Button btnContraofertar;
    @FXML private Button btnAceptar;
    @FXML private Button btnRechazar;

    private final OfertaDao ofertaDao = new OfertaDao();
    private final InmuebleDao inmuebleDao = new InmuebleDao();
    private final CompradorDao compradorDao = new CompradorDao();
    private final ContraofertaDao contraofertaDao = new ContraofertaDao();
    private final VendedorDao vendedorDao = new VendedorDao();

    private final ObservableList<Oferta> dataOfertas = FXCollections.observableArrayList();
    private Oferta ofertaSeleccionada;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = Session.getCurrent().orElse(null);
        if (usuarioActual == null || !Access.can(usuarioActual, "OFERTA", "READ")) {
            mostrarError("No tiene permisos para acceder a esta sección");
            return;
        }

        configurarTabla();
        configurarCombos();
        cargarOfertas();
        configurarPermisos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));

        colInmueble.setCellValueFactory(cellData -> {
            Inmueble inm = cellData.getValue().getInmueble();
            return new javafx.beans.property.SimpleStringProperty(
                inm != null ? inm.getDireccion() : ""
            );
        });

        colComprador.setCellValueFactory(cellData -> {
            Comprador comp = cellData.getValue().getComprador();
            return new javafx.beans.property.SimpleStringProperty(
                comp != null ? comp.getNombreCompleto() : ""
            );
        });

        tableOfertas.setItems(dataOfertas);

        tableOfertas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> {
                if (newSel != null) {
                    ofertaSeleccionada = newSel;
                    cargarDatosFormulario(newSel);
                    actualizarBotonesSegunEstado();
                }
            }
        );
    }

    private void configurarCombos() {
        // Estados
        cboEstado.setItems(FXCollections.observableArrayList(
            "PENDIENTE", "ACEPTADA", "RECHAZADA", "CONTRAOFERTADA", "CANCELADA"
        ));

        // Inmuebles disponibles
        List<Inmueble> inmuebles = inmuebleDao.findAll();
        ObservableList<Inmueble> obsInmuebles = FXCollections.observableArrayList(
            inmuebles.stream().filter(i -> "DISPONIBLE".equals(i.getEstado())).toList()
        );
        cboInmueble.setItems(obsInmuebles);

        cboInmueble.setCellFactory(lv -> new ListCell<Inmueble>() {
            @Override
            protected void updateItem(Inmueble item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDireccion() + " - " + item.getTipo());
            }
        });

        cboInmueble.setButtonCell(new ListCell<Inmueble>() {
            @Override
            protected void updateItem(Inmueble item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDireccion());
            }
        });

        // Compradores
        List<Comprador> compradores = compradorDao.findAll();
        ObservableList<Comprador> obsCompradores = FXCollections.observableArrayList(compradores);
        cboComprador.setItems(obsCompradores);

        cboComprador.setCellFactory(lv -> new ListCell<Comprador>() {
            @Override
            protected void updateItem(Comprador item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombreCompleto());
            }
        });

        cboComprador.setButtonCell(new ListCell<Comprador>() {
            @Override
            protected void updateItem(Comprador item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombreCompleto());
            }
        });
    }

    private void configurarPermisos() {
        boolean puedeCrear = Access.can(usuarioActual, "OFERTA", "CREATE");
        boolean puedeActualizar = Access.can(usuarioActual, "OFERTA", "UPDATE");
        boolean puedeEliminar = Access.can(usuarioActual, "OFERTA", "DELETE");

        btnNueva.setDisable(!puedeCrear);
        btnGuardar.setDisable(!puedeCrear && !puedeActualizar);
        btnEliminar.setDisable(!puedeEliminar);

        // Vendedores pueden contraofertar
        btnContraofertar.setDisable(!Usuario.ROL_VENDEDOR.equals(usuarioActual.getRol()));
    }

    private void cargarOfertas() {
        dataOfertas.clear();
        try {
            List<Oferta> lista;

            // Filtrar según rol
            if (Usuario.ROL_ADMIN.equals(usuarioActual.getRol())) {
                lista = ofertaDao.findAll();
            } else if (Usuario.ROL_COMPRADOR.equals(usuarioActual.getRol())) {
                // Buscar comprador asociado al usuario
                Comprador comp = compradorDao.findByUsuarioId(usuarioActual.getId()).orElse(null);
                if (comp != null) {
                    lista = ofertaDao.findByComprador(comp.getId());
                } else {
                    lista = List.of();
                }
            } else if (Usuario.ROL_VENDEDOR.equals(usuarioActual.getRol())) {
                // Buscar vendedor asociado al usuario
                Vendedor vend = vendedorDao.findByUsuarioId(usuarioActual.getId()).orElse(null);
                if (vend != null) {
                    lista = ofertaDao.findPendientesByVendedor(vend.getId());
                } else {
                    lista = List.of();
                }
            } else {
                lista = List.of();
            }

            dataOfertas.addAll(lista);
        } catch (Exception e) {
            mostrarError("Error al cargar ofertas: " + e.getMessage());
        }
    }

    private void cargarDatosFormulario(Oferta oferta) {
        cboInmueble.setValue(oferta.getInmueble());
        cboComprador.setValue(oferta.getComprador());
        txtMonto.setText(oferta.getMonto() != null ? oferta.getMonto().toString() : "");
        cboEstado.setValue(oferta.getEstado());
        txtComentarios.setText(oferta.getComentarios());
        lblPrioridad.setText("Prioridad: " + oferta.getPrioridad());
    }

    @FXML
    public void onNueva() {
        limpiarFormulario();
        ofertaSeleccionada = null;
        cboInmueble.requestFocus();
    }

    @FXML
    public void onGuardar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Oferta oferta;
            if (ofertaSeleccionada == null) {
                oferta = new Oferta();
            } else {
                oferta = ofertaSeleccionada;
            }

            oferta.setInmueble(cboInmueble.getValue());
            oferta.setComprador(cboComprador.getValue());
            oferta.setMonto(new BigDecimal(txtMonto.getText().trim()));

            if (cboEstado.getValue() != null) {
                oferta.setEstado(cboEstado.getValue());
            }
            oferta.setComentarios(txtComentarios.getText().trim());

            if (ofertaSeleccionada == null) {
                ofertaDao.save(oferta);
                mostrarInfo("Oferta guardada exitosamente. Prioridad: " + oferta.getPrioridad());
            } else {
                ofertaDao.update(oferta);
                mostrarInfo("Oferta actualizada exitosamente");
            }

            cargarOfertas();
            limpiarFormulario();
            ofertaSeleccionada = null;

        } catch (Exception e) {
            mostrarError("Error al guardar oferta: " + e.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        if (ofertaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una oferta para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Cancelar oferta?");
        confirmacion.setContentText("¿Está seguro de cancelar esta oferta?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                ofertaDao.delete(ofertaSeleccionada.getId());
                mostrarInfo("Oferta cancelada exitosamente");
                cargarOfertas();
                limpiarFormulario();
                ofertaSeleccionada = null;
            } catch (Exception e) {
                mostrarError("Error al cancelar oferta: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onContraofertar() {
        if (ofertaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una oferta para contraofertar");
            return;
        }

        // Crear ventana de diálogo para contraoferta
        Dialog<Contraoferta> dialog = new Dialog<>();
        dialog.setTitle("Nueva Contraoferta");
        dialog.setHeaderText("Crear contraoferta para la oferta #" + ofertaSeleccionada.getId());

        ButtonType btnGuardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarType, ButtonType.CANCEL);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMontoContra = new TextField();
        txtMontoContra.setPromptText("Monto");
        ComboBox<String> cboFormaPagoContra = new ComboBox<>(FXCollections.observableArrayList(
            "EFECTIVO", "FINANCIAMIENTO", "MIXTO", "CREDITO"
        ));
        TextField txtTiempoContra = new TextField();
        txtTiempoContra.setPromptText("Meses");
        TextArea txtComentariosContra = new TextArea();
        txtComentariosContra.setPromptText("Comentarios");
        txtComentariosContra.setPrefRowCount(3);

        grid.add(new Label("Monto:"), 0, 0);
        grid.add(txtMontoContra, 1, 0);
        grid.add(new Label("Forma de Pago:"), 0, 1);
        grid.add(cboFormaPagoContra, 1, 1);
        grid.add(new Label("Tiempo (meses):"), 0, 2);
        grid.add(txtTiempoContra, 1, 2);
        grid.add(new Label("Comentarios:"), 0, 3);
        grid.add(txtComentariosContra, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardarType) {
                try {
                    Contraoferta contra = new Contraoferta();
                    contra.setOferta(ofertaSeleccionada);

                    // Obtener vendedor del usuario actual
                    Vendedor vendedor = vendedorDao.findByUsuarioId(usuarioActual.getId()).orElse(null);
                    contra.setVendedor(vendedor);

                    contra.setMonto(new BigDecimal(txtMontoContra.getText().trim()));
                    contra.setFormaPago(cboFormaPagoContra.getValue());

                    String tiempo = txtTiempoContra.getText().trim();
                    if (!tiempo.isEmpty()) {
                        contra.setTiempoPago(Integer.parseInt(tiempo));
                    }

                    contra.setComentarios(txtComentariosContra.getText().trim());
                    return contra;
                } catch (Exception e) {
                    mostrarError("Error en los datos: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(contraoferta -> {
            try {
                contraofertaDao.save(contraoferta);

                // Actualizar estado de la oferta
                ofertaSeleccionada.setEstado("CONTRAOFERTADA");
                ofertaDao.update(ofertaSeleccionada);

                mostrarInfo("Contraoferta creada exitosamente");
                cargarOfertas();
            } catch (Exception e) {
                mostrarError("Error al guardar contraoferta: " + e.getMessage());
            }
        });
    }

    @FXML
    public void onAceptar() {
        if (ofertaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una oferta para aceptar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Aceptar Oferta");
        confirmacion.setHeaderText("¿Aceptar oferta?");
        confirmacion.setContentText("¿Desea aceptar esta oferta y crear un acuerdo?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                ofertaSeleccionada.setEstado("ACEPTADA");
                ofertaDao.update(ofertaSeleccionada);

                mostrarInfo("Oferta aceptada. Puede proceder a crear un acuerdo.");
                cargarOfertas();
            } catch (Exception e) {
                mostrarError("Error al aceptar oferta: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onRechazar() {
        if (ofertaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una oferta para rechazar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Rechazar Oferta");
        confirmacion.setHeaderText("¿Rechazar oferta?");
        confirmacion.setContentText("¿Está seguro de rechazar esta oferta?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                ofertaSeleccionada.setEstado("RECHAZADA");
                ofertaDao.update(ofertaSeleccionada);

                mostrarInfo("Oferta rechazada");
                cargarOfertas();
                limpiarFormulario();
                ofertaSeleccionada = null;
            } catch (Exception e) {
                mostrarError("Error al rechazar oferta: " + e.getMessage());
            }
        }
    }

    private void actualizarBotonesSegunEstado() {
        if (ofertaSeleccionada == null) {
            return;
        }

        String estado = ofertaSeleccionada.getEstado();
        boolean esVendedor = Usuario.ROL_VENDEDOR.equals(usuarioActual.getRol());

        btnAceptar.setDisable(!"PENDIENTE".equals(estado) || !esVendedor);
        btnRechazar.setDisable(!"PENDIENTE".equals(estado) || !esVendedor);
        btnContraofertar.setDisable(!"PENDIENTE".equals(estado) || !esVendedor);
    }

    private boolean validarFormulario() {
        if (cboInmueble.getValue() == null) {
            mostrarAdvertencia("Seleccione un inmueble");
            return false;
        }
        if (cboComprador.getValue() == null) {
            mostrarAdvertencia("Seleccione un comprador");
            return false;
        }
        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingrese el monto de la oferta");
            return false;
        }

        // Validar monto
        BigDecimal monto;
        try {
            monto = new BigDecimal(txtMonto.getText().trim());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAdvertencia("El monto debe ser mayor a cero");
                txtMonto.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("El monto debe ser un número válido");
            txtMonto.requestFocus();
            return false;
        }

        // Validar que el monto no exceda el precio del inmueble en más del 50%
        Inmueble inmueble = cboInmueble.getValue();
        if (inmueble != null && inmueble.getPrecio() != null) {
            BigDecimal precioInmueble = inmueble.getPrecio();
            BigDecimal limiteMaximo = precioInmueble.multiply(new BigDecimal("1.5"));

            if (monto.compareTo(limiteMaximo) > 0) {
                mostrarAdvertencia("El monto de la oferta no puede exceder el 150% del precio del inmueble\n" +
                    "Precio: " + precioInmueble + "\nLímite máximo: " + limiteMaximo);
                txtMonto.requestFocus();
                return false;
            }
        }

        // Validar que el inmueble esté disponible
        if (inmueble != null && !"DISPONIBLE".equals(inmueble.getEstado())) {
            mostrarAdvertencia("El inmueble seleccionado no está disponible");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        cboInmueble.setValue(null);
        cboComprador.setValue(null);
        txtMonto.clear();
        cboEstado.setValue("PENDIENTE");
        txtComentarios.clear();
        lblPrioridad.setText("Prioridad: -");
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
    public void onRefrescar() {
        cargarOfertas();
        mostrarInfo("Ofertas actualizadas");
    }
}

