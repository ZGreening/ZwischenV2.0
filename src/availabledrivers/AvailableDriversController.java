///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        AvailableDriversController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for available drivers screen
///////////////////////////////////////////////////////////////////////////////

package availabledrivers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import other.Globals;

public class AvailableDriversController implements Initializable {

  @FXML
  private AnchorPane root;

  @FXML
  private VBox display;

  @FXML
  private ComboBox<String> originComboBox;

  @FXML
  private ComboBox<String> destinationComboBox;

  @FXML
  private ComboBox<String> timeComboBox;

  @FXML
  private ComboBox<String> day;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    originComboBox.setItems(Globals.getLocationList());
    destinationComboBox.setItems(Globals.getLocationList());
    timeComboBox.setItems(Globals.getTimeList());
    day.setItems(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"));
  }

  @FXML
  void onCancelPressed(ActionEvent event) {
    Globals.closeScene(root);
  }

  @FXML
  void onSubmitPressed(ActionEvent event) {

  }

  @FXML
  void onSearchPressed(ActionEvent event) {
    if (originComboBox.getValue() == null || destinationComboBox.getValue() == null
        || timeComboBox.getValue() == null) {
      System.out.println("values are null");
      return;
    }

    ArrayList<String> tableNames = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();

    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT FOLDER, USERNAME FROM LOGIN")) {

      while (resultSet.next()) {
        String username = resultSet.getString("USERNAME");

        //If the username being searched is the current user, ignore his driver schedule
        if (!username.equals(Globals.getCurrentUser().getUsername())) {
          tableNames.add(resultSet.getString("FOLDER"));
          usernames.add(username);
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement()) {

      for (int iii = 0; iii < tableNames.size(); iii++) {
        String tablename = tableNames.get(iii);
        String username = usernames.get(iii);

        ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s", tablename));

        if (resultSet.next()) {
          String day = resultSet.getString("DAY");
          String origin = resultSet.getString("ORIGIN");
          String destination = resultSet.getString("DESTINATION");
          String time = resultSet.getString("TIME");

          if (destination.equals(destinationComboBox.getValue()) && origin
              .equals(originComboBox.getValue())
              && time.equals(timeComboBox.getValue()) && day.equals(this.day.getValue())) {
            displayRide(username, origin, destination, time, day);
          }

        }

        resultSet.close();
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  private void displayRide(String username, String origin, String destination, String time,
      String day) {
    final GridPane gridPane = new GridPane();

    Label label = new Label();
    label.setText("Driver: " + username + " is driving from " + origin + "\nto " + destination
        + " at " + time + " on " + day);
    label.setWrapText(true);

    Button button = new Button();
    button.setText("Send Message");
    button.setOnAction(event -> System.out.println("sample message"));
    button.setAlignment(Pos.CENTER_RIGHT);

    GridPane.setMargin(label, new Insets(5));
    GridPane.setMargin(button, new Insets(5));

    gridPane.add(label, 0, 0);
    gridPane.add(button, 1, 0);

    gridPane.setStyle("-fx-background-color: silver");
    gridPane.setOnMouseEntered(e -> gridPane.setStyle("-fx-background-color: lightgrey"));
    gridPane.setOnMouseExited(e -> gridPane.setStyle("-fx-background-color: silver"));

    display.getChildren().add(gridPane);
  }
}
