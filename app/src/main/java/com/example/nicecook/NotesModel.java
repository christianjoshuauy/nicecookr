package com.example.nicecook;

class NotesModel {
    String notesName;
    String description;
    String time;

    public NotesModel(String notesName, String description, String time) {
        this.notesName = notesName;
        this.description = description;
        this.time = time;
    }

    public String getNotesName() {
        return notesName;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}
