package info.androidhive.roomdatabase.view.itemsdetails;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.db.entity.ItemEntity;
import info.androidhive.roomdatabase.ui.viewmodel.ItemsListViewModel;
import info.androidhive.roomdatabase.view.itemsdetails.ItemsActivity;

public class AddingItemActivity extends AppCompatActivity
{
    @BindView(R.id.itemdate_textview) TextView itemDateTextView;
    @BindView(R.id.itemTime_textview) TextView itemTimeTextView;
    @BindView(R.id.itemValue_edittext) EditText itemValueEditText;

    @BindView(R.id.InsertNewItem) Button saveItemBtn;


    String resultTesxt;
    double itemValue, oldValue, differenceValue;
    String itemDate, itemTime;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    ItemEntity itemEntity;
    ItemsListViewModel viewModel;
    Bitmap bitmapPhoto;
    int flatId;
    double itemCost;

    List<ItemEntity> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_item);

        ButterKnife.bind(this);

        viewModel  = ViewModelProviders.of(this).get(ItemsListViewModel.class);

        itemDateTextView.setText(getCurrentDate());
        itemTimeTextView.setText(getCurrentTime());

        flatId = getIntent().getIntExtra("flat_id", 0);
        itemCost = getIntent().getDoubleExtra("item_cost", 1);
        Log.d("adding item Cost" , "" + itemCost);
        Log.d("adding flat id" , "" + flatId);

        itemDate = getCurrentDate();
        itemTime = getCurrentTime();

        viewModel.getAllItemsByFlatId(flatId).observe(this, new Observer<List<ItemEntity>>()
        {
            @Override
            public void onChanged(@Nullable List<ItemEntity> itemsList)
            {
                items = itemsList;
                Log.d("size", items.size() + "");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.addactivity_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_save)
        {
            checkAllFields();
            return true;
        }

        if (id == R.id.action_cancel)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.InsertNewItem) void AddItem()
    {
        if (checkAllFields())
        {
            if (itemValueEditText.getText().toString().isEmpty())
            {
                itemValueEditText.setError("من فضلك ادخل القيمة");
                return;
            }
            if (isNumber(itemValueEditText.getText().toString()) == true)
            {
                itemValue = Double.parseDouble(itemValueEditText.getText().toString());
                insertNewItem();
            }
            else
            {
                itemValueEditText.setError("ادخل رقم");
            }
        }
    }


    public boolean isNumber(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (Character.isDigit(s.charAt(i)) == false)
                return false;

        return true;

    }


    public boolean checkAllFields()
    {
        if (TextUtils.isEmpty(itemValueEditText.getText().toString()))
        {
            itemValueEditText.setError("enter item value");
            return false ;
        }
        else
        {
            return true;
        }
    }

    public String getCurrentDate()
    {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public String getCurrentTime()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date1 = new SimpleDateFormat("HH:mm a");
        // you can get seconds by adding  "...:ss" to it
        date1.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        String localTime = date1.format(currentLocalTime);

        return localTime;
    }


    public void insertNewItem()
    {
        double differenceValue = itemValue;
        double oldValue = 0;

        if (items.size() > 0)
        {
            differenceValue = itemValue - items.get(0).getItemNewValue();
            oldValue = items.get(0).getItemDifferenceValue();
        }

        ItemEntity itemEntity = new ItemEntity( oldValue , itemValue, differenceValue,
                differenceValue * itemCost ,getCurrentDate(), getCurrentTime() , flatId); viewModel.insertItem(itemEntity);
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ItemsActivity.class);
        intent.putExtra("flat_id", flatId);
        intent.putExtra("item_cost", itemCost);
        startActivity(intent);
        finish();

    }
}