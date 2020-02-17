package com.shadow.rentalapp;

public class UserHelperClass {

    String name,email,phoneNo,pass,gender;

   /* public UserHelperClass(){

    }*/

    public UserHelperClass(String name, String email, String phoneNo, String pass,String gender) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pass = pass;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.pass = gender;
    }
}
