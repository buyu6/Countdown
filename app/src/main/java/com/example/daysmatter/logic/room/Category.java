package com.example.daysmatter.logic.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey
    @NonNull
     private String id;
    private String name;
    private int imageId;
    private boolean isSelected;
    public Category(){
    this.id= UUID.randomUUID().toString();
    this.name="";
    this.imageId=0;
    this.isSelected=false;
    }
    @Ignore
    public Category(String id ,String name,int imageId,boolean isSelected){
        this.id=id;
        this.name=name;
        this.imageId=imageId;
        this.isSelected=isSelected;
    }

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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
