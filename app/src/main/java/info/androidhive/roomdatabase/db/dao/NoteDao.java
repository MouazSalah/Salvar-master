package info.androidhive.roomdatabase.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import info.androidhive.roomdatabase.db.entity.NoteEntity;

@Dao
public interface NoteDao
{
    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    LiveData<List<NoteEntity>> getAllNotes();

    @Query("SELECT * FROM notes_table WHERE id=:id")
    NoteEntity getNoteById(int id);

    @Query("SELECT * FROM notes_table WHERE id=:id")
    LiveData<NoteEntity> getNote(int id);

    @Insert
    long insert(NoteEntity note);

    @Update
    void update(NoteEntity note);

    @Delete
    void delete(NoteEntity note);

    @Query("DELETE FROM notes_table")
    void deleteAll();
}
