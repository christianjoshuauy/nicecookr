package com.example.nicecook;

public class Recipe {
    private String title;
    private String author;
    private int time;
    private String ingredients;
    private String procedure;

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Recipe() {
    }

    public Recipe(String title, String author, int time, String ingredients, String procedure) {
        this.title = title;
        this.author = author;
        this.time = time;
        this.ingredients = ingredients;
        this.procedure = procedure;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
