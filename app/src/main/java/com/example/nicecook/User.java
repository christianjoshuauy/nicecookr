package com.example.nicecook;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private ArrayList<String> favorites;
    private ArrayList<String> notes;

    public User(String id, String name, ArrayList<String> favorites, ArrayList<String> notes) {
        this.id = id;
        this.name = name;
        this.favorites = favorites;
        this.notes = notes;
    }

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
