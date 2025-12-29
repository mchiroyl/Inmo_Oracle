package com.inmo.ui;

import com.inmo.dao.AgenteDao;
import com.inmo.dao.VendedorDao;
import com.inmo.domain.Agente;
import com.inmo.domain.Vendedor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VendedoresController {

    // ---- Controles de formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<Agente> cmbAgente;
    @FXML private CheckBox chkActivo;

    @FXML private TextField txtBuscar;
    @FXML private Label lblInfo;

    // ---- Tabla
    @FXML private TableView<Vendedor> tbl;
    @FXML private TableColumn<Vendedor, Long>   colId;
    @FXML private TableColumn<Vendedor, String> colNombre;
    @FXML private TableColumn<Vendedor, String> colTel;
    @FXML private TableColumn<Vendedor, String> colEmail;
    @FXML private TableColumn<Vendedor, String> colAgente;
    @FXML private TableColumn<Vendedor, String> colActivo;

    // ---- DAOs
    private final VendedorDao vendedorDao = new VendedorDao();
    private final AgenteDao   agenteDao   = new AgenteDao();

    // ---- Estado
    private Vendedor current;
    private List<Vendedor> allData = new ArrayList<>();

    // ------------------------------------------------------------
    // Inicialización
    // ------------------------------------------------------------
    @FXML
    public void initialize() {
        // Columnas
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getNombre())));
        colTel.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTelefono())));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getEmail())));
        colAgente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAgente() == null ? "" : nvl(c.getValue().getAgente().getNombre())
        ));
        colActivo.setCellValueFactory(c -> new SimpleStringProperty(
                "S".equalsIgnoreCase(String.valueOf(c.getValue().getActivo())) ? "Sí" : "No"
        ));

        // Editables en línea (opcional)
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colTel.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        // Selección -> llena formulario
        tbl.getSelectionModel().selectedItemProperty()
           .addListener((obs, oldV, newV) -> fillForm(newV));

        // Combo de agentes
        loadAgentes();

        // Datos
        reload();
        clearForm();
    }

    private void loadAgentes() {
        var agentes = agenteDao.findAll(); // lista ordenada por nombre (según tu AgenteDao)
        cmbAgente.setItems(FXCollections.observableArrayList(agentes));
        cmbAgente.setConverter(new StringConverter<>() {
            @Override public String toString(Agente a) { return a == null ? "" : nvl(a.getNombre()); }
            @Override public Agente fromString(String s) { return null; }
        });
    }

    private void reload() {
        // Trae solo activos para la vista principal (ajústalo si quieres ver también inactivos)
        allData = vendedorDao.findAllActivos();
        tbl.setItems(FXCollections.observableArrayList(allData));
        setInfo("Vendedores: " + allData.size());
    }

    // ------------------------------------------------------------
    // Acciones UI
    // ------------------------------------------------------------
    @FXML
    public void onNuevo() {
        clearForm();
        txtNombre.requestFocus();
    }

    @FXML
    public void onGuardar() {
        String nombre = nvl(txtNombre.getText()).trim();
        String email  = nvl(txtEmail.getText()).trim();
        String tel    = nvl(txtTelefono.getText()).trim();
        Agente ag     = cmbAgente.getSelectionModel().getSelectedItem();
        String activo = chkActivo.isSelected() ? "S" : "N";

        if (nombre.isBlank()) { warn("El nombre es obligatorio."); return; }
        if (email.isBlank())  { warn("El correo es obligatorio."); return; }

        if (current == null) current = new Vendedor();

        current.setNombre(nombre);
        current.setEmail(email.toLowerCase(Locale.ROOT));
        current.setTelefono(tel);
        current.setAgente(ag);
        // *** ARREGLO CLAVE: el setter espera String, no char
        current.setActivo(activo);

        try {
            vendedorDao.save(current);   // se asume que tu VendedorDao tiene save(...)
            reload();
            setInfo("Vendedor guardado.");
        } catch (Exception ex) {
            ex.printStackTrace();
            warn("No se pudo guardar el vendedor.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Vendedor sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Seleccione un vendedor."); return; }
        if (!confirm("¿Eliminar vendedor ID " + sel.getId() + "?")) return;

        try {
            vendedorDao.delete(sel);     // se asume que tu VendedorDao tiene delete(...)
            reload();
            clearForm();
            setInfo("Vendedor eliminado.");
        } catch (Exception ex) {
            ex.printStackTrace();
            warn("No se pudo eliminar el vendedor.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onBuscar() {
        String term = nvl(txtBuscar.getText()).trim().toLowerCase(Locale.ROOT);
        if (term.isBlank()) {
            tbl.setItems(FXCollections.observableArrayList(allData));
            setInfo("Vendedores: " + allData.size());
            return;
        }

        List<Vendedor> filtrados = allData.stream()
                .filter(v ->
                        nvl(v.getNombre()).toLowerCase(Locale.ROOT).contains(term) ||
                        nvl(v.getEmail()).toLowerCase(Locale.ROOT).contains(term)  ||
                        nvl(v.getTelefono()).toLowerCase(Locale.ROOT).contains(term)||
                        (v.getAgente()!=null && nvl(v.getAgente().getNombre())
                                .toLowerCase(Locale.ROOT).contains(term))
                )
                .collect(Collectors.toList());

        tbl.setItems(FXCollections.observableArrayList(filtrados));
        setInfo("Vendedores: " + filtrados.size());
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------
    private void fillForm(Vendedor v) {
        current = v;
        if (v == null) { clearForm(); return; }

        txtNombre.setText(nvl(v.getNombre()));
        txtEmail.setText(nvl(v.getEmail()));
        txtTelefono.setText(nvl(v.getTelefono()));
        cmbAgente.getSelectionModel().select(v.getAgente());
        chkActivo.setSelected("S".equalsIgnoreCase(String.valueOf(v.getActivo())));
    }

    private void clearForm() {
        current = null;
        txtNombre.clear();
        txtEmail.clear();
        txtTelefono.clear();
        cmbAgente.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private void setInfo(String msg) {
        if (lblInfo != null) lblInfo.setText(msg == null ? "" : msg);
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO)
                .showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
