package com.inmo.ui;

import com.inmo.dao.AgenteDao;
import com.inmo.domain.Agente;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class AgentesController {

    // --- UI ---
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private CheckBox chkActivo;

    @FXML private TextField txtBuscar;

    @FXML private TableView<Agente> tbl;
    @FXML private TableColumn<Agente, String> colId;
    @FXML private TableColumn<Agente, String> colNombre;
    @FXML private TableColumn<Agente, String> colTel;
    @FXML private TableColumn<Agente, String> colEmail;
    @FXML private TableColumn<Agente, String> colActivo;

    @FXML private Label lblInfo;

    private final AgenteDao agenteDao = new AgenteDao();
    private Agente current;

    @FXML
    public void initialize() {
        // Tabla
        colId.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue() == null || c.getValue().getId() == null ? "" : c.getValue().getId().toString()
        ));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getNombre())));
        colTel.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTelefono())));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getEmail())));
        colActivo.setCellValueFactory(c -> new SimpleStringProperty(
                "S".equalsIgnoreCase(c.getValue().getActivo()) ? "Sí" : "No"
        ));

        // Estilo editable (sólo visual; guardamos con el botón Guardar)
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colTel.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        // Selección en la tabla -> llenar formulario
        tbl.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillForm(newV));

        loadData();
        clearForm();
    }

    private void loadData() {
        List<Agente> data = agenteDao.findAll();
        tbl.getItems().setAll(data);
        setInfo("Agentes: " + data.size());
    }

    private void setInfo(String msg) {
        if (lblInfo != null) lblInfo.setText(msg == null ? "" : msg);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private void fillForm(Agente a) {
        current = a;
        if (a == null) {
            clearForm();
            return;
        }
        txtNombre.setText(nvl(a.getNombre()));
        txtTelefono.setText(nvl(a.getTelefono()));
        txtEmail.setText(nvl(a.getEmail()));
        chkActivo.setSelected("S".equalsIgnoreCase(a.getActivo()));
    }

    private void clearForm() {
        current = null;
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        chkActivo.setSelected(true);
    }

    // --- Acciones ---
    @FXML
    public void onNuevo() {
        clearForm();
        txtNombre.requestFocus();
    }

    @FXML
    public void onGuardar() {
        // Validaciones básicas
        String nombre = nvl(txtNombre.getText()).trim();
        String email  = nvl(txtEmail.getText()).trim().toLowerCase();
        String tel    = nvl(txtTelefono.getText()).trim();
        String activo = chkActivo.isSelected() ? "S" : "N";

        if (nombre.isBlank()) {
            alert("El nombre es obligatorio."); return;
        }
        if (email.isBlank()) {
            alert("El email es obligatorio."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            alert("El email no parece válido."); return;
        }

        if (current == null) current = new Agente();
        current.setNombre(nombre);
        current.setEmail(email);
        current.setTelefono(tel);
        current.setActivo(activo);

        try {
            // Guarda y VINCULA automáticamente con USUARIO (si existe rol=AGENTE)
            agenteDao.save(current);
            loadData();
            // Volver a seleccionar el último guardado por comodidad
            Agente refreshed = agenteDao.findByEmail(email);
            if (refreshed != null) {
                tbl.getSelectionModel().select(refreshed);
                fillForm(refreshed);
            }
            setInfo("Agente guardado correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("No se pudo guardar el agente.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Agente sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Seleccione un agente."); return; }

        if (!confirm("¿Eliminar el agente '" + nvl(sel.getNombre()) + "'?")) return;

        try {
            agenteDao.delete(sel);
            loadData();
            clearForm();
            setInfo("Agente eliminado.");
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("No se pudo eliminar el agente.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onBuscar() {
        String term = nvl(txtBuscar.getText()).trim();
        List<Agente> data = agenteDao.search(term);
        tbl.getItems().setAll(data);
        setInfo("Agentes: " + data.size());
    }

    // --- helpers UI ---
    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO)
                .showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
