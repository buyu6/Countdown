package com.example.daysmatter.logic.entity;

public class HistoryEvent {
    private String date;
    private String title;

    public HistoryEvent() {}

    public HistoryEvent(String date, String title) {
        this.date = date;
        this.title = title;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

