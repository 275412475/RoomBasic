package com.example.mywords;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Word.class},version = 2,exportSchema = false)
public abstract class WordDataBase extends RoomDatabase {
    private static WordDataBase INSETANCE;
    static synchronized WordDataBase getDatabase(Context context){
        if (INSETANCE==null){
            INSETANCE = Room.databaseBuilder(context.getApplicationContext(),WordDataBase.class,"word_database")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSETANCE;
    }
    public abstract WordDao getWordDao();

    static final Migration MIGRATION_1_2 =new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_inbisible INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 =new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_inbisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
