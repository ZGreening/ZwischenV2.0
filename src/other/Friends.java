///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        Friends.java
// Group:       3
// Date:        November 26, 2018
// Description: Concrete class Friends represents the link between users
///////////////////////////////////////////////////////////////////////////////

package other;

import javafx.scene.control.Button;

public class Friends extends User {

  private String friendName;
  private Button removeFriend;
  private Button messsegeFriend;

  /**
   * Constructor for the class Friends.
   *
   * @param friendName the name of the friend
   */
  public Friends(String friendName) {

    this.friendName = friendName;
    this.removeFriend = new Button();
    this.messsegeFriend = new Button();
  }

  public String getFriendName() {
    return friendName;
  }

  public void setFriendName(String friendName) {
    this.friendName = friendName;
  }

  public Button getRemoveFriend() {
    return removeFriend;
  }

  public void setRemoveFriend(Button removeFriend) {
    this.removeFriend = removeFriend;
  }

  public Button getMesssegeFriend() {
    return messsegeFriend;
  }

  public void setMesssegeFriend(Button messsegeFriend) {
    this.messsegeFriend = messsegeFriend;
  }

}
