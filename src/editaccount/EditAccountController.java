///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        EditAccountController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for edit account window
///////////////////////////////////////////////////////////////////////////////

package editaccount;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import other.Globals;

public class EditAccountController {

  @FXML
  private AnchorPane root;

  @FXML
  private ImageView avatar;

  @FXML
  private Label username;

  @FXML
  private PasswordField password;

  @FXML
  private TextField phoneNum;

  @FXML
  private TextField email;

  @FXML
  private PasswordField confirmPassword;

  @FXML
  private Label feedbackLabel;

  //saveUserImage will replace userImage with default if file==null, so file should not be null
  private File file = new File(
      Paths.get("lib/UserData/" + Globals.getCurrentUser().getUserFolder() + "/avatar.png")
          .toString());

  @FXML
  void onReturnToMainScreenPressed(ActionEvent event) {
    Globals.changeScene("mainscreen/MainScreen.fxml", root);
  }

  private void updateAccount(String passToUpdate, String emailToUpdate,
      String phoneNumberToUpdate) {
    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement()) {

      if (!passToUpdate.isEmpty()) {
        statement.executeUpdate(String
            .format("UPDATE LOGIN SET PASSWORD = '%s' WHERE USERNAME = '%s'", passToUpdate,
                Globals.getCurrentUser().getUsername()));
      } else {
        feedbackLabel.setText("Password not updated");
      }

      statement.executeUpdate(String
          .format("UPDATE LOGIN SET EMAIL = '%s' WHERE USERNAME = '%s'", emailToUpdate,
              Globals.getCurrentUser().getUsername()));
      statement.executeUpdate(String
          .format("UPDATE LOGIN SET PNUMBER = '%s' WHERE USERNAME = '%s'", phoneNumberToUpdate,
              Globals.getCurrentUser().getUsername()));

      //Update new user information by logging in with new email and phone number
      Globals.getCurrentUser().loginUser(Globals.getCurrentUser().getUsername(),
          emailToUpdate, phoneNumberToUpdate, Globals.getCurrentUser().getUserFolder());

      Globals.getCurrentUser().saveUserImage(file, true);
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
    }
  }

  @FXML
  void onUpdateAccountPressed(ActionEvent event) {
    String newpass = password.getText();
    String newConfirmPass = confirmPassword.getText();
    String newPNumber = phoneNum.getText();
    String newEmail = email.getText();

    if (!newpass.equals(newConfirmPass)) {
      feedbackLabel.setText("Passwords do not match");
    } else if (!newpass.isEmpty() && newpass.length() < 5) {
      feedbackLabel.setText("Password must be atleast 5 characters long");
    } else if (newEmail.isEmpty()) {
      feedbackLabel.setText("Email is empty");
    } else if (!newEmail
        .matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
      feedbackLabel.setText("Invalid Email");
    } else if (newPNumber.isEmpty()) {
      feedbackLabel.setText("Phone number is empty");
    } else if (!(newPNumber.matches("\\([0-9]{3}\\)[0-9]{3}-[0-9]{4}")
        || newPNumber.matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")
        || newPNumber.matches("[0-9]{10}"))) {
      feedbackLabel.setText("Incorrect phone number format");
    } else {
      newPNumber = Globals.formatPhoneNum(newPNumber);
      updateAccount(newpass, newEmail, newPNumber);
      Globals.changeScene("mainscreen/MainScreen.fxml", root);
    }
  }

  @FXML
  void onUploadPressed(ActionEvent event) {
    //Open up file chooser to user's documents directory
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));

    //Only allow jpeg jpg and png to be selected
    ArrayList<String> extensionList = new ArrayList<>();
    extensionList.add("*.jpeg");
    extensionList.add("*.jpg");
    extensionList.add("*.png");
    fileChooser.getExtensionFilters().addAll(
        new ExtensionFilter("PNG, JPG, or JPEG", extensionList));

    //Open file chooser
    file = fileChooser.showOpenDialog(root.getScene().getWindow());

    //When a file is selected, set as avatar
    if (file != null) {
      avatar.setImage(new Image(file.toURI().toString()));
    }
  }

  @FXML
  void onPhoneNumMouseClicked() {
    phoneNum.selectAll();
  }

  @FXML
  void onEmailMouseClicked() {
    email.selectAll();
  }

  @FXML
  void initialize() {
    //Set up image to use current user's username for image, "default" by default
    avatar.setImage(new Image(
        Paths.get("lib/UserData/" + Globals.getCurrentUser().getUserFolder() + "/avatar.png")
            .toUri().toString()));

    //display username
    username.setText(Globals.getCurrentUser().getUsername());

    //Display current user's phone number
    phoneNum.setText("(" + Globals.getCurrentUser().getPhoneNum().substring(0, 3) + ")"
        + Globals.getCurrentUser().getPhoneNum().substring(3, 6) + "-"
        + Globals.getCurrentUser().getPhoneNum().substring(6, 10));

    //Display current user's email
    email.setText(Globals.getCurrentUser().getEmail());
  }
}

