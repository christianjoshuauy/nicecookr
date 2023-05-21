package com.example.nicecook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    private String recipeID;
    private String title;
    private String author;
    private int time;
    private ArrayList<String> ingredients;
    private ArrayList<String> procedure;
    private String imageFile;

    protected Recipe(Parcel in) {
        recipeID = in.readString();
        title = in.readString();
        author = in.readString();
        time = in.readInt();
        ingredients = in.createStringArrayList();
        procedure = in.createStringArrayList();
        imageFile = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getProcedure() {
        return procedure;
    }

    public void setProcedure(ArrayList<String> procedure) {
        this.procedure = procedure;
    }

    public Recipe() {
    }

    public Recipe(String recipeID, String title, String author, int time, ArrayList<String> ingredients, ArrayList<String> procedure, String imageFile) {
        this.recipeID = recipeID;
        this.title = title;
        this.author = author;
        this.time = time;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.imageFile = imageFile;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(recipeID);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeInt(time);
        parcel.writeStringArray(ingredients.toArray(new String[ingredients.size()]));
        parcel.writeStringArray(procedure.toArray(new String[procedure.size()]));
        parcel.writeString(imageFile);
    }
}
