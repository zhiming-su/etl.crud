package com.xiyu.schedulix.dao;


public class Job {

    private String id;
    private String status;

    public Job() {
    }

    public Job(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }
}