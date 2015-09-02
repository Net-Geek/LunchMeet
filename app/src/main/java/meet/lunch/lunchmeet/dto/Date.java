package meet.lunch.lunchmeet.dto;

/**
 * Created by Aaron on 8/4/2015.
 */
public class Date {
    public String date;
    public double latitude;
    public double longitude;
    public String restaurantName;
    public String status;
    public String dateTime;
    public String[] userIDs;

    public void setDate(String date) {
        this.date = date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setUserIDs(String[] userIDs) {
        this.userIDs = userIDs;
    }

    public String getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getStatus() {
        return status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String[] getUserIDs() {
        return userIDs;
    }
}
