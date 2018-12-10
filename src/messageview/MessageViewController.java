///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        Message.java
// Group:       3
// Date:        November 24, 2018
// Description: The controller class for MessageView. Displays one message,
//    and gives the options to mark as read/unread, delete, or reply.
///////////////////////////////////////////////////////////////////////////////

package messageview;

import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import other.Globals;
import other.Message;

public class MessageViewController {

  private Message message;

  @FXML
  private AnchorPane root;

  @FXML
  private ImageView senderAvatar;

  @FXML
  private Label sender;

  @FXML
  private Label messageText;

  @FXML
  private Button markUnreadButton;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private Circle unreadIndicator;

  public MessageViewController(Message message) {
    this.message = message;
  }

  @FXML
  void onDeletePressed(ActionEvent event) {
    Alert alert = new Alert(AlertType.CONFIRMATION,
        "Do you really want to delete?\nThis cannot be undone.", ButtonType.OK, ButtonType.CANCEL);
    alert.showAndWait();

    if (alert.getResult() == ButtonType.OK) {
      ArrayList<Message> messages = Globals.getCurrentUser().getMessages();
      messages.remove(message);
      message.deleteFile();
      Globals.changeScene("notifications/Notifications.fxml", root);
    }
  }

  @FXML
  void onMarkAsUnreadPressed(ActionEvent event) {
    unreadIndicator.setVisible(!unreadIndicator.isVisible());
    message.setRead(!message.isRead());
    message.writeFile(); //Update message file
    if (markUnreadButton.getText().equals("Mark as Unread")) {
      markUnreadButton.setText("Mark as Read");
    } else {
      markUnreadButton.setText("Mark as Unread");
    }
  }

  @FXML
  void onReplyPressed(ActionEvent event) {
    Globals.loadMessagesWithSendTo(message.getSender(), root);
  }

  @FXML
  void onReturnPressed(ActionEvent event) {
    Globals.changeScene("notifications/Notifications.fxml", root);
  }

  @FXML
  void initialize() {
    messageText.setText("Message: " + message.getMessage());
    sender.setText("Sender:\n" + message.getSender());
    senderAvatar.setImage(new Image(
        Paths.get("lib/UserData/" + message.getSender() + "/defaultAvatar.png").toUri()
            .toString()));

    scrollPane.setFitToWidth(true);

    //If the message was unread, mark it as read
    if (!message.isRead()) {
      message.setRead(true);
      message.writeFile();
    }
  }
}

