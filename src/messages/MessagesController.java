///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        MessagesController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for message screen
///////////////////////////////////////////////////////////////////////////////

package messages;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import other.Globals;
import other.Message;

public class MessagesController {

  @FXML
  private TextArea message;

  @FXML
  private ComboBox<String> recipient;

  @FXML
  private AnchorPane root;

  public ComboBox<String> getRecipient() {
    return recipient;
  }

  @FXML
  void onCancelPressed(ActionEvent event) {
    Globals.closeScene(root);
  }

  @FXML
  void onSendPressed(ActionEvent event) {
    if (recipient.getValue() == null) {
      return;
    } else if (message.getText().isEmpty()) {
      return;
    }

    Message message = new Message(this.message.getText(), recipient.getValue(),
        Globals.getCurrentUser().getUsername());
    message.sendMessage();

    Globals.closeScene(root);
  }

  @FXML
  void initialize() {
    ArrayList<String> usernames = Globals.getAllUsernames();
    usernames
        .remove(
            Globals.getCurrentUser().getUsername());    //So you cannot send a message to yourself
    recipient.setItems(FXCollections.observableArrayList(usernames));
  }
}
