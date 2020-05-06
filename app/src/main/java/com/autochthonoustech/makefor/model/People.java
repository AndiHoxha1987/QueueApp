package com.autochthonoustech.makefor.model;

public class People {
    private String name, status,id,key;

    public People() {
    }

    public People(String name, String status, String id,String key) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.key = key;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
