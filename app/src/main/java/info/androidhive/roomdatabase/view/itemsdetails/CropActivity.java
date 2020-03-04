package info.androidhive.roomdatabase.view.itemsdetails;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;

import info.androidhive.roomdatabase.R;

public class CropActivity extends AppCompatActivity
{
    public static Bitmap croppedImage;
    private CropImageView cropImageView;

    int flatId;
    double itemCost;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    double itemValue;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        flatId = getIntent().getIntExtra("flat_id", 0);
        itemCost = getIntent().getDoubleExtra("item_cost", 1);
        Log.d("first flat id", "" +  flatId);
        Log.d("first item cost ", "" +  itemCost);

        initActivity();
    }

    private void initActivity()
    {

        // Set URI image to display
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(ItemsActivity.bitmapPhoto);

        // Rotate image the cropped image using function and angle
        // cropImageView.rotateImage(angle);
        // For ex., cropImageView.rotateImage(-90);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.nextStep);
        mFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view.getId() == R.id.nextStep)
                {
                    cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                        @Override
                        public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                            croppedImage = result.getBitmap();
                            processImage();
                        }
                    });
                    cropImageView.getCroppedImageAsync();
                }
            }
        });
    }

    private void showConfirmDialog()
    {
        String[] dialogLanguages = { "نعم" , "اعادة تصوير" ,"ادخال يدوي"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "هل هذا الرقم صحيح ؟";
        builder.setTitle(msg + " " + itemValue);
        builder.setItems(dialogLanguages, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    Intent intent = new Intent(getApplicationContext(), ItemsActivity.class);
                    intent.putExtra("crop_value" , 2);
                    intent.putExtra("flat_id", flatId);
                    intent.putExtra("item_cost", itemCost);
                    startActivity(intent);
                    finish();
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
    }

    /*private void nextStep()
    {
        Intent intent = new Intent(getApplicationContext(), ItemsActivity.class);
        intent.putExtra("crop_value" , 2);
        intent.putExtra("flat_id", flatId);
        intent.putExtra("item_cost", itemCost);
        startActivity(intent);
        finish();
    }*/


    // may need to do this in a different thread(Async Task)
    private void processImage()
    {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (textRecognizer.isOperational())
        {
            // final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),bitmapPhoto);
            // imageView.setImageBitmap(bitmap);
            Frame frame = new Frame.Builder().setBitmap(croppedImage).build();
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
}
