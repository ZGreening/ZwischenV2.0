///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        NotificationsController.java
// Group:       3
// Date:        October 20, 2018
// Description: Controller Class for the notifications window
///////////////////////////////////////////////////////////////////////////////

package notifications;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import messageview.MessageViewController;
import other.Globals;
import other.Message;

public class NotificationsController {

  private ArrayList<GridPane> messageDisplays = new ArrayList<>();

  @FXML
  private AnchorPane root;

  @FXML
  private VBox messageOutput;

  private void openMessageView(Message message) {
    try {
      Stage stage = new Stage();
      FXMLLoader loader = new FXMLLoader(
          getClass().getClassLoader().getResource("messageview/MessageView.fxml"));
      MessageViewController messageViewController = new MessageViewController(message);

      //load scene with instantiated controller
      loader.setController(messageViewController);
      AnchorPane anchorPane = loader.load();
      Scene scene = new Scene(anchorPane);

      //Set stage
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setTitle("Zwischen");
      stage.show();

      //Close old stage
      stage = (Stage) root.getScene().getWindow();
      stage.close();
    } catch (IOException exception) {
      System.out.println("Unable to open message view");
    }

  }

  @FXML
  void onDeletePressed(ActionEvent event) {
    ArrayList<Message> messages = Globals.getCurrentUser().getMessages();
    ArrayList<Message> deleted = new ArrayList<>();
    boolean firstTime = true;

    for (GridPane gridPane : messageDisplays) {
      CheckBox checkBox = (CheckBox) gridPane.getChildren().get(4);
      if (checkBox.isSelected()) {
        if (firstTime) {
          firstTime = false;

          //Prompt confirmation dialog first time
          Alert alert = new Alert(AlertType.CONFIRMATION,
              "Do you really want to delete?\nThis cannot be undone.", ButtonType.OK,
              ButtonType.CANCEL);
          alert.showAndWait();

          //If the user does not want to delete, return early
          if (alert.getResult() != ButtonType.OK) {
            return;
          }
        }

        //Delete message
        Message message = messages.get(messageDisplays.indexOf(gridPane));
        message.deleteFile();
        deleted.add(message);
        messageOutput.getChildren().remove(gridPane);
      }
    }

    //Delete messages from currentUsers messages attribute
    for (Message message : deleted) {
      messages.remove(message);
    }
  }

  @FXML
  void onMarkAsReadPressed(ActionEvent event) {
    ArrayList<Message> messages = Globals.getCurrentUser().getMessages();

    for (GridPane gridPane : messageDisplays) {
      CheckBox checkBox = (CheckBox) gridPane.getChildren().get(4);
      if (checkBox.isSelected()) {
        Message message = messages.get(messageDisplays.indexOf(gridPane));
        message.setRead(true);
        message.writeFile();  //Rewrite the file so it contains the new value

        //Hide unread message indicator
        Circle circle = (Circle) gridPane.getChildren().get(0);
        circle.setVisible(false);

        //Uncheck checkbox
        checkBox.setSelected(false);
      }
    }
  }

  @FXML
  void onDeselectAllPressed(ActionEvent event) {
    for (GridPane gridPane : messageDisplays) {
      CheckBox checkBox = (CheckBox) gridPane.getChildren().get(4);
      checkBox.setSelected(false);
    }
  }

  @FXML
  void onReturnPressed(ActionEvent event) {
    Globals.closeScene(root);
  }

  @FXML
  void onSelectAllPressed(ActionEvent event) {
    for (GridPane gridPane : messageDisplays) {
      CheckBox checkBox = (CheckBox) gridPane.getChildren().get(4);
      checkBox.setSelected(true);
    }
  }

  @FXML
  void onMarkAsUnreadPressed(ActionEvent event) {
    ArrayList<Message> messages = Globals.getCurrentUser().getMessages();

    for (GridPane gridPane : messageDisplays) {
      CheckBox checkBox = (CheckBox) gridPane.getChildren().get(4);
      if (checkBox.isSelected()) {
        Message message = messages.get(messageDisplays.indexOf(gridPane));
        message.setRead(false);
        message.writeFile();  //Rewrite the file so it contains the new value

        //Show unread message indicator
        Circle circle = (Circle) gridPane.getChildren().get(0);
        circle.setVisible(true);

        //Uncheck checkbox
        checkBox.setSelected(false);
      }
    }
  }

  @FXML
  void initialize() {
    for (Message message : Globals.getCurrentUser().getMessages()) {
      GridPane gridPane = new GridPane();

      //Set column spacing
      ColumnConstraints column = new ColumnConstraints();
      column.setPercentWidth(5);
      gridPane.getColumnConstraints().add(column);

      column = new ColumnConstraints();
      column.setPercentWidth(15);
      gridPane.getColumnConstraints().add(column);
      gridPane.getColumnConstraints().add(column);

      column = new ColumnConstraints();
      column.setPercentWidth(45);
      gridPane.getColumnConstraints().add(column);

      column = new ColumnConstraints();
      column.setPercentWidth(20);
      gridPane.getColumnConstraints().add(column);

      //Create visible marker for read and unread files
      Circle circle = new Circle();
      circle.setRadius(5);
      circle.setStyle("-fx-fill: royalblue");
      if (message.isRead()) {
        circle.setVisible(false);
      }

      //Get sender
      Label sender = new Label();
      sender.setText("Sender:\n" + message.getSender());
      sender.setFont(new Font(14));

      //Get sender's avatar
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(
          Paths.get("lib/UserData/" + message.getSender() + "/defaultAvatar.png").toUri()
              .toString()));
      imageView.setFitHeight(75);
      imageView.setFitWidth(75);

      //Get message
      Label messageLabel = new Label();
      messageLabel.setText("Message:\n" + message.getMessage());
      sender.setFont(new Font(14));
      messageLabel.setWrapText(true);

      //Add checkbox
      CheckBox checkBox = new CheckBox();
      checkBox.setText("Select");
      checkBox.setFont(new Font(14));
      checkBox.setFocusTraversable(false);

      //Add message
      gridPane.add(circle, 0, 0);
      gridPane.add(imageView, 1, 0);
      gridPane.add(sender, 2, 0);
      gridPane.add(messageLabel, 3, 0);
      gridPane.add(checkBox, 4, 0);

      //Set color for mouse hover
      gridPane.setStyle("-fx-background-color: silver");
      gridPane.setOnMouseEntered(e -> gridPane.setStyle("-fx-background-color: lightgrey"));
      gridPane.setOnMouseExited(e -> gridPane.setStyle("-fx-background-color: silver"));

      //Set ability to read individual message in messageView by clicking on it
      gridPane.setOnMouseClicked(e -> openMessageView(message));

      messageOutput.getChildren().add(gridPane);
      messageDisplays.add(gridPane);
    }
  }
}
