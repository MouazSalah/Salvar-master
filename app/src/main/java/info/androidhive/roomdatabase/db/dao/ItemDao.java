package info.androidhive.roomdatabase.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
import info.androidhive.roomdatabase.db.entity.ItemEntity;

@Dao
public interface ItemDao
{
    @Query("SELECT * FROM items_table ORDER BY item_id DESC")
    LiveData<List<ItemEntity>> getAllItems();

    @Query("SELECT * FROM items_table WHERE flat_id=:id ORDER BY item_id DESC")
    LiveData<List<ItemEntity>> getAllItemsByFlatId(int id);

    @Query("SELECT * FROM items_table WHERE item_id==:flatId ORDER BY item_id DESC")
    ItemEntity getSelectedItemById(int flatId);

    @Insert
    long insertNewItem(ItemEntity itemEntity);

    @Update
    void updateNewItem(ItemEntity itemEntity);

    @Delete
    void deleteItem(ItemEntity itemEntity);

    @Query("DELETE FROM items_table")
    void deleteAllITems();
}
