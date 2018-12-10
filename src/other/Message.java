///////////////////////////////////////////////////////////////////////////////
// Project:     Zwischen
// File:        Message.java
// Group:       3
// Date:        November 23, 2018
// Description: A class representing a message from one user to another
///////////////////////////////////////////////////////////////////////////////

package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Message implements Serializable, Comparable {

  private String message;
  private String recipient;
  private String sender;
  private boolean read = false;
  private Date timeCreated = new Date();
  private String path;

  /**
   * Constructor for the class Message.
   *
   * @param message The message to send
   * @param recipient The username of the recipient
   * @param sender The username of the sender
   */
  public Message(String message, String recipient, String sender) {
    this.message = message;
    this.recipient = recipient;
    this.sender = sender;
  }

  /**
   * Write the message to a file via serialization. The path of the file to write is the path stored
   * in the class attributes. The path must be set before a file can be written
   */
  public void writeFile() {
    if (path == null) {
      return;
    }

    try {
      Path path = Paths.get(this.path);

      FileOutputStream fileOutputStream = new FileOutputStream(path.toString());
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(this);

      fileOutputStream.close();
      objectOutputStream.close();
    } catch (FileNotFoundException exception) {
      System.out.println("File not found " + path);
    } catch (IOException exception) {
      System.out.println("IOException fileOutputStream");
      exception.printStackTrace();
    }
  }

  /**
   * A method to delete the associated message file of this class. The class attribute path must not
   * be null.
   */
  public void deleteFile() {
    if (path == null) {
      return;
    }

    File file = new File(path);
    boolean successful = file.delete();

    if (!successful) {
      System.out.println("Unable to delete file at path: " + path);
    }
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public String getMessage() {
    return message;
  }

  public String getSender() {
    return sender;
  }

  /**
   * Method to get the time created. To avoid Findbugs error, a new date object is created and set
   * to the time of timeCreated.
   *
   * @return a Date object of the time created
   */
  public Date getTimeCreated() {
    Date date = new Date();
    date.setTime(timeCreated.getTime());
    return date;
  }

  /**
   * Creates a message file path to the recipients messages folder. Ensures that the filepath is
   * unique, and calls writeFile to write a message file.
   */
  public void sendMessage() {
    String folder = "";

    try (Connection connection = DriverManager.getConnection("jdbc:derby:lib/ZwischenDB");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
            String.format("Select FOLDER from LOGIN WHERE USERNAME='%s'", recipient))) {
      if (resultSet.next()) {
        folder = resultSet.getString("FOLDER");
      }
    } catch (SQLException exception) {
      System.out.println("Unable to get " + "'s folder");
    }

    Path path1 = Paths.get("lib/UserData/" + folder + "/messages");
    File[] files = new File(path1.toString()).listFiles();

    int iii = 1; //Number of the file to check
    String fileName = "Message" + iii + ".message"; //First file name
    boolean validNameFound = false;

    //Loop until valid file name found
    while (!validNameFound) {
      validNameFound = true;

      for (File file : files) {
        //Check if filename exists
        if (fileName.equals(file.getName())) {
          iii++; //increment file number
          fileName = "Message" + iii + ".message"; //create next file name
          validNameFound = false;
          break; //Loop through files again to make sure the new name was not already passed
        }
      }
    }

    //Create filename path
    Path path2 = Paths.get(fileName);

    //Save relative path to message as string
    path = path1.resolve(path2).toString();

    //Write the file stream to user messages folder
    writeFile();
  }

  //Added to fix FindBugs error
  @Override
  public boolean equals(Object object) {
    return (this == object);
  }

  @Override
  public int compareTo(Object message) {
    return ((Message) message).getTimeCreated().compareTo(this.timeCreated);
  }
}
