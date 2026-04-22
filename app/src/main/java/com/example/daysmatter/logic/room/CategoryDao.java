package com.example.daysmatter.logic.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;
@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(Category category);

    @Update
    void updateCategory(Category newCategory);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM category")
    List<Category> loadAllcategory();

    @Query("DELETE FROM message WHERE categoryName = :categoryName")
    void deleteMessagesByCategory(String categoryName);

    @Upsert
    void upSertCategories(List<Category> categories);

    @Query("DELETE FROM message")
    void clearAllMessages();

    @Query("DELETE FROM category")
    void clearAllCategories();
}
