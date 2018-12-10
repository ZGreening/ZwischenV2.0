///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        RideHistoryController.java
// Group:       3
// Date:        October 24, 2018
// Description: Controller class for ride history screen
///////////////////////////////////////////////////////////////////////////////

package ridehistory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import other.Globals;

public class RideHistoryController {

  @FXML
  private AnchorPane root;

  @FXML
  void onDeleteAllButtonPressed(ActionEvent event) {
    //Todo
  }

  @FXML
  void onDeleteCheckedButtonPressed(ActionEvent event) {
    //Todo
  }

  @FXML
  void onReturnHomeButtonPressed(ActionEvent event) {
    Globals.changeScene("mainscreen/MainScreen.fxml", root);
  }
}

