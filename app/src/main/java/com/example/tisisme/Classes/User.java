package com.example.tisisme.Classes;

import java.util.ArrayList;

public class User {
    int IDU;
    String fullName,username,password,type;
    ArrayList<String> studies;
    ArrayList<UniCourse> uniCourses;

    public User(){

    }
    public User(String u,String p){
        this.username=u;
        this.password=p;
    }
    public String getPassword(){
        return this.password;
    }
    public String getUsername(){
        return this.username;
    }
}
