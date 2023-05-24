package com.example.nicecook;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Note implements Parcelable {
    String notesName;
    String description;
    String time;
    String userId;
    String noteId;
    int status;
    public Note() {
    }

    public Note(String notesName, String description, String time,int status,String noteId, String userId) {
        this.notesName = notesName;
        this.description = description;
        this.time = time;
        this.status = status;
        this.noteId = noteId;
        this.userId = userId;
    }

    public Note(String notesName, String description, String time) {
        this.notesName = notesName;
        this.description = description;
        this.time = time;
    }

    protected Note(Parcel in) {
        notesName = in.readString();
        description = in.readString();
        time = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getNotesName() {
        return notesName;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(notesName);
        parcel.writeString(description);
        parcel.writeString(time);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getNoteId() {
        return noteId;
    }
}
