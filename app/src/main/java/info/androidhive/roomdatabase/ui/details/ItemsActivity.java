package info.androidhive.roomdatabase.ui.details;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.db.entity.ItemEntity;
import info.androidhive.roomdatabase.ui.AddingItemActivity;
import info.androidhive.roomdatabase.ui.CropActivity;
import info.androidhive.roomdatabase.ui.viewmodel.ItemsListViewModel;

public class ItemsActivity extends AppCompatActivity implements ItemAdapter.ItemsAdapterListener
{
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int PICK_IMAGE_REQUEST = 1;

    int flatId, cropResult;
    double itemValue, oldValue, differentValue, itemCost;

    String resultText;

    private ItemsListViewModel viewModel;

    public static Bitmap bitmapPhoto;

    List<ItemEntity> items = new ArrayList<>();

    private ItemAdapter itemAdapter;

    @BindView(R.id.items_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.items_empty_view) RelativeLayout emptyItemsView;
    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(ItemsListViewModel.class);

        flatId = getIntent().getIntExtra("flat_id", 0);
        itemCost = getIntent().getDoubleExtra("item_cost" , 1);
        cropResult = getIntent().getIntExtra("crop_value" , 1);

        viewModel.getAllItemsByFlatId(flatId).observe(this, new Observer<List<ItemEntity>>()
        {
            @Override
            public void onChanged(@Nullable List<ItemEntity> itemsList)
            {
                items = itemsList;
                toggleEmptyNotes(items.size());
            }
        });


        if (cropResult == 2)
        {
            flatId = getIntent().getIntExtra("flat_id", 0);
            itemCost = getIntent().getDoubleExtra("item_cost", 1);
            bitmapPhoto = CropActivity.croppedImage;

            Log.d("flate Id =" , flatId + "");
            Log.d("item cost =" , itemCost + "");

            processImage();
            insertNewItem();
            Log.d("crop_value = ", "cropped");
        }

