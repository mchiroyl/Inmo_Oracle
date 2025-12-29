package com.inmo.ui;

import com.inmo.domain.Usuario;
import com.inmo.security.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuController {

    // Botones del menú
    @FXML private Button btnUsuarios;
    @FXML private Button btnAgentes;
    @FXML private Button btnVendedores;
    @FXML private Button btnCompradores;
    @FXML private Button btnInmuebles;
    @FXML private Button btnOfertas;
    @FXML private Button btnAcuerdos;
    @FXML private Button btnConsultas;
    @FXML private Button btnLogout;

    // Tamaño consistente para el Login al cerrar sesión
    private static final double LOGIN_WIDTH  = 360;
    private static final double LOGIN_HEIGHT = 480;

    // Configura visibilidad por rol
    public void configureFor(Usuario u) {
        if (u == null) return;

        String rol = u.getRol();
        boolean isAdmin     = Usuario.ROL_ADMIN.equals(rol);
        boolean isAgente    = Usuario.ROL_AGENTE.equals(rol);
        boolean isVendedor  = Usuario.ROL_VENDEDOR.equals(rol);
        boolean isComprador = Usuario.ROL_COMPRADOR.equals(rol);

        if (btnUsuarios != null)    btnUsuarios.setVisible(isAdmin);
        if (btnAgentes != null)     btnAgentes.setVisible(isAdmin || isAgente);
        if (btnVendedores != null)  btnVendedores.setVisible(isAdmin || isAgente || isVendedor);
        if (btnCompradores != null) btnCompradores.setVisible(isAdmin || isAgente || isComprador);
        if (btnInmuebles != null)   btnInmuebles.setVisible(isAdmin || isAgente || isVendedor);
        if (btnOfertas != null)     btnOfertas.setVisible(isAdmin || isComprador || isVendedor);
        if (btnAcuerdos != null)    btnAcuerdos.setVisible(isAdmin || isAgente || isVendedor);
        if (btnConsultas != null)   btnConsultas.setVisible(true); // Todos pueden ver consultas
    }

    @FXML
    public void initialize() {
        var uOpt = Session.getCurrent();
        if (uOpt != null && uOpt.get() != null) {
            configureFor(uOpt.get());
        }
    }

    // ---------- Navegación ----------
    @FXML public void onOpenUsuarios(ActionEvent e)    { openModal("/ui/usuarios.fxml",    "INMO - Usuarios"); }
    @FXML public void onOpenAgentes(ActionEvent e)     { openModal("/ui/agentes.fxml",     "INMO - Agentes"); }
    @FXML public void onOpenVendedores(ActionEvent e)  { openModal("/ui/vendedores.fxml",  "INMO - Vendedores"); }
    @FXML public void onOpenCompradores(ActionEvent e) { openModal("/ui/compradores.fxml", "INMO - Compradores"); }
    @FXML public void onOpenInmuebles(ActionEvent e)   { openModal("/ui/inmuebles.fxml",   "INMO - Inmuebles"); }
    @FXML public void onOpenOfertas(ActionEvent e)     { openModal("/ui/ofertas.fxml",     "INMO - Ofertas"); }
    @FXML public void onOpenAcuerdos(ActionEvent e)    { openModal("/ui/acuerdos.fxml",    "INMO - Acuerdos de Venta"); }
    @FXML public void onOpenConsultas(ActionEvent e)   { openModal("/ui/consultas.fxml",   "INMO - Consultas e Informes"); }

    private void openModal(String fxmlPath, String title) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("No se encontró el recurso FXML: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            URL css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage owner = null;
            if (btnUsuarios != null && btnUsuarios.getScene() != null) {
                owner = (Stage) btnUsuarios.getScene().getWindow();
            }

            Stage modal = new Stage();
            modal.setTitle(title);
            modal.setScene(scene);
            if (owner != null) {
                modal.initOwner(owner);
                modal.initModality(Modality.WINDOW_MODAL);
            }
            modal.setResizable(true);
            modal.centerOnScreen();
            modal.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ---------- Cerrar sesión ----------
    @FXML
    public void onLogout(ActionEvent e) {
        try {
            Stage current = (Stage) ((Node) e.getSource()).getScene().getWindow();
            current.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, LOGIN_WIDTH, LOGIN_HEIGHT);
            var css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage login = new Stage();
            login.setTitle("INMO - Login");
            login.setScene(scene);
            login.setResizable(false);
            login.centerOnScreen();
            login.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
