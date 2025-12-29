package com.inmo.ui;

import com.inmo.dao.AgenteDao;
import com.inmo.dao.InmuebleDao;
import com.inmo.dao.VendedorDao;
import com.inmo.domain.Agente;
import com.inmo.domain.Inmueble;
import com.inmo.domain.Usuario;
import com.inmo.domain.Vendedor;
import com.inmo.security.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InmueblesController {

    // --- UI ---
    @FXML private ComboBox<Vendedor> cmbVendedor;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;
    @FXML private Label lblInfo;

    @FXML private TableView<Inmueble> tbl;
    @FXML private TableColumn<Inmueble, Long>   colId;
    @FXML private TableColumn<Inmueble, String> colDir;
    @FXML private TableColumn<Inmueble, String> colTipo;
    @FXML private TableColumn<Inmueble, String> colPrecio;
    @FXML private TableColumn<Inmueble, String> colVendedor;

    // --- DAOs ---
    private final InmuebleDao inmuebleDao = new InmuebleDao();
    private final VendedorDao vendedorDao = new VendedorDao();
    private final AgenteDao   agenteDao   = new AgenteDao();

    private Inmueble current;

    // ---------- Init ----------
    @FXML
    public void initialize() {
        // Columnas
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colDir.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDireccion())));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTipo())));
        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPrecio() == null
                        ? ""
                        : c.getValue().getPrecio().toPlainString())
        );
        colVendedor.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVendedor() == null
                        ? ""
                        : nvl(c.getValue().getVendedor().getNombre()))
        );

        // Edición en celda (si luego quieres habilitar commit puedes añadir listeners)
        colDir.setCellFactory(TextFieldTableCell.forTableColumn());
        colTipo.setCellFactory(TextFieldTableCell.forTableColumn());

        // Selección de fila -> llenar formulario
        tbl.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillForm(newV));

        // Tipos predefinidos
        cmbTipo.setItems(FXCollections.observableArrayList(
                "Casa", "Apartamento", "Terreno", "Bodega", "Finca", "Oficina", "Local"
        ));

        loadVendedoresPorRol();
        loadData();
        clearForm();
    }

    // Carga el combo de vendedores según el rol del usuario autenticado
    private void loadVendedoresPorRol() {
        Usuario u = Session.getCurrent().map(x -> x).orElse(null);
        List<Vendedor> vendedores;

        if (u == null || Usuario.ROL_ADMIN.equals(u.getRol())) {
            vendedores = vendedorDao.findAllActivos();
        } else if (Usuario.ROL_AGENTE.equals(u.getRol())) {
            Agente ag = agenteDao.findByUsuarioId(u.getId());
            vendedores = (ag == null) ? List.of() : vendedorDao.findByAgenteId(ag.getId());
        } else if (Usuario.ROL_VENDEDOR.equals(u.getRol())) {
            Vendedor v = vendedorDao.findByUsuarioId(u.getId()).orElse(null);
            vendedores = (v == null) ? List.of() : List.of(v);
        } else {
            vendedores = vendedorDao.findAllActivos();
        }

        cmbVendedor.setItems(FXCollections.observableArrayList(vendedores));
        cmbVendedor.setConverter(new StringConverterForVendedor());
    }

    private void loadData() {
        List<Inmueble> data = inmuebleDao.findAll();
        tbl.getItems().setAll(data);
        setInfo("Inmuebles: " + data.size());
    }

    private void setInfo(String msg) {
        if (lblInfo != null) lblInfo.setText(msg == null ? "" : msg);
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    // ---------- Form helpers ----------
    private void fillForm(Inmueble i) {
        current = i;
        if (i == null) {
            clearForm();
            return;
        }
        txtDireccion.setText(nvl(i.getDireccion()));
        cmbTipo.getSelectionModel().select(nvl(i.getTipo()));
        txtPrecio.setText(i.getPrecio() == null ? "" : i.getPrecio().toPlainString());
        cmbVendedor.getSelectionModel().select(i.getVendedor());
    }

    private void clearForm() {
        current = null;
        txtDireccion.clear();
        cmbTipo.getSelectionModel().clearSelection();
        txtPrecio.clear();
        cmbVendedor.getSelectionModel().clearSelection();
    }

    // ---------- Acciones ----------
    @FXML
    public void onNuevo() {
        clearForm();
        txtDireccion.requestFocus();
    }

    @FXML
    public void onGuardar() {
        String dir      = nvl(txtDireccion.getText()).trim();
        String tipo     = nvl(cmbTipo.getSelectionModel().getSelectedItem());
        String precioStr= nvl(txtPrecio.getText()).trim();
        Vendedor vend   = cmbVendedor.getSelectionModel().getSelectedItem();

        if (dir.isBlank())     { warn("La dirección es obligatoria."); return; }
        if (tipo.isBlank())    { warn("El tipo es obligatorio."); return; }
        if (vend == null)      { warn("Seleccione un vendedor."); return; }

        BigDecimal precio = null;
        if (!precioStr.isBlank()) {
            try { precio = new BigDecimal(precioStr); }
            catch (Exception e) { warn("Precio inválido."); return; }
        }

        if (current == null) current = new Inmueble();
        current.setDireccion(dir);
        current.setTipo(tipo);
        current.setPrecio(precio);
        current.setVendedor(vend);

        try {
            inmuebleDao.save(current);
            loadData();
            setInfo("Inmueble guardado.");
        } catch (Exception ex) {
            ex.printStackTrace();
            warn("No se pudo guardar el inmueble.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onEliminar() {
        Inmueble sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Seleccione un inmueble."); return; }
        if (!confirm("¿Eliminar inmueble ID " + sel.getId() + "?")) return;

        try {
            inmuebleDao.delete(sel); // soportado en el DAO
            loadData();
            clearForm();
            setInfo("Inmueble eliminado.");
        } catch (Exception ex) {
            ex.printStackTrace();
            warn("No se pudo eliminar el inmueble.\n" + ex.getMessage());
        }
    }

    @FXML
    public void onBuscar() {
        String term = nvl(txtBuscar.getText()).trim().toLowerCase();
        Vendedor selVend = cmbVendedor.getSelectionModel().getSelectedItem();

        List<Inmueble> data;

        if (selVend != null && term.isBlank()) {
            // Sólo filtrar por vendedor (consulta directa)
            data = inmuebleDao.findByVendedorId(selVend.getId());
        } else if (term.isBlank()) {
            data = inmuebleDao.findAll();
        } else {
            // Buscar por término y, si hay vendedor seleccionado, filtrar en memoria
            data = inmuebleDao.search(term);
            if (selVend != null) {
                data = data.stream()
                        .filter(i -> i.getVendedor() != null
                                && Objects.equals(i.getVendedor().getId(), selVend.getId()))
                        .collect(Collectors.toList());
            }
        }

        tbl.getItems().setAll(data);
        setInfo("Inmuebles: " + data.size());
    }

    // ---------- helpers ----------
    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO)
                .showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    // Conversor para mostrar el nombre del vendedor en el ComboBox
    private static class StringConverterForVendedor extends javafx.util.StringConverter<Vendedor> {
        @Override public String toString(Vendedor v) { return v == null ? "" : nvl(v.getNombre()); }
        @Override public Vendedor fromString(String s) { return null; }
        private static String nvl(String s) { return s == null ? "" : s; }
    }
}
