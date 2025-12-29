package com.inmo.ui;

import com.inmo.dao.AgenteDao;
import com.inmo.dao.CompradorDao;
import com.inmo.domain.Agente;
import com.inmo.domain.Comprador;
import com.inmo.domain.Usuario;
import com.inmo.security.Access;
import com.inmo.security.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CompradoresController {

    @FXML private TableView<Comprador> tableCompradores;
    @FXML private TableColumn<Comprador, Long> colId;
    @FXML private TableColumn<Comprador, String> colNombre;
    @FXML private TableColumn<Comprador, String> colApellido;
    @FXML private TableColumn<Comprador, String> colTelefono;
    @FXML private TableColumn<Comprador, String> colEstadoCivil;
    @FXML private TableColumn<Comprador, Integer> colEdad;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtNacionalidad;
    @FXML private TextField txtEdad;
    @FXML private ComboBox<String> cboEstadoCivil;
    @FXML private ComboBox<Agente> cboAgente;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;

    private final CompradorDao compradorDao = new CompradorDao();
    private final AgenteDao agenteDao = new AgenteDao();
    private final ObservableList<Comprador> dataCompradores = FXCollections.observableArrayList();
    private Comprador compradorSeleccionado;

    @FXML
    public void initialize() {
        // Verificar permisos
        Usuario usuario = Session.getCurrent().orElse(null);
        if (usuario == null || !Access.can(usuario, "COMPRADOR", "READ")) {
            mostrarError("No tiene permisos para acceder a esta sección");
            return;
        }

        configurarTabla();
        configurarCombos();
        cargarCompradores();
        configurarPermisos(usuario);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEstadoCivil.setCellValueFactory(new PropertyValueFactory<>("estadoCivil"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        tableCompradores.setItems(dataCompradores);

        tableCompradores.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> {
                if (newSel != null) {
                    compradorSeleccionado = newSel;
                    cargarDatosFormulario(newSel);
                }
            }
        );
    }

    private void configurarCombos() {
        cboEstadoCivil.setItems(FXCollections.observableArrayList(
            "SOLTERO", "CASADO", "DIVORCIADO", "VIUDO"
        ));

        List<Agente> agentes = agenteDao.findAll();
        ObservableList<Agente> obsAgentes = FXCollections.observableArrayList(agentes);
        cboAgente.setItems(obsAgentes);

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

    private void configurarPermisos(Usuario usuario) {
        boolean puedeCrear = Access.can(usuario, "COMPRADOR", "CREATE");
        boolean puedeActualizar = Access.can(usuario, "COMPRADOR", "UPDATE");
        boolean puedeEliminar = Access.can(usuario, "COMPRADOR", "DELETE");

        btnNuevo.setDisable(!puedeCrear);
        btnGuardar.setDisable(!puedeCrear && !puedeActualizar);
        btnEliminar.setDisable(!puedeEliminar);
    }

    private void cargarCompradores() {
        dataCompradores.clear();
        try {
            List<Comprador> lista = compradorDao.findAll();
            dataCompradores.addAll(lista);
        } catch (Exception e) {
            mostrarError("Error al cargar compradores: " + e.getMessage());
        }
    }

    private void cargarDatosFormulario(Comprador comprador) {
        txtNombre.setText(comprador.getNombre());
        txtApellido.setText(comprador.getApellido());
        txtDireccion.setText(comprador.getDireccion());
        txtTelefono.setText(comprador.getTelefono());
        txtNacionalidad.setText(comprador.getNacionalidad());
        txtEdad.setText(comprador.getEdad() != null ? comprador.getEdad().toString() : "");
        cboEstadoCivil.setValue(comprador.getEstadoCivil());
        cboAgente.setValue(comprador.getAgente());
    }

    @FXML
    public void onNuevo() {
        limpiarFormulario();
        compradorSeleccionado = null;
        txtNombre.requestFocus();
    }

    @FXML
    public void onGuardar() {
        try {
            if (!validarFormulario()) {
                return;
            }

            Comprador comprador;
            if (compradorSeleccionado == null) {
                comprador = new Comprador();
            } else {
                comprador = compradorSeleccionado;
            }

            comprador.setNombre(txtNombre.getText().trim());
            comprador.setApellido(txtApellido.getText().trim());
            comprador.setDireccion(txtDireccion.getText().trim());
            comprador.setTelefono(txtTelefono.getText().trim());
            comprador.setNacionalidad(txtNacionalidad.getText().trim());
            comprador.setEstadoCivil(cboEstadoCivil.getValue());
            comprador.setAgente(cboAgente.getValue());

            String edadStr = txtEdad.getText().trim();
            if (!edadStr.isEmpty()) {
                comprador.setEdad(Integer.parseInt(edadStr));
            }

            if (compradorSeleccionado == null) {
                compradorDao.save(comprador);
                mostrarInfo("Comprador guardado exitosamente");
            } else {
                compradorDao.update(comprador);
                mostrarInfo("Comprador actualizado exitosamente");
            }

            cargarCompradores();
            limpiarFormulario();
            compradorSeleccionado = null;

        } catch (Exception e) {
            mostrarError("Error al guardar comprador: " + e.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        if (compradorSeleccionado == null) {
            mostrarAdvertencia("Seleccione un comprador para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar comprador?");
        confirmacion.setContentText("¿Está seguro de eliminar al comprador " +
            compradorSeleccionado.getNombreCompleto() + "?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                compradorDao.delete(compradorSeleccionado.getId());
                mostrarInfo("Comprador eliminado exitosamente");
                cargarCompradores();
                limpiarFormulario();
                compradorSeleccionado = null;
            } catch (Exception e) {
                mostrarError("Error al eliminar comprador: " + e.getMessage());
            }
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAdvertencia("El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        if (txtApellido.getText().trim().isEmpty()) {
            mostrarAdvertencia("El apellido es obligatorio");
            txtApellido.requestFocus();
            return false;
        }

        // Validar teléfono si se proporciona
        String telefono = txtTelefono.getText().trim();
        if (!telefono.isEmpty() && !telefono.matches("^[0-9+\\-\\s()]+$")) {
            mostrarAdvertencia("El teléfono solo debe contener números, espacios, guiones, paréntesis o signo +");
            txtTelefono.requestFocus();
            return false;
        }

        String edadStr = txtEdad.getText().trim();
        if (!edadStr.isEmpty()) {
            try {
                int edad = Integer.parseInt(edadStr);
                if (edad < 18 || edad > 120) {
                    mostrarAdvertencia("La edad debe estar entre 18 y 120 años");
                    txtEdad.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAdvertencia("La edad debe ser un número válido");
                txtEdad.requestFocus();
                return false;
            }
        }

        // Validar que se haya seleccionado un agente (requerido por lógica de negocio)
        if (cboAgente.getValue() == null) {
            mostrarAdvertencia("Debe asignar un agente al comprador");
            cboAgente.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtNacionalidad.clear();
        txtEdad.clear();
        cboEstadoCivil.setValue(null);
        cboAgente.setValue(null);
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
        cargarCompradores();
        mostrarInfo("Datos actualizados");
    }
}
