package friendslist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import other.Globals;
import other.User;

public class FriendsListController {

  @FXML
  private AnchorPane root;

  @FXML
  private VBox friendsList;

  @FXML
  private ComboBox<String> addUserComboBox;

  private ObservableList<String> friendsListArray = FXCollections.observableArrayList();

  private void displayFriend(String friend) {
    GridPane gridPane = new GridPane();

    //Set column widths
    ColumnConstraints column = new ColumnConstraints();
    column.setPercentWidth(33);
    gridPane.getColumnConstraints().add(column);
    gridPane.getColumnConstraints().add(column);
    gridPane.getColumnConstraints().add(column);

    //Create friend username label
    Label label = new Label();
    label.setText(friend);
    label.setFont(new Font(14));

    //Create Send Message button
    Button sendMessage = new Button();
    sendMessage.setText("Send Message");
    sendMessage.setFocusTraversable(false);
    sendMessage.setPrefHeight(100);

    //Create Remove button
    Button removeFriend = new Button();
    removeFriend.setText("Remove");
    removeFriend.setFocusTraversable(false);
    removeFriend.setPrefHeight(100);

    //Send message button functionality
    sendMessage.setOnAction(event -> Globals.loadMessagesWithSendTo(friend, root));

    //Remove friend functionality
    removeFriend.setOnAction(event -> {
      for (User friendObject : Globals.getCurrentUser().getFriends()) {

        //Find and remove the friend object whose username matches the String friend
        if (friend.equals(friendObject.getUsername())) {
          Globals.getCurrentUser().getFriends().remove(friendObject);
          friendsListArray.remove(friendObject);
          friendsList.getChildren().remove(gridPane);

        }
        //Save to file
        Globals.getCurrentUser().storeFriends();
      }
    });

    removeFriend.setAlignment(Pos.CENTER_RIGHT);
    sendMessage.setAlignment(Pos.CENTER);

    gridPane.add(label, 0, 0);
    gridPane.add(sendMessage, 1, 0);
    gridPane.add(removeFriend, 2, 0);

    //Set color for mouse hover
    gridPane.setStyle("-fx-background-color: silver");
    gridPane.setOnMouseEntered(e -> gridPane.setStyle("-fx-background-color: lightgrey"));
    gridPane.setOnMouseExited(e -> gridPane.setStyle("-fx-background-color: silver"));

    friendsList.getChildren().add(gridPane);
  }

  @FXML
  void onAddUserPressed(ActionEvent event) {
    String newFriend = addUserComboBox.getValue();

    if (newFriend != null) {
      //Adds selected friend to the list view
      if (!friendsListArray.contains(newFriend)) {
        friendsListArray.add(newFriend);
      }

      try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
          Statement statement = connection.createStatement();
          ResultSet resultSet = statement
              .executeQuery(String.format("SELECT * from LOGIN where USERNAME='%s'", newFriend))) {
        if (resultSet.next()) {
          String phoneNumber = resultSet.getString("PNUMBER");
          String email = resultSet.getString("EMAIL");
          Globals.getCurrentUser().getFriends().add(new User(newFriend, email, phoneNumber));
        }
      } catch (SQLException exception) {
        System.out.println("Failed to get friend information from database");
      }

      Globals.getCurrentUser().storeFriends();

      displayFriend(newFriend);

      // Removes friend from the combobox
      if (friendsListArray.contains(newFriend)) {
        addUserComboBox.getItems().remove(newFriend);
      }
    }
  }

  @FXML
  void onReturnHomePressed(ActionEvent event) {
    Globals.closeScene(root);
  }

  @FXML
  void initialize() {
    ArrayList<String> usernames = Globals.getAllUsernames();
    usernames.remove(Globals.getCurrentUser().getUsername());
    addUserComboBox.setItems(FXCollections.observableArrayList(usernames));

    for (User friend : Globals.getCurrentUser().getFriends()) {
      friendsListArray.add(friend.getUsername());
      addUserComboBox.getItems().remove(friend.getUsername());
      displayFriend(friend.getUsername());
    }
  }
}
