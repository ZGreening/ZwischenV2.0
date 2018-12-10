package other;

import java.util.Date;

public class PastRide extends Ride {

  private String rider;

  /**
   * An overloaded constructor for the class PastRide.
   *
   * @param rider The passenger who requested the ride
   * @param driver The driver
   * @param dest The destination
   * @param startP The starting location
   * @param date The date of the ride
   */
  public PastRide(String rider, String driver, String dest, String startP, Date date) {
    super(driver, dest, startP, date);
    this.rider = rider;
  }

  /**
   * An overloaded constructor for the class PastRide.
   *
   * @param rider The passenger who requested the ride
   * @param driver The driver
   * @param dest The destination
   * @param startP The starting location
   * @param date The date of the ride
   * @param idNum The id of the ride //Todo What is the IDNumber???
   */
  public PastRide(String rider, String driver, String dest, String startP, Date date, int idNum) {
    super(driver, dest, startP, date);
    this.rider = rider;
    this.setIdnumber(idNum);
  }

  public String getRider() {
    return rider;
  }

  public void setRider(String rider) {
    this.rider = rider;
  }
}
