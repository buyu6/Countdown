package com.example.daysmatter.logic.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.UUID;

@Entity(tableName = "message")
public class Message {

    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private int time;
    private String aimdate;
    private boolean isTop;
    private Integer categoryIcon;
    private String categoryName;
    @NonNull
    private String categoryId;

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.isTop = false;
        this.categoryIcon = 0;
        this.categoryName = null;
        this.categoryId = "";
    }

    // 全参构造函数
    @Ignore
    public Message(String title, int time, String aimdate, boolean isTop, @NonNull String id,
                   Integer categoryIcon, String categoryName, @NonNull String categoryId) {
        this.title = title;
        this.time = time;
        this.aimdate = aimdate;
        this.isTop = isTop;
        this.id = id;
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public String getAimdate() { return aimdate; }
    public void setAimdate(String aimdate) { this.aimdate = aimdate; }

    public boolean isTop() { return isTop; }
    public void setTop(boolean top) { isTop = top; }

    public Integer getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(Integer categoryIcon) { this.categoryIcon = categoryIcon; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @NonNull
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(@NonNull String categoryId) { this.categoryId = categoryId; }
}