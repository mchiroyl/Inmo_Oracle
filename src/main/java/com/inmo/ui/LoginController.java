package com.inmo.ui;

import com.inmo.domain.Usuario;
import com.inmo.security.AuthService;
import com.inmo.security.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final AuthService auth = new AuthService();

    @FXML
    public void onLogin(ActionEvent e) {
        String email = txtEmail.getText();
        String pass  = txtPassword.getText();

        Optional<Usuario> opt = auth.login(email, pass);
        if (opt.isEmpty()) {
            lblError.setText("Credenciales inválidas o usuario inactivo.");
            lblError.setVisible(true);
            return;
        }

        // Sesión
        Usuario current = opt.get();
        Session.setCurrent(current);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/menu.fxml"));
            Parent root = loader.load();

            // Pasar el usuario al MenuController para ocultar módulos según rol
            MenuController menu = loader.getController();
            menu.configureFor(current);

            Scene scene = new Scene(root);
            // CSS opcional
            var css = getClass().getResource("/css/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("INMO – Menú");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception ex) {
            lblError.setText("Error cargando el menú: " + ex.getMessage());
            lblError.setVisible(true);
            ex.printStackTrace();
        }
    }
}
