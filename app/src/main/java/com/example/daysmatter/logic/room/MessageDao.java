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
public interface MessageDao {
    @Insert
    void insert(Message message);
    @Upsert
    void upSertMessage(Message message);
    //更新或者插入
    @Upsert
    void upSertMessages(List<Message> messages);
    //删除信息
    @Delete
    void deletemessage(Message message);
//加载所有信息
    @Query("SELECT * FROM message")
    List<Message> loadAllMessage();
//根据id删除信息
    @Query("DELETE FROM message WHERE id = :id")
    void deleteById(String id);
    //得到置顶信息
    @Query("SELECT * FROM message ORDER BY isTop DESC, id ASC")
    List<Message> getAllByTop();
//取消置顶
    @Query("UPDATE message SET isTop = false WHERE isTop = true")
    void cancelAllTop();
//根据种类查询信息
    @Query("SELECT * FROM message WHERE categoryName = :category ORDER BY isTop DESC, id ASC")
    List<Message> getMessagesByCategory(String category);
}
