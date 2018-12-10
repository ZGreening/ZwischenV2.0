///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        RideHistoryController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for ride history screen
///////////////////////////////////////////////////////////////////////////////

package ridehistory;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import other.Globals;
import other.PastRide;

public class RideHistoryController implements Initializable {

  ObservableList<PastRide> past = getPastRides();
  @FXML
  private AnchorPane root;
  @FXML
  private TableView<PastRide> availableDriversTableview;
  @FXML
  private TableColumn<PastRide, String> driverColumn;
  @FXML
  private TableColumn<PastRide, String> fromColumn;
  @FXML
  private TableColumn<PastRide, String> toColumn;
  @FXML
  private TableColumn<PastRide, Date> dateColumn;
  @FXML
  private TableColumn<PastRide, Button> messageColumn;
  @FXML
  private TableColumn<PastRide, CheckBox> deleteColumn;
  @FXML
  private Label feedbackLabel;
  @FXML
  private Button deleteAllButton;

  @FXML
  private Button deleteCheckedButton;

  @FXML
  private Button returnHomeButton;

  @FXML
  void onDeleteAllButtonPressed(ActionEvent event) throws SQLException {

    ObservableList<PastRide> rides2 = FXCollections.observableArrayList();

    try (Connection conn12p = DriverManager.getConnection(
        "jdbc:derby:lib/ZwischenDB");
        Statement stmt12p = conn12p.createStatement()) {
      for (PastRide ride : past) {
        String query = String.format(String.format(
            "DELETE FROM RIDES WHERE IDENTIFIER = '%d' ", ride.getIdnumber()));

        rides2.add(ride);
        stmt12p.executeUpdate(query);
      }

      System.out.println(("deleted"));
      past.remove(rides2);
    }


  }

  @FXML
  void onDeleteCheckedButtonPressed(ActionEvent event) throws SQLException {

    ObservableList<PastRide> rides = FXCollections.observableArrayList();

    for (PastRide ride : past) {
      String query = String.format(
          String.format("DELETE FROM RIDE WHERE IDENTIFIER = '%d' ", ride.getIdnumber()));

      if (ride.getCheckBox().isSelected()) {

        rides.add(ride);
        try (Connection conn123 = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB")) {
          Statement stmt123 = conn123.createStatement();

          stmt123.executeUpdate(query);
          stmt123.close();
        }

        System.out.println(("deleted"));
        past.remove(ride);
      }

    }

  }

  @FXML
  void onReturnHomeButtonPressed(ActionEvent event) {

    Globals.changeScene("mainscreen/MainScreen.fxml", root);

  }

  private ObservableList<PastRide> getPastRides() {

    ObservableList<PastRide> pastRides = FXCollections.observableArrayList();
    String query = String.format("SELECT * FROM RIDES WHERE DRIVER = '%s'",
        Globals.getCurrentUser().getUsername());

    try (Connection conn120 = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement stmt120 = conn120.createStatement();
        ResultSet resultSet120 = stmt120.executeQuery(query)) {
      if (resultSet120.wasNull()) {

        System.out.println("no history to show");

      } else {
        while (resultSet120.next()) {
          PastRide pastRide = new PastRide(resultSet120.getString("DRIVER"),
              resultSet120.getString("RIDER"),
              resultSet120.getString("GOINGTO"),
              resultSet120.getString("COMINGFROM"),
              resultSet120.getDate("DATE"),
              resultSet120.getInt("IDENTIFIER"));
          pastRides.add(pastRide);
        }
      }
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
      System.out.println("something went wrong");
    }
    return pastRides;
  }


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    driverColumn.setCellValueFactory(new PropertyValueFactory<PastRide, String>("driver"));
    toColumn.setCellValueFactory(new PropertyValueFactory<PastRide, String>("to"));
    fromColumn.setCellValueFactory(new PropertyValueFactory<PastRide, String>("StartP"));
    dateColumn.setCellValueFactory(new PropertyValueFactory<PastRide, Date>("date"));
    messageColumn.setCellValueFactory(new PropertyValueFactory<PastRide, Button>("message"));
    deleteColumn.setCellValueFactory(new PropertyValueFactory<PastRide, CheckBox>("checkBox"));

    availableDriversTableview.setItems(past);

  }
}

