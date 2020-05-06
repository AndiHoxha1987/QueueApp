package com.autochthonoustech.makefor.model;

public class Users {
    private String device_token;
    private String name;
    private String status;
    private String uid;
    private String email;

    public Users(String device_token,String email ,String name, String status,
                 String uid ) {
        this.device_token = device_token;
        this.email = email;
        this.name = name;
        this.status = status;
        this.uid = uid;
    }

    public Users() {
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
