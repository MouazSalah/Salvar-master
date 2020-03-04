package info.androidhive.roomdatabase.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import info.androidhive.roomdatabase.db.dao.ItemDao;
import info.androidhive.roomdatabase.db.entity.ItemEntity;
import info.androidhive.roomdatabase.db.entity.NoteEntity;
import info.androidhive.roomdatabase.db.dao.NoteDao;
import info.androidhive.roomdatabase.db.converter.DateConverter;

/**
 * Created by ravi on 05/02/18.
 */

@Database(entities = {NoteEntity.class, ItemEntity.class}, version = 6)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();
    public abstract ItemDao itemDao();

    private static AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (AppDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "notes_database")
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}