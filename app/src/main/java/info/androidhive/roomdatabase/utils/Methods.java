package info.androidhive.roomdatabase.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.concurrent.ExecutionException;

import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.db.entity.ItemEntity;

public class Methods
{
    int returnedValue = 0;
    Context context;

    public Methods(Context con)
    {
        this.context = con;
    }

    public int showAlertDialog()
    {
        new AlertDialog.Builder(context).setTitle("رسالة تحذير")
                .setMessage("هل تريد حذف ذلك العنصر؟")


                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                       returnedValue = 1;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        returnedValue = 0;
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return returnedValue;
    }

   /* private void showActionsDialog(int itemId, final int position)
    {
        try
        {
            CharSequence colors[] = new CharSequence[]{"تعديل", "حذف"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("اختر واحدا");
            builder.setItems(colors, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (which == 0)
                    {
                        showAlertDialog();
                    }
                    else
                    {
                        Methods methods = new Methods();
                        if (methods.showAlertDialog(getApplicationContext()) == 1)
                        {
                            viewModel.deleteItem(itemEntity);
                        }

                        // showAlertDialog(itemEntity);
                    }
                }
            });
            builder.show();

        } catch (ExecutionException e) {
            // TODO - handle error
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO - handle error
            e.printStackTrace();
        }
    }*/
}
