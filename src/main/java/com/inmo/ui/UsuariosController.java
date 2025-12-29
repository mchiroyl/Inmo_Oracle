package com.inmo.ui;

import com.inmo.dao.UsuarioDao;
import com.inmo.domain.Usuario;
import com.inmo.security.Access;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.Optional;

public class UsuariosController {

    // Form
    @FXML private TextField txtId;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblInfo;

    // Tabla
    @FXML private TableView<Usuario> tbl;
    @FXML private TableColumn<Usuario, Long>   colId;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colActivo;

    private final UsuarioDao dao = new UsuarioDao();

    @FXML
    public void initialize() {
        // columnas (con getters JavaBean)
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colEmail.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getEmail()).orElse("")));
        colRol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getRol()).orElse("")));
        colActivo.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                (c.getValue().getActivo() != null && c.getValue().getActivo() == 'S') ? "S" : "N"));

        // combo de roles
        cmbRol.setItems(FXCollections.observableArrayList(
                Usuario.ROL_ADMIN, Usuario.ROL_AGENTE, Usuario.ROL_VENDEDOR, Usuario.ROL_COMPRADOR
        ));

        // selección en tabla -> carga al formulario
        tbl.getSelectionModel().selectedItemProperty().addListener((obs, oldv, u) -> {
            if (u == null) return;
            txtId.setText(u.getId() == null ? "" : String.valueOf(u.getId()));
            txtEmail.setText(Objects.toString(u.getEmail(), ""));
            cmbRol.getSelectionModel().select(Objects.toString(u.getRol(), ""));
            chkActivo.setSelected(u.getActivo() != null && u.getActivo() == 'S');
            txtPassword.clear(); // nunca mostramos el hash
        });

        recargarTabla();
        limpiar();
    }

    @FXML
    public void onNuevo() {
        tbl.getSelectionModel().clearSelection();
        limpiar();
    }

    @FXML
    public void onGuardar() {
        if (!Access.canManageUsers()) { info("No autorizado."); return; }

        String email = txtEmail.getText();
        String pass  = txtPassword.getText();
        String rol   = cmbRol.getSelectionModel().getSelectedItem();
        boolean activo = chkActivo.isSelected();

        if (email == null || email.isBlank()) { info("Email es requerido."); return; }
        if (rol == null || rol.isBlank())     { info("Rol es requerido.");   return; }

        Long id = parseLong(txtId.getText());

        Usuario u = (id == null) ? new Usuario() : dao.findById(id).orElseGet(Usuario::new);
        u.setEmail(email.trim());
        if (pass != null && !pass.isBlank()) {
            u.setHashPassword(BCrypt.hashpw(pass, BCrypt.gensalt(12)));
        }
        u.setRol(rol);
        u.setActivo(activo ? 'S' : 'N');

        if (id == null) dao.save(u); else dao.update(u);

        recargarTabla();
        limpiar();
        info("Usuario guardado");
    }

    @FXML
    public void onEliminar() {
        if (!Access.canManageUsers()) { info("No autorizado."); return; }
        var sel = tbl.getSelectionModel().getSelectedItem();
        if (sel == null || sel.getId() == null) return;
        dao.delete(sel.getId());
        recargarTabla();
        limpiar();
        info("Usuario eliminado");
    }

    // ——— helpers ———

    private void recargarTabla() {
        tbl.setItems(FXCollections.observableArrayList(dao.findAll()));
        tbl.refresh();
    }

    private void limpiar() {
        txtId.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRol.getSelectionModel().clearSelection();
        chkActivo.setSelected(false);
        lblInfo.setText("");
    }

    private void info(String msg) { lblInfo.setText(msg); }

    private static Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.parseLong(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
