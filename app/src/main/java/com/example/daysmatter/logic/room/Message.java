package com.example.daysmatter.logic.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "message")
public class Message implements Serializable {

    @PrimaryKey
    @NonNull
    private String id=UUID.randomUUID().toString();;
    private String title;
    private int time;
    private String aimdate;
    private boolean isTop;
    private Integer categoryIcon;
    private String categoryName;
    @NonNull
    private String categoryId="default_id";

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.isTop = false;
        this.categoryIcon = 0;
        this.categoryName = null;
        this.categoryId = "";
    }
    // 在 Message 类中添加这个构造函数
    @Ignore
    public Message(String id,String title, int time, String aimdate, boolean isTop,
                   Integer categoryIcon, String categoryName) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.aimdate = aimdate;
        this.isTop = isTop;
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.categoryId = "default_id"; // 或者根据你的逻辑设置默认值
    }
    @Ignore
    public Message(String title, int time, String aimdate, boolean isTop,
                   Integer categoryIcon, String categoryName) {
        this.id = UUID.randomUUID().toString(); // 自动生成唯一 ID
        this.title = title;
        this.time = time;
        this.aimdate = aimdate;
        this.isTop = isTop;
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
        this.categoryId = "default_id"; // 或者根据你的逻辑设置默认值
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