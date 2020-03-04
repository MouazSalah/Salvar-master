package info.androidhive.roomdatabase.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import info.androidhive.roomdatabase.db.dao.ItemDao;
import info.androidhive.roomdatabase.db.entity.ItemEntity;
import info.androidhive.roomdatabase.db.entity.NoteEntity;
import info.androidhive.roomdatabase.db.dao.NoteDao;

/**
 * Created by ravi on 05/02/18.
 */

public class NotesRepository {

    private NoteDao mNoteDao;
    private LiveData<List<NoteEntity>> mAllNotes;
    private ItemDao mItemDao;
    private LiveData<List<ItemEntity>> mAllItems;
    int flatId;


    public NotesRepository(Application application)
    {
        AppDatabase db = AppDatabase.getDatabase(application);
        mNoteDao = db.noteDao();
        mAllNotes = mNoteDao.getAllNotes();
        mItemDao = db.itemDao();
        mAllItems = mItemDao.getAllItemsByFlatId(flatId);
    }

    public LiveData<List<NoteEntity>> getAllNotes() {
        return mAllNotes;
    }


    public NoteEntity getNote(int noteId) throws ExecutionException, InterruptedException {
        return new getNoteAsync(mNoteDao).execute(noteId).get();
    }

    public void insertNote(NoteEntity note) {
        new insertNotesAsync(mNoteDao).execute(note);
    }

    public void updateNote(NoteEntity note) {
        new updateNotesAsync(mNoteDao).execute(note);
    }

    public void deleteNote(NoteEntity note) {
        new deleteNotesAsync(mNoteDao).execute(note);
    }

    public void deleteAllNotes() {
        new deleteAllNotesAsync(mNoteDao).execute();
    }


    public LiveData<List<ItemEntity>> getAllItemsByFlatId(int flatId)
    {
        return mItemDao.getAllItemsByFlatId(flatId);
    }


    public ItemEntity getItem(int itemId) throws ExecutionException, InterruptedException
    {
        return new NotesRepository.getSelectedItemAsync(mItemDao).execute(itemId).get();
    }




    public void insertItem(ItemEntity itemEntity) {
        new NotesRepository.insertItemAsync(mItemDao).execute(itemEntity);
    }

    public void updateItem( ItemEntity itemEntity) {
        new NotesRepository.updateItemAsync(mItemDao).execute(itemEntity);
    }

    public void deleteItem(ItemEntity itemEntity ) {
        new NotesRepository.deleteItemAsync(mItemDao).execute(itemEntity);
    }

    public void deleteAllItems() {
        new NotesRepository.deleteAllItemsAsync(mItemDao).execute();
    }







    /**
     * NOTE: all write operations should be done in background thread,
     * otherwise the following error will be thrown
     * `java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.`
     */

    private static class getNoteAsync extends AsyncTask<Integer, Void, NoteEntity> {

        private NoteDao mNoteDaoAsync;

        getNoteAsync(NoteDao animalDao) {
            mNoteDaoAsync = animalDao;
        }

        @Override
        protected NoteEntity doInBackground(Integer... ids) {
            return mNoteDaoAsync.getNoteById(ids[0]);
        }
    }

    private static class insertNotesAsync extends AsyncTask<NoteEntity, Void, Long> {

        private NoteDao mNoteDaoAsync;

        insertNotesAsync(NoteDao noteDao) {
            mNoteDaoAsync = noteDao;
        }

        @Override
        protected Long doInBackground(NoteEntity... notes) {
            long id = mNoteDaoAsync.insert(notes[0]);
            return id;
        }
    }

    private static class updateNotesAsync extends AsyncTask<NoteEntity, Void, Void> {

        private NoteDao mNoteDaoAsync;

        updateNotesAsync(NoteDao noteDao) {
            mNoteDaoAsync = noteDao;
        }

        @Override
        protected Void doInBackground(NoteEntity... notes) {
            mNoteDaoAsync.update(notes[0]);
            return null;
        }
    }

    private static class deleteNotesAsync extends AsyncTask<NoteEntity, Void, Void> {

        private NoteDao mNoteDaoAsync;

        deleteNotesAsync(NoteDao noteDao) {
            mNoteDaoAsync = noteDao;
        }

        @Override
        protected Void doInBackground(NoteEntity... notes) {
            mNoteDaoAsync.delete(notes[0]);
            return null;
        }
    }

    private static class deleteAllNotesAsync extends AsyncTask<NoteEntity, Void, Void> {

        private NoteDao mNoteDaoAsync;

        deleteAllNotesAsync(NoteDao noteDao) {
            mNoteDaoAsync = noteDao;
        }

        @Override
        protected Void doInBackground(NoteEntity... notes) {
            mNoteDaoAsync.deleteAll();
            return null;
        }
    }




    private static class getSelectedItemAsync extends AsyncTask<Integer, Void, ItemEntity>
    {

        private ItemDao mItemDao;

        getSelectedItemAsync(ItemDao itemDao) {
            mItemDao = itemDao;
        }

        @Override
        protected ItemEntity doInBackground(Integer... ids) {
            return mItemDao.getSelectedItemById(ids[0]);
        }
    }

    private static class insertItemAsync extends AsyncTask<ItemEntity, Void, Long>
    {
        private ItemDao mItemDao;

        insertItemAsync(ItemDao itemDao) {
            mItemDao = itemDao;
        }

        @Override
        protected Long doInBackground(ItemEntity... item)
        {
            long id = mItemDao.insertNewItem(item[0]);
            return id;
        }
    }


    private static class updateItemAsync extends AsyncTask<ItemEntity, Void, Long>
    {
        private ItemDao mItemDao;

        updateItemAsync(ItemDao itemDao) {
            mItemDao = itemDao;
        }

        @Override
        protected Long doInBackground(ItemEntity... items)
        {
            mItemDao.updateNewItem(items[0]);
            return null;
        }
    }


    private static class deleteItemAsync extends AsyncTask<ItemEntity, Void, Long>
    {
        private ItemDao mItemDao;

        deleteItemAsync(ItemDao itemDao) {
            mItemDao = itemDao;
        }

        @Override
        protected Long doInBackground(ItemEntity... items)
        {
            mItemDao.deleteItem(items[0]);
            return null;
        }
    }



    private static class deleteAllItemsAsync extends AsyncTask<ItemEntity, Void, Void> {

        private ItemDao mItemDao;

        deleteAllItemsAsync(ItemDao itemDao) {
            mItemDao = itemDao;
        }

        @Override
        protected Void doInBackground(ItemEntity... items) {
            mItemDao.deleteAllITems();
            return null;
        }
    }

}
