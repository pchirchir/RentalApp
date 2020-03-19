package com.shadow.rentalapp.data.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Profile implements Serializable {


    @DocumentId
    private String id;

    private String fullName;
    private String phone;
    private String gender;
    private String role;
    private String avatarUrl;

    public Profile() {
    }

    public Profile(String fullName, String phone, String gender, String role) {
        this.fullName = fullName;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
