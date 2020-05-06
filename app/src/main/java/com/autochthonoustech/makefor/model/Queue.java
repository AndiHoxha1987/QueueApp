package com.autochthonoustech.makefor.model;

public class Queue {
    private String admin;
    private String name;
    private String status;
    private String groupId;
    private Object members;

    public Queue() {
    }

    public Queue(String admin, String name, String status, String groupId, Object members) {
        this.admin = admin;
        this.name = name;
        this.status = status;
        this.groupId = groupId;
        this.members = members;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Object getMembers() {
        return members;
    }

    public void setMembers(Object members) {
        this.members = members;
    }
}
