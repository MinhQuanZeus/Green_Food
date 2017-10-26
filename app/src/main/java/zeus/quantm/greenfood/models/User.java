package zeus.quantm.greenfood.models;

/**
 * Created by EDGY on 6/25/2017.
 */

public class User {
    private String userID;
    private String name;
    private String phone;
    private String address;
    private String avatar;
    private long rating;
    private String tokenID;

    public User() {
    }

    public User(String userID, String name, String phone, String address, String avatar, long rating, String tokenID) {
        this.userID = userID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.rating = rating;
        this.tokenID = tokenID;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }

    public long getRating() {
        return rating;
    }

    public String getTokenID() {
        return tokenID;
    }
}
