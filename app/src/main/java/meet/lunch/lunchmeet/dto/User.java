package meet.lunch.lunchmeet.dto;

/**
 * Created by Aaron on 6/16/2015.
 */
public class User {

    public String bio;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String photoUrl;

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
