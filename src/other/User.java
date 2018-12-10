///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        User.java
// Group:       3
// Date:        November 3, 2018
// Description: Concrete class user represents a driver or rider user
//    of the program.
///////////////////////////////////////////////////////////////////////////////

package other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;

public class User implements Serializable {

  private String username;
  private String email;
  private String phoneNum;
  private String userFolder;
  private ArrayList<Message> messages = new ArrayList<>();
  private ArrayList<User> friends = new ArrayList<>();

  /**
   * An overloaded constructor for the class User.
   *
   * @param username the user's username
   * @param email the user's email
   * @param phoneNum the user's phoneNum
   */
  public User(String username, String email, String phoneNum) {
    this.username = username;
    this.email = email;
    this.phoneNum = phoneNum;
  }

  public User() {
  }

  public ArrayList<User> getFriends() {
    return friends;
  }

  public ArrayList<Message> getMessages() {
    return messages;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNum() {
    return phoneNum;
  }

  public String getUserFolder() {
    return userFolder;
  }

  /**
   * A method for loading the current user's friends list.
   */
  public void loadFriends() {
    Path path = Paths.get("lib/UserData/" + userFolder + "/");
    Path file = path.resolve("friends.list");

    try (FileInputStream fileInputStream = new FileInputStream(file.toString());
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

      friends = (ArrayList<User>) objectInputStream.readObject();

    } catch (ClassNotFoundException exception) {
      System.out.println("Failed to load friends.list");
    } catch (IOException exception) {
      //Friends list does not exist
    }
  }

  /**
   * A method for saving the current user's friends list.
   */
  public void storeFriends() {
    Path path = Paths.get("lib/UserData/" + userFolder + "/");
    Path file = path.resolve("friends.list");

    System.out.println(file.toString());

    try (FileOutputStream fileOutputStream = new FileOutputStream(file.toString());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

      objectOutputStream.writeObject(friends);

    } catch (IOException exception) {
      System.out.println("Failed to save friends.list");
    }
  }

  /**
   * A function to deserialize all the message files in a users messages folder and load them into
   * the messages ArrayList. Messages are sorted by the date that they were created.
   */
  private void loadMessages() {
    Path path = Paths.get("lib/UserData/" + userFolder + "/messages");
    File file = new File(path.toString());

    if (file != null) {
      File[] messageFiles = file.listFiles();

      if (messageFiles != null) {
        for (File messageFile : messageFiles) {
          try (FileInputStream fileInputStream = new FileInputStream(messageFile);
              ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            Message message = (Message) objectInputStream.readObject();
            messages.add(message);

          } catch (IOException exception) {
            System.out.println("IOException: unable to read message file " + messageFile);
          } catch (ClassNotFoundException exception) {
            System.out
                .println("ClassNotFoundException: unable to read message file " + messageFile);
          }
        }

        //Sorts messages by the time that they were received, newest messages first
        Collections.sort(messages);
      }
    }
  }

  /**
   * Saves a user image to the lib/UserData folder. Username must be unique, otherwise the file will
   * not save.
   */
  public void saveUserImage(File file, boolean overwrite) {
    CopyOption[] copyOptions;

    if (overwrite) {
      copyOptions = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES,
          StandardCopyOption.REPLACE_EXISTING};
    } else {
      copyOptions = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};
    }

    //If an image file path was not loaded, use default defaultAvatar.png
    if (file == null) {
      file = new File("lib/defaultAvatar.png");
    }

    //Copy image to users folder
    try {
      Files.copy(Paths.get(file.getAbsolutePath()),
          Paths.get("lib/UserData/" + Globals.getCurrentUser().getUserFolder() + "/avatar.png"),
          copyOptions);
    } catch (IOException exception) {
      System.out.println("Unable to save image\n" + Globals.getCurrentUser().getUserFolder()
          + "/avatar.png may already exist");
    }
  }

  /**
   * A helper method to reset all userdata to default on user logout.
   */
  public void logoutUser() {
    username = null;
    email = null;
    phoneNum = null;
    userFolder = null;
    messages = new ArrayList<>(); //unload messages
  }

  /**
   * A helper method to set all current user data on login.
   *
   * @param username current user's username
   * @param email current user's email
   * @param phoneNum current user's phoneNum
   * @param userFolder current user's userFolder
   */
  public void loginUser(String username, String email, String phoneNum, String userFolder) {
    this.username = username;
    this.email = email;
    this.phoneNum = phoneNum;
    this.userFolder = userFolder;
    loadMessages();
    loadFriends();
  }
}
