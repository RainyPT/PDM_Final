package com.example.tisisme.Classes;

import java.util.ArrayList;

public class User {
    private int ID;
    private String firstName,SecondName;
    private String email;
    public User(int ID, String firstName, String secondName, String email) {
        this.ID = ID;
        this.firstName = firstName;
        SecondName = secondName;
        this.email = email;
    }
    public User(String firstName,String secondName,String email) {
        this.firstName = firstName;
        SecondName = secondName;
        this.email = email;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return SecondName;
    }

    public void setSecondName(String secondName) {
        SecondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