        itemAdapter = new ItemAdapter(this, (ItemAdapter.ItemsAdapterListener) this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);
    }

    private void toggleEmptyNotes(int size)
    {
        if (size > 0)
        {
            emptyItemsView.setVisibility(View.GONE);
        }
        else
        {
            emptyItemsView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.items_fab)
    public void onFabButtonClicked()
    {
        showActionDialog();
    }

    @OnClick(R.id.items_fab)
    public void onFabClick()
    {
         showNoteDialog(false, null, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
       if (item.getItemId() == R.id.action_add_items)
        {
            insertNewItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            bitmapPhoto = (Bitmap) data.getExtras().get("data");

        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri uri = data.getData();

            try
            {
                bitmapPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
        intent.putExtra("flat_id", flatId);
        intent.putExtra("item_cost", itemCost);
        startActivity(intent);
        finish();
    }

    // may need to do this in a different thread(Async Task)
    private void processImage()
    {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (textRecognizer.isOperational())
        {
            // final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),bitmapPhoto);
            // imageView.setImageBitmap(bitmap);
            Frame frame = new Frame.Builder().setBitmap(bitmapPhoto).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < items.size(); i++)
            {
                TextBlock textBlock = items.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            resultText = stringBuilder.toString();
            Log.d("result before" , resultText);

            resultText = resultText.replaceAll("\\s","");

            Log.d("result after" , resultText);

            if (resultText == null)
            {
                Toast.makeText(this, "من فضلك حاول ثانية ، الصورة لا تحتوي علي اي ارقام", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                itemValue = Double.parseDouble(resultText);
                showConfirmDialog();
            }

            /*if (isNumber(resultText) == true)
            {
                itemValue = Double.parseDouble(resultText);
                showConfirmDialog();
                Log.d("Detected", itemValue + "");
               //Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.d("Detected",   "Not founded");
                Toast.makeText(this, "من فضلك حاول مرة اخري", Toast.LENGTH_SHORT).show();
            }*/
        }
        else
        {
            Log.d("TAG", "processImage: not operational");
        }
    }

    private void getImageFromGallery()
    {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private void showConfirmDialog()
    {
        String[] dialogLanguages = { "نعم" , "اعادة تصوير" ,"ادخال يدوي"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("هل هذا الرقم صحيح ؟");
        builder.setItems(dialogLanguages, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    insertNewItem();
                }
                if (which == 1)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        }
                        else
                        {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                }
                if (which == 2)
                {
                    Intent intent = new Intent(getApplicationContext(), AddingItemActivity.class);
                    intent.putExtra("flat_id", flatId);
                    intent.putExtra("item_cost", itemCost);
                    startActivity(intent);
                    finish();
                }
            }
        });
        builder.show();


       /*
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("رسالة تأكيد");
        builder.setMessage("هل هذا الرقم صحيح " + itemValue );


        builder.setCancelable(false).setPositiveButton("نعم", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                insertNewItem();
            }
        })
        .setNegativeButton("اعادة تصوير", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogBox, int id)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        })
        .setNeutralButton("ادخال يدوي", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = new Intent(getApplicationContext(), AddingItemActivity.class);
                intent.putExtra("flat_id", flatId);
                intent.putExtra("item_cost", itemCost);
                startActivity(intent);
                finish();

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();*/
    }

    public boolean isNumber(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (Character.isDigit(s.charAt(i)) == false)
                return false;

        return true;

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
         differentValue = itemValue;
         oldValue = 0;

        if (items.size() > 0)
        {
            differentValue = itemValue - items.get(0).getItemNewValue();
            oldValue = items.get(0).getItemDifferenceValue();
        }

       /* SharedPrefMethods prefMethods = new SharedPrefMethods(this);
        if (prefMethods.getOldValue() == 0)
        {
            oldValue = prefMethods.getOldValue();
        }

        differenceValue = itemValue - oldValue;
        prefMethods.saveOldValue(itemValue);
*/

        Log.d("Flagitem new value", itemValue + "");
        Log.d("Flagitem old value", oldValue + "");
        Log.d("Flagitem differ value", differentValue + "");

        ItemEntity itemEntity = new ItemEntity( oldValue , itemValue, differentValue,
                differentValue * itemCost ,getCurrentDate(), getCurrentTime() , flatId);
        viewModel.insertItem(itemEntity);
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ItemsActivity.class);
        intent.putExtra("flat_id", flatId);
        intent.putExtra("item_cost", itemCost);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(int itemId, int position)
    {
        showActionsDialog(itemId, position);
    }

    @Override
    public void onLongClick(int itemId, int position)
    {
        showActionsDialog(itemId, position);
    }


    private void showActionsDialog(int itemId, final int position)
    {
        final ItemEntity itemEntity;

        try
        {
            itemEntity = viewModel.getItem(itemId);

            CharSequence colors[] = new CharSequence[]{getString(R.string.edit), getString(R.string.delete)};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_title_choose));
            builder.setItems(colors, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (which == 0)
                    {
                        showNoteDialog(true, itemEntity, position);
                    }
                    else
                    {
                        showAlertDialog(itemEntity);
                    }
                }
            });
            builder.show();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showActionDialog()
    {
        String[] dialogLanguages = {"الكاميرا","يدوي" , "الاستوديو"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("طريقة تسجيل القيمة");
        builder.setItems(dialogLanguages, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        }
                        else
                        {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                }
                if (which == 1)
                {
                    Toast.makeText(ItemsActivity.this, "shown", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AddingItemActivity.class);
                    intent.putExtra("flat_id", flatId);
                    intent.putExtra("item_cost", itemCost);
                    startActivity(intent);
                    finish();
                }

                if (which == 2)
                {
                    getImageFromGallery();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public void showAlertDialog(final ItemEntity itemEntity)
    {
        new AlertDialog.Builder(this).setTitle("رسالة تحذير")
                .setMessage("هل تريد حذف ذلك العنصر؟")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        viewModel.deleteItem(itemEntity);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showNoteDialog(final boolean shouldUpdate, final ItemEntity itemEntity, final int position)
    {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && itemEntity != null)
        {
            // append sets text to EditText and places the cursor at the end
            inputNote.append(itemEntity.getItemNewValue()+ "");
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? getString(R.string.update) : getString(R.string.save), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogBox, int id)
                    {

                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialogBox, int id)
                            {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString()))
                {
                    Toast.makeText(ItemsActivity.this, getString(R.string.dialog_title_enter_note), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    alertDialog.dismiss();
                }
            }
        });
    }

}
