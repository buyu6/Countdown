package com.example.daysmatter.logic.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {Message.class, Category.class}, exportSchema = false)
public abstract class CountdownDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract CategoryDao categoryDao();
    // 单例实例，使用 volatile 保证多线程可见性
    private static volatile CountdownDatabase instance;

    // 获取数据库实例的静态方法
    public static CountdownDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (CountdownDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CountdownDatabase.class,
                            "countdown_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}
