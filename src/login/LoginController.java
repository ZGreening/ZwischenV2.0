///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        LoginController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for login window
///////////////////////////////////////////////////////////////////////////////

package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import other.Globals;


public class LoginController {

  @FXML
  private Label feedbackLabel;

  @FXML
  private PasswordField password;

  @FXML
  private TextField username;

  @FXML
  private AnchorPane root;

  @FXML
  void onCreateAccountPressed(ActionEvent event) {
    Globals.changeScene("createaccount/CreateAccount.fxml", root);
  }

  private void checkAndLogin(String username, String password) {
    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LOGIN")) {
      boolean usernameFound = false;

      while (resultSet.next()) {
        //Get username and password from row
        String databaseUsername = resultSet.getString("USERNAME");
        String databasePassword = resultSet.getString("PASSWORD");
        String databaseEmail = resultSet.getString("EMAIL");
        String databasePhoneNumber = resultSet.getString("PNUMBER");
        String databaseFolder = resultSet.getString("FOLDER");

        //Determine if user entered password and username match
        if ((username.equals(databaseUsername)) && (password.equals(databasePassword))) {
          Globals.getCurrentUser()
              .loginUser(databaseUsername, databaseEmail, databasePhoneNumber, databaseFolder);
          Globals.changeScene("mainscreen/MainScreen.fxml", root);
          return;
        } else if (username.equals(databaseUsername)) {
          usernameFound = true;
        }
      }

      if (usernameFound) {
        feedbackLabel.setText("Password is incorrect");
        this.password.selectAll();
        this.password.requestFocus();
      } else {
        feedbackLabel.setText("Username does not exist");
        this.password.clear();
        this.username.selectAll();
        this.username.requestFocus();
      }

    } catch (SQLException sqlExcept) {
      System.out.println("Unable to check login database");
    }
  }

  @FXML
  void onLoginPressed(ActionEvent event) {
    String localuser = username.getText();
    String localpass = password.getText();

    if ((localuser.isEmpty()) && (localpass.isEmpty())) {
      feedbackLabel.setText("Username and Password is empty");
    } else if (localuser.isEmpty()) {
      feedbackLabel.setText("Username Is Empty");
    } else if (localpass.isEmpty()) {
      feedbackLabel.setText("Password is empty");
    } else if (localuser.length() < 5) {
      feedbackLabel.setText("Username must be more than 5 characters");
    } else if (localpass.length() < 5) {
      feedbackLabel.setText("Password must be more than 5 characters");
    } else {
      checkAndLogin(localuser, localpass);
    }
  }
}
