package model;

import com.google.firebase.database.DatabaseReference;

import helper.FirebaseConfig;

public class User {

    private String userId;
    private String userName;
    private String street;
    private String district;
    private String city;

    public User() {
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference usersRef = reference
                .child("Users")
                .child(userId);
        usersRef.setValue(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
