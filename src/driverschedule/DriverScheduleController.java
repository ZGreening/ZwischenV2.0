///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        DriverScheduleController.java
// Group:       3
// Date:        October 22, 2018
// Description: Controller Class for the ride schedule window
///////////////////////////////////////////////////////////////////////////////

package driverschedule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import other.Globals;

public class DriverScheduleController {

  @FXML
  private VBox monday;

  @FXML
  private VBox tuesday;

  @FXML
  private VBox wednesday;

  @FXML
  private VBox thursday;

  @FXML
  private VBox friday;

  @FXML
  private VBox saturday;

  @FXML
  private VBox sunday;

  @FXML
  private AnchorPane root;

  private ArrayList<Ride> dailyRides = new ArrayList<>();

  private void generateNewRideRow(VBox day, String origin, String destination, String time) {
    final GridPane gridPane = new GridPane();
    String dayString;

    //For storing day as a string
    if (day == monday) {
      dayString = "Monday";
    } else if (day == tuesday) {
      dayString = "Tuesday";
    } else if (day == wednesday) {
      dayString = "Wednesday";
    } else if (day == thursday) {
      dayString = "Thursday";
    } else if (day == friday) {
      dayString = "Friday";
    } else if (day == saturday) {
      dayString = "Saturday";
    } else if (day == sunday) {
      dayString = "Sunday";
    } else {
      dayString = "Not a day";
    }

    ComboBox<String> originBox = new ComboBox<>(Globals.getLocationList());
    ComboBox<String> destinationBox = new ComboBox<>(Globals.getLocationList());
    ComboBox<String> timeBox = new ComboBox<>(Globals.getTimeList());

    Button deleteButton = new Button();
    deleteButton.setText("Delete");
    deleteButton.setFont(new Font(12));

    final Ride ride = new Ride(originBox, destinationBox, timeBox, dayString);

    originBox.setEditable(true);
    destinationBox.setEditable(true);

    originBox.setFocusTraversable(false);
    destinationBox.setFocusTraversable(false);
    timeBox.setFocusTraversable(false);
    deleteButton.setFocusTraversable(false);

    //Give actions to all objects
    originBox.setOnAction(event -> {
      if (ride.rowIsFilled()) {
        generateNewRideRow(day, null, null, null);
      }
    });

    destinationBox.setOnAction(event -> {
      if (ride.rowIsFilled()) {
        generateNewRideRow(day, null, null, null);
      }
    });

    timeBox.setOnAction(event -> {
      if (ride.rowIsFilled()) {
        generateNewRideRow(day, null, null, null);
      }
    });

    deleteButton.setOnAction(event -> {
      if (ride.rowIsFilled()) {
        day.getChildren().remove(gridPane);
        dailyRides.remove(ride);

        //Delete from database
        try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
            Statement statement = connection.createStatement()) {
          statement.executeUpdate(String
              .format(
                  "DELETE from %s where (DAY='%s' and ORIGIN='%s' and DESTINATION='%s' and "
                      + "TIME='%s')", Globals.getCurrentUser().getUserFolder().toUpperCase(),
                  ride.day, ride.origin.getValue(), ride.destination.getValue(),
                  ride.time.getValue()));
        } catch (SQLException exception) {
          System.out.println("Unable to delete from database");
        }
      }
    });

    //If row has information, display it in the GUI
    if (origin != null && destination != null && time != null) {
      originBox.getSelectionModel().select(origin);
      destinationBox.getSelectionModel().select(destination);
      timeBox.getSelectionModel().select(time);
    }

    //Create gridpane
    gridPane.add(originBox, 0, 0);
    gridPane.add(destinationBox, 1, 0);
    gridPane.add(timeBox, 2, 0);
    gridPane.add(deleteButton, 3, 0);

    dailyRides.add(ride);

    day.getChildren().add(gridPane);
  }

  @FXML
  void onSavePressed(ActionEvent event) {
    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement()) {

      //Delete all rows so that there are no conflicting entries
      statement
          .executeUpdate(String.format("DELETE FROM %s", Globals.getCurrentUser().getUserFolder()));

      //Save all rides
      for (Ride ride : dailyRides) {
        if (ride.rowIsFilled()) {
          statement.executeUpdate(String.format("INSERT INTO %s VALUES('%s','%s','%s','%s')",
              Globals.getCurrentUser().getUserFolder(), ride.day, ride.origin.getValue(),
              ride.destination.getValue(),
              ride.time.getValue()));
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    Globals.closeScene(root);
  }

  @FXML
  void initialize() {
    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String
            .format("select * from %s", Globals.getCurrentUser().getUserFolder().toUpperCase()))) {

      //Load entries from database
      while (resultSet.next()) {
        String day = resultSet.getString("DAY");
        String origin = resultSet.getString("ORIGIN");
        String destination = resultSet.getString("DESTINATION");
        String time = resultSet.getString("TIME");

        switch (day) {
          case "Monday":
            generateNewRideRow(monday, origin, destination, time);
            break;
          case "Tuesday":
            generateNewRideRow(tuesday, origin, destination, time);
            break;
          case "Wednesday":
            generateNewRideRow(wednesday, origin, destination, time);
            break;
          case "Thursday":
            generateNewRideRow(thursday, origin, destination, time);
            break;
          case "Friday":
            generateNewRideRow(friday, origin, destination, time);
            break;
          case "Saturday":
            generateNewRideRow(saturday, origin, destination, time);
            break;
          case "Sunday":
            generateNewRideRow(sunday, origin, destination, time);
            break;
          default:
            System.out.println("Not a valid day");
        }
      }
    } catch (SQLException exception) {
      System.out.println("Unable to load  user data");
    }

    //Generate empty rows under all filled rows
    generateNewRideRow(monday, null, null, null);
    generateNewRideRow(tuesday, null, null, null);
    generateNewRideRow(wednesday, null, null, null);
    generateNewRideRow(thursday, null, null, null);
    generateNewRideRow(friday, null, null, null);
    generateNewRideRow(saturday, null, null, null);
    generateNewRideRow(sunday, null, null, null);
  }

  //Private class ride used for organization of dynamic javafx objects
  private class Ride {

    private ComboBox<String> origin;
    private ComboBox<String> destination;
    private ComboBox<String> time;
    private String day;

    private Ride(ComboBox<String> origin, ComboBox<String> destination, ComboBox<String> time,
        String day) {
      this.origin = origin;
      this.destination = destination;
      this.time = time;
      this.day = day;
    }

    private boolean rowIsFilled() {
      return (origin.getValue() != null && destination.getValue() != null
          && time.getValue() != null);
    }
  }
}
