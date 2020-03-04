package info.androidhive.roomdatabase.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "items_table",foreignKeys = @ForeignKey(entity = NoteEntity.class,
        parentColumns = "id",childColumns = "flat_id",onDelete = CASCADE))
public class ItemEntity
{
    @ColumnInfo(name = "item_id")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    long id;

    @ColumnInfo(name = "item_old_value")
    @NonNull
    double itemOldValue;

    @ColumnInfo(name = "item_new_value")
    @NonNull
    double itemNewValue;


    @ColumnInfo(name = "item_difference_value")
    @NonNull
    double itemDifferenceValue;

    @ColumnInfo(name = "item_cost")
    @NonNull
    double itemCost;

    @ColumnInfo(name = "item_date")
    @NonNull
    String itemDate;

    @ColumnInfo(name = "item_time")
    @NonNull
    String itemTime;

    @ColumnInfo(name = "flat_id")
    public
    int flat_id;

    @Ignore
    public ItemEntity() {
    }

    public ItemEntity(double itemOldValue, double itemNewValue, double itemDifferenceValue, double itemCost, @NonNull String itemDate, @NonNull String itemTime, int flat_id) {
        this.itemOldValue = itemOldValue;
        this.itemNewValue = itemNewValue;
        this.itemDifferenceValue = itemDifferenceValue;
        this.itemCost = itemCost;
        this.itemDate = itemDate;
        this.itemTime = itemTime;
        this.flat_id = flat_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getItemOldValue() {
        return itemOldValue;
    }

    public void setItemOldValue(double itemOldValue) {
        this.itemOldValue = itemOldValue;
    }

    public double getItemNewValue() {
        return itemNewValue;
    }

    public void setItemNewValue(double itemNewValue) {
        this.itemNewValue = itemNewValue;
    }

    public double getItemDifferenceValue() {
        return itemDifferenceValue;
    }

    public void setItemDifferenceValue(double itemDifferenceValue) {
        this.itemDifferenceValue = itemDifferenceValue;
    }

    public double getItemCost() {
        return itemCost;
    }

    public void setItemCost(double itemCost) {
        this.itemCost = itemCost;
    }

    @NonNull
    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(@NonNull String itemDate) {
        this.itemDate = itemDate;
    }

    @NonNull
    public String getItemTime() {
        return itemTime;
    }

    public void setItemTime(@NonNull String itemTime) {
        this.itemTime = itemTime;
    }

    public int getFlat_id() {
        return flat_id;
    }

    public void setFlat_id(int flat_id) {
        this.flat_id = flat_id;
    }
}
