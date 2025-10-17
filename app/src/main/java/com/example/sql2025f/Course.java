package com.example.sql2025f;

public class Course {
    private long id;
    private String title, code;

    public Course(long id, String title, String code){
        this.id = id;
        this.title = title;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
