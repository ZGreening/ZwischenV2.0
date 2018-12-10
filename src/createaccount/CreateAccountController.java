///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        CreateAccountController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for create account screen
///////////////////////////////////////////////////////////////////////////////

package createaccount;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

public class CreateAccountController {

  private File file;

  @FXML
  private TextField username;

  @FXML
  private PasswordField password;

  @FXML
  private PasswordField confirmPassword;

  @FXML
  private TextField email;

  @FXML
  private TextField phoneNum;

  @FXML
  private ImageView avatar;

  @FXML
  private Label feedbackLabel;

  @FXML
  private AnchorPane root;

  private void createUserFolder() {
    try {
      Path path1 = Paths.get("lib/UserData/" + Globals.getCurrentUser().getUserFolder());
      Path path2 = path1.resolve("messages");

      //Create user folder and user messages folder
      Files.createDirectory(path1);
      Files.createDirectory(path2);
    } catch (IOException exception) {
      System.out.println("Unable to create user directory");
    }
  }

  /**
   * Generates a unique user foldername. Folder name will be the same as the username in all lower
   * case. If a folder already exists with the users username, then a number will be appended to the
   * foldername.
   *
   * @param username the username to generate a folder name for
   * @return the folder name as a string
   */
  private String generateUniqueFolderName(String username) {
    File file = new File("lib/UserData");
    File[] files = file.listFiles();
    boolean uniqueNameFound = false;
    String uniqueName = username.toLowerCase();
    int iii = 1;

    while (!uniqueNameFound) {
      uniqueNameFound = true;

      if (iii > 1) {
        uniqueName = username.toLowerCase() + iii;
      }

      for (File file1 : files) {
        if (file1.getName().toLowerCase().equals(uniqueName)) {
          uniqueNameFound = false;
          iii++;
          break;
        }
      }
    }

    return uniqueName;
  }

  /**
   * A function to store a new user account to the database. The function gets a unique foldername,
   * creates the user folders and saves the user image. It then logs in as the user. **Must have a
   * unique username**
   *
   * @param username new user's username
   * @param password new user's password
   * @param email new user's email
   * @param phoneNum new user's phone number
   */
  private void storeAccountAndLogin(String username, String password, String email,
      String phoneNum) {
    String folderName = generateUniqueFolderName(username);

    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement
            .executeQuery(String.format("SELECT * FROM LOGIN WHERE USERNAME='%s'", username))) {

      if (resultSet.next()) {
        feedbackLabel.setText("User already exists");
      } else {
        statement.executeUpdate(String
            .format("INSERT INTO LOGIN VALUES('%s','%s','%s','%s','%s')", username, password, email,
                phoneNum, folderName));
        Globals.getCurrentUser().loginUser(username, email, phoneNum, folderName);
        createUserFolder();
        Globals.getCurrentUser().saveUserImage(file, false);
        Globals.changeScene("mainscreen/MainScreen.fxml", root);
      }

    } catch (SQLException exception) {
      System.out.println("Unable to create new user: " + username);
      exception.printStackTrace();
    }
  }

  @FXML
  void onCreateAccountPressed(ActionEvent event) {
    //Get text strings
    String usernameText = username.getText();
    String passwordText = password.getText();
    String confirmPasswordText = confirmPassword.getText();
    String emailText = email.getText();
    String phoneNumText = phoneNum.getText();

    //Ensure all fields are filled and email and phone number are in correct format
    if (usernameText.isEmpty()) {
      feedbackLabel.setText("Username is empty");
    } else if (usernameText.length() < 5) {
      feedbackLabel.setText("Username must be at least 5 characters long");
    } else if (passwordText.isEmpty()) {
      feedbackLabel.setText("Password is empty");
    } else if (passwordText.length() < 5) {
      feedbackLabel.setText("Password must be at least 5 characters long");
    } else if (confirmPasswordText.isEmpty()) {
      feedbackLabel.setText("Confirm password is empty");
    } else if (confirmPasswordText.length() < 5) {
      feedbackLabel.setText("Confirm Password must be at least 5 characters long");
    } else if (!passwordText.equals(confirmPasswordText)) {
      feedbackLabel.setText("Passwords do not match");
    } else if (emailText.isEmpty()) {
      feedbackLabel.setText("Email is empty");
    } else if (!emailText
        .matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
      feedbackLabel.setText("Invalid Email");
    } else if (phoneNumText.isEmpty()) {
      feedbackLabel.setText("Phone number is empty");
    } else if (!(phoneNumText.matches("\\([0-9]{3}\\)[0-9]{3}-[0-9]{4}")
        || phoneNumText.matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")
        || phoneNumText.matches("[0-9]{10}"))) {
      feedbackLabel.setText("Incorrect phone number format");
    } else {

      phoneNumText = Globals.formatPhoneNum(phoneNumText);

      storeAccountAndLogin(usernameText, passwordText, emailText, phoneNumText);

      //Create a table for the new user to store their schedule
      try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
          Statement statement = connection.createStatement()) {

        statement.executeUpdate(String.format(
            "create table %s(DAY VARCHAR(10),ORIGIN VARCHAR(255),DESTINATION VARCHAR(255),"
                + "TIME VARCHAR(10))", Globals.getCurrentUser().getUserFolder()));

      } catch (SQLException exception) {
        System.out.println("Unable to create user table");
      }
    }
  }

  @FXML
  void onReturnToLoginPressed(ActionEvent event) {
    Globals.changeScene("login/Login.fxml", root);
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
  void initialize() {
    //Set up image to use current user's username for image, "default" by default
    try {
      avatar.setImage(new Image(Paths.get("lib/defaultAvatar.png").toUri().toURL().toString()));
    } catch (MalformedURLException exception) {
      exception.printStackTrace();
    }
  }
}
