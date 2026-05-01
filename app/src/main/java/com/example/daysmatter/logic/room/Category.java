package com.example.daysmatter.logic.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "category")
public class Category implements Serializable { // 建议加上序列化，方便传参
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private int imageId;
    private boolean isSelected;

    // Room 默认使用的构造函数
    public Category(String name, int imageId){
        this.id = UUID.randomUUID().toString();
        this.name = name;      // 修正：使用传入的参数
        this.imageId = imageId; // 修正：使用传入的参数
        this.isSelected = false;
    }

    // 用于全字段初始化的构造函数
    @Ignore
    public Category(String id, String name, int imageId, boolean isSelected){
        this.id = id;
        this.name = name;
        this.imageId = imageId;
        this.isSelected = isSelected;
    }

    // Getter 和 Setter 保持不变...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public boolean isSelected() { return isSelected; } // 建议规范命名
    public void setSelected(boolean selected) { isSelected = selected; }
}