///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        RideRequest.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for available drivers screen
///////////////////////////////////////////////////////////////////////////////

package riderequest;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import messages.MessagesController;
import other.Globals;
import other.Ride;

public class RideRequest implements Initializable {

  //private Message message;

  ObservableList<Ride> available = getRides();
  @FXML
  private AnchorPane root;
  @FXML
  private VBox scrollpaneVBox;
  @FXML
  private ComboBox<String> pickupComboBox;
  @FXML
  private ComboBox<String> destinationComboBox;
  @FXML
  private ComboBox<String> timeComboBox;
  @FXML
  private ComboBox<String> day;
  private ObservableList<String> time = FXCollections
      .observableArrayList("12:00 AM", "12:30 AM", "1:00 AM", "1:30 AM", "2:00 AM", "2:30 AM",
          "3:00 AM", "3:30 AM", "4:00 AM", "4:30 AM", "5:00 AM", "5:30 AM", "6:00 AM", "6:30 AM",
          "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
          "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM",
          "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM",
          "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM",
          "10:30 PM", "11:00 PM", "11:30 PM");

  private ObservableList<String> locations = FXCollections
      .observableArrayList("Coastal Village Apartments",
          "Coconut Point Mall", "Florida Gulf Coast University",
          "Florida SouthWestern State College",
          "Gulf Coast Town Center", "Miromar Outlets", "The Reef Apartments",
          "Walmart Supercenter (Estero)");


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    pickupComboBox.setItems(locations);
    destinationComboBox.setItems(locations);
    timeComboBox.setItems(time);
    day.setItems(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"));
  }

  @FXML
  void onCancelPressed(ActionEvent event) {
    Globals.closeScene(root);
  }


  @FXML
  void onSubmitPressed(ActionEvent event) {
    for (Ride ride : available) {
      String query = String.format(
          String.format("INSERT INTO RIDES VALUES('%s','%s','%s','%d','%s')", ride.getDriver(),
              Globals.getCurrentUser().getUsername(), ride.getDest(), ride.getStartP(),
              ride.getIdnumber(),
              ride.getDate()));

      if (ride.getCheckBox().isSelected()) {

        try (Connection conn133 = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
            Statement stmt133 = conn133.createStatement();
            ResultSet rs133 = stmt133.executeQuery(
                String.format("SELECT * FROM RIDES WHERE IDENTIFIER = %d", ride.getIdnumber()))) {

          if (rs133.next()) {
            System.out.println("Already sent. Go to Ride History and send a message");
          } else {
            stmt133.executeUpdate(query);
            System.out.println(("sent"));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }

        Globals.changeScene("messages/Messages.fxml");

        FXMLLoader loader = new FXMLLoader(
            getClass().getClassLoader().getResource("messages/Messages.fxml"));
        MessagesController controller = loader.getController();
        ComboBox<String> comboBox = controller.getRecipient();
        comboBox.getSelectionModel().select(ride.getDriver());
        available.remove(ride);
      }
    }
  }

  @FXML
  void onSearchPressed(ActionEvent event) {
    if (pickupComboBox.getValue() == null || destinationComboBox.getValue() == null
        || timeComboBox.getValue() == null) {
      System.out.println("values are null");
      return;
    }
    ArrayList<String> tableNames = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();

    String query = String.format("SELECT FOLDER, USERNAME FROM LOGIN");
    try (Connection conn130 = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement stmt130 = conn130.createStatement()) {
      ResultSet resultSet130 = stmt130.executeQuery(query);

      while (resultSet130.next()) {
        String username = resultSet130.getString("USERNAME");

        if (!username.equals(Globals.getCurrentUser().getUsername())) {
          tableNames.add(resultSet130.getString("FOLDER"));
          usernames.add(username);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    try (Connection conn130 = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement stmt130 = conn130.createStatement()) {
      for (int iii = 0; iii < tableNames.size(); iii++) {
        String tablename = tableNames.get(iii);
        String username = usernames.get(iii);
        ResultSet resultSet = stmt130.executeQuery(String.format("SELECT * FROM %s", tablename));

        if (resultSet.next()) {
          String day = resultSet.getString("DAY");
          String origin = resultSet.getString("ORIGIN");
          String destination = resultSet.getString("DESTINATION");
          String time = resultSet.getString("TIME");

          if (destination.equals(destinationComboBox.getValue()) && origin
              .equals(pickupComboBox.getValue())
              && time.equals(timeComboBox.getValue()) && day.equals(this.day.getValue())) {
            displayAride(username, origin, destination, time, day);
          }
        }
        resultSet.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private ObservableList<Ride> getRides() {
    ObservableList<Ride> rides = FXCollections.observableArrayList();
    String query = String
        .format("SELECT * FROM RIDES");
    try (Connection conn130 = DriverManager.getConnection(
        "jdbc:derby:lib/ZwischenDB");
        Statement stmt130 = conn130.createStatement();
        ResultSet resultSet130 = stmt130.executeQuery(query)) {
      if (resultSet130.next()) {
        while (resultSet130.next()) {
          Ride ride = new Ride(resultSet130.getString("DRIVER"),
              resultSet130.getString("RIDER"),
              resultSet130.getString("GOINGTO"),
              resultSet130.getString("COMINGFROM"),
              resultSet130.getDate("DATE"),
              resultSet130.getInt("IDENTIFIER"));
          rides.add(ride);
        }
      }
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
      System.out.println("something went wrong");
    }
    return rides;
  }

  private void displayAride(String username, String origin, String destination, String time,
      String day) {
    final GridPane gridPane = new GridPane();

    Label label = new Label();
    label.setText(username + " is driving from " + origin + " to " + destination
        + " at " + time + " on " + day);
    label.setWrapText(true);

    Button button = new Button();
    button.setText("Send Message");
    button.setOnAction(event -> System.out.println("sample message"));
    button.setAlignment(Pos.CENTER_RIGHT);

    gridPane.add(label, 0, 0);
    gridPane.add(button, 1, 0);

    gridPane.setStyle("-fx-background-color: silver");
    gridPane.setOnMouseEntered(e -> gridPane.setStyle("-fx-background-color: lightgrey"));
    gridPane.setOnMouseExited(e -> gridPane.setStyle("-fx-background-color: silver"));

    scrollpaneVBox.getChildren().add(gridPane);
  }
}
