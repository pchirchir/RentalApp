package com.shadow.rentalapp.data.models;

public class User {

    String name, phoneNo, gender;

    public User(String name, String phoneNo, String gender) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
