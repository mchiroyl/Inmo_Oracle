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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AcuerdosController {

    @FXML private TableView<Acuerdo> tableAcuerdos;
    @FXML private TableColumn<Acuerdo, Long> colId;
    @FXML private TableColumn<Acuerdo, String> colInmueble;
    @FXML private TableColumn<Acuerdo, String> colComprador;
    @FXML private TableColumn<Acuerdo, String> colVendedor;
    @FXML private TableColumn<Acuerdo, BigDecimal> colMonto;
    @FXML private TableColumn<Acuerdo, String> colEstado;
    @FXML private TableColumn<Acuerdo, String> colFechaAcuerdo;

    @FXML private ComboBox<Oferta> cboOferta;
    @FXML private ComboBox<Inmueble> cboInmueble;
    @FXML private ComboBox<Comprador> cboComprador;
    @FXML private ComboBox<Vendedor> cboVendedor;
    @FXML private ComboBox<Agente> cboAgente;
    @FXML private TextField txtMontoFinal;
    @FXML private ComboBox<String> cboFormaPago;
    @FXML private TextField txtTiempoPago;
    @FXML private ComboBox<String> cboEstado;
    @FXML private TextArea txtNotas;
    @FXML private DatePicker dpFechaCierre;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCompletar;

    private final AcuerdoDao acuerdoDao = new AcuerdoDao();
    private final OfertaDao ofertaDao = new OfertaDao();
    @FXML private final InmuebleDao inmuebleDao = new InmuebleDao();
    private final CompradorDao compradorDao = new CompradorDao();
    private final VendedorDao vendedorDao = new VendedorDao();
    private final AgenteDao agenteDao = new AgenteDao();

    private final ObservableList<Acuerdo> dataAcuerdos = FXCollections.observableArrayList();
    private Acuerdo acuerdoSeleccionado;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = Session.getCurrent().orElse(null);
        if (usuarioActual == null || !Access.can(usuarioActual, "ACUERDO", "READ")) {
            mostrarError("No tiene permisos para acceder a esta sección");
            return;
        }

        configurarTabla();
        configurarCombos();
        cargarAcuerdos();
        configurarPermisos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoFinal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

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

        colVendedor.setCellValueFactory(cellData -> {
            Vendedor vend = cellData.getValue().getVendedor();
            return new javafx.beans.property.SimpleStringProperty(
                vend != null ? vend.getNombre() : ""
            );
        });

        colFechaAcuerdo.setCellValueFactory(cellData -> {
            OffsetDateTime fecha = cellData.getValue().getFechaAcuerdo();
            String fechaStr = fecha != null ?
                fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new javafx.beans.property.SimpleStringProperty(fechaStr);
        });

        tableAcuerdos.setItems(dataAcuerdos);

        tableAcuerdos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> {
                if (newSel != null) {
                    acuerdoSeleccionado = newSel;
                    cargarDatosFormulario(newSel);
                }
            }
        );
    }

    private void configurarCombos() {
        // Formas de pago
        cboFormaPago.setItems(FXCollections.observableArrayList(
            "EFECTIVO", "FINANCIAMIENTO", "MIXTO", "CREDITO"
        ));

        // Estados
        cboEstado.setItems(FXCollections.observableArrayList(
            "PENDIENTE", "EN_PROCESO", "COMPLETADO", "CANCELADO"
        ));

        // Ofertas aceptadas
        List<Oferta> ofertasAceptadas = ofertaDao.findByEstado("ACEPTADA");
        cboOferta.setItems(FXCollections.observableArrayList(ofertasAceptadas));
        cboOferta.setCellFactory(lv -> new ListCell<Oferta>() {
            @Override
            protected void updateItem(Oferta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText("Oferta #" + item.getId() + " - " +
                        item.getInmueble().getDireccion() +
                        " - " + item.getComprador().getNombreCompleto());
                }
            }
        });
        cboOferta.setButtonCell(new ListCell<Oferta>() {
            @Override
            protected void updateItem(Oferta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText("Oferta #" + item.getId());
                }
            }
        });

        // Cuando se selecciona una oferta, autocompletar datos
        cboOferta.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                autocompletarDesdeOferta(newVal);
            }
        });

        // Inmuebles
        List<Inmueble> inmuebles = inmuebleDao.findAll();
        cboInmueble.setItems(FXCollections.observableArrayList(inmuebles));
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
        cboComprador.setItems(FXCollections.observableArrayList(compradores));
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

        // Vendedores
        List<Vendedor> vendedores = vendedorDao.findAllActivos();
        cboVendedor.setItems(FXCollections.observableArrayList(vendedores));
        cboVendedor.setCellFactory(lv -> new ListCell<Vendedor>() {
            @Override
            protected void updateItem(Vendedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cboVendedor.setButtonCell(new ListCell<Vendedor>() {
            @Override
            protected void updateItem(Vendedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        // Agentes
        List<Agente> agentes = agenteDao.findAll();
        cboAgente.setItems(FXCollections.observableArrayList(agentes));
        cboAgente.setCellFactory(lv -> new ListCell<Agente>() {
            @Override
            protected void updateItem(Agente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cboAgente.setButtonCell(new ListCell<Agente>() {
            @Override
            protected void updateItem(Agente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
    }

    private void autocompletarDesdeOferta(Oferta oferta) {
        cboInmueble.setValue(oferta.getInmueble());
        cboComprador.setValue(oferta.getComprador());
        cboVendedor.setValue(oferta.getInmueble().getVendedor());
        cboAgente.setValue(oferta.getAgente());
        txtMontoFinal.setText(oferta.getMonto().toString());
        // formaPago y tiempoPago se definen en el acuerdo, no en la oferta
    }

    private void configurarPermisos() {
        boolean puedeCrear = Access.can(usuarioActual, "ACUERDO", "CREATE");
        boolean puedeActualizar = Access.can(usuarioActual, "ACUERDO", "UPDATE");
        boolean puedeEliminar = Access.can(usuarioActual, "ACUERDO", "DELETE");

        btnNuevo.setDisable(!puedeCrear);
        btnGuardar.setDisable(!puedeCrear && !puedeActualizar);
        btnEliminar.setDisable(!puedeEliminar);
        btnCompletar.setDisable(!puedeActualizar);
    }

    private void cargarAcuerdos() {
        dataAcuerdos.clear();
        try {
            List<Acuerdo> lista = acuerdoDao.findAll();
            dataAcuerdos.addAll(lista);
        } catch (Exception e) {
            mostrarError("Error al cargar acuerdos: " + e.getMessage());
        }
    }

    private void cargarDatosFormulario(Acuerdo acuerdo) {
        cboOferta.setValue(acuerdo.getOferta());
        cboInmueble.setValue(acuerdo.getInmueble());
        cboComprador.setValue(acuerdo.getComprador());
        cboVendedor.setValue(acuerdo.getVendedor());
        cboAgente.setValue(acuerdo.getAgente());
        txtMontoFinal.setText(acuerdo.getMontoFinal() != null ? acuerdo.getMontoFinal().toString() : "");
        cboFormaPago.setValue(acuerdo.getFormaPago());
        txtTiempoPago.setText(acuerdo.getTiempoPago() != null ? acuerdo.getTiempoPago().toString() : "");
        cboEstado.setValue(acuerdo.getEstado());
        txtNotas.setText(acuerdo.getNotas());

        if (acuerdo.getFechaCierre() != null) {
            dpFechaCierre.setValue(acuerdo.getFechaCierre().toLocalDate());
        }
    }

    @FXML
    public void onNuevo() {
        limpiarFormulario();
        acuerdoSeleccionado = null;
        cboOferta.requestFocus();
    }

    @FXML
    public void onGuardar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Acuerdo acuerdo;
            if (acuerdoSeleccionado == null) {
                acuerdo = new Acuerdo();
            } else {
                acuerdo = acuerdoSeleccionado;
            }

            acuerdo.setOferta(cboOferta.getValue());
            acuerdo.setInmueble(cboInmueble.getValue());
            acuerdo.setComprador(cboComprador.getValue());
            acuerdo.setVendedor(cboVendedor.getValue());
            acuerdo.setAgente(cboAgente.getValue());
            acuerdo.setMontoFinal(new BigDecimal(txtMontoFinal.getText().trim()));
            acuerdo.setFormaPago(cboFormaPago.getValue());

            String tiempoStr = txtTiempoPago.getText().trim();
            if (!tiempoStr.isEmpty()) {
                acuerdo.setTiempoPago(Integer.parseInt(tiempoStr));
            }

            if (cboEstado.getValue() != null) {
                acuerdo.setEstado(cboEstado.getValue());
            }
            acuerdo.setNotas(txtNotas.getText().trim());

            if (dpFechaCierre.getValue() != null) {
                acuerdo.setFechaCierre(dpFechaCierre.getValue().atStartOfDay().atOffset(OffsetDateTime.now().getOffset()));
            }

            if (acuerdoSeleccionado == null) {
                acuerdoDao.save(acuerdo);
                mostrarInfo("Acuerdo guardado exitosamente");
            } else {
                acuerdoDao.update(acuerdo);
                mostrarInfo("Acuerdo actualizado exitosamente");
            }

            cargarAcuerdos();
            limpiarFormulario();
            acuerdoSeleccionado = null;

        } catch (Exception e) {
            mostrarError("Error al guardar acuerdo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onEliminar() {
        if (acuerdoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un acuerdo para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Cancelar acuerdo?");
        confirmacion.setContentText("¿Está seguro de cancelar este acuerdo?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                acuerdoDao.delete(acuerdoSeleccionado.getId());
                mostrarInfo("Acuerdo cancelado exitosamente");
                cargarAcuerdos();
                limpiarFormulario();
                acuerdoSeleccionado = null;
            } catch (Exception e) {
                mostrarError("Error al cancelar acuerdo: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onCompletar() {
        if (acuerdoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un acuerdo para completar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Completar Acuerdo");
        confirmacion.setHeaderText("¿Completar acuerdo?");
        confirmacion.setContentText("¿Está seguro de marcar este acuerdo como completado?\n" +
            "Esto cambiará el estado del inmueble a VENDIDO.");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                acuerdoSeleccionado.setEstado("COMPLETADO");
                acuerdoSeleccionado.setFechaCierre(OffsetDateTime.now());
                acuerdoDao.update(acuerdoSeleccionado);

                // Cambiar estado del inmueble a VENDIDO
                Inmueble inmueble = acuerdoSeleccionado.getInmueble();
                if (inmueble != null) {
                    inmueble.setEstado("VENDIDO");
                    inmuebleDao.save(inmueble);
                }

                mostrarInfo("Acuerdo completado exitosamente");
                cargarAcuerdos();
                limpiarFormulario();
                acuerdoSeleccionado = null;
            } catch (Exception e) {
                mostrarError("Error al completar acuerdo: " + e.getMessage());
            }
        }
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
        if (cboVendedor.getValue() == null) {
            mostrarAdvertencia("Seleccione un vendedor");
            return false;
        }
        if (txtMontoFinal.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingrese el monto final");
            return false;
        }
        try {
            BigDecimal monto = new BigDecimal(txtMontoFinal.getText().trim());
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAdvertencia("El monto debe ser mayor a cero");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("El monto debe ser un número válido");
            return false;
        }
        if (cboFormaPago.getValue() == null) {
            mostrarAdvertencia("Seleccione una forma de pago");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cboOferta.setValue(null);
        cboInmueble.setValue(null);
        cboComprador.setValue(null);
        cboVendedor.setValue(null);
        cboAgente.setValue(null);
        txtMontoFinal.clear();
        cboFormaPago.setValue(null);
        txtTiempoPago.clear();
        cboEstado.setValue("PENDIENTE");
        txtNotas.clear();
        dpFechaCierre.setValue(null);
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
        cargarAcuerdos();
        mostrarInfo("Acuerdos actualizados");
    }
}
