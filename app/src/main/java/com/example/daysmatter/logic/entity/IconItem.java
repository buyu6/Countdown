package com.example.daysmatter.logic.entity;

public class IconItem {

    private final int resId;
    private boolean isSelected;

    public IconItem(int resId) {
        this(resId, false);
    }

    public IconItem(int resId, boolean isSelected) {
        this.resId = resId;
        this.isSelected = isSelected;
    }

    public int getResId() {
        return resId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}