package info.androidhive.roomdatabase.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import info.androidhive.roomdatabase.db.NotesRepository;
import info.androidhive.roomdatabase.db.entity.ItemEntity;

public class ItemsListViewModel extends AndroidViewModel
{
    private NotesRepository mRepository;
    private LiveData<List<ItemEntity>> items;

    public ItemsListViewModel(@NonNull Application application)
    {
        super(application);

        mRepository = new NotesRepository(application);
    }

    public LiveData<List<ItemEntity>> getAllItemsByFlatId(int flatId)
    {
        items = mRepository.getAllItemsByFlatId(flatId);
        return items;
    }

    public ItemEntity getItem(int id) throws ExecutionException, InterruptedException
    {
        return mRepository.getItem(id);
    }

    public void insertItem(ItemEntity itemEntity)
    {
        mRepository.insertItem(itemEntity);
    }

    public void updateItem(ItemEntity itemEntity) {
        mRepository.updateItem(itemEntity);
    }

    public void deleteItem(ItemEntity itemEntity) {
        mRepository.deleteItem(itemEntity);
    }

    public void deleteAllItems() {
        mRepository.deleteAllItems();
    }
}

