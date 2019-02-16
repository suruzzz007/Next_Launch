package kwa.pravaah;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import kwa.pravaah.database.DbManager;

public class AlarmReceiver extends BroadcastReceiver {


    private static final String TAG = "KWATest: AlarmReceiver";
    private String POWER="ON";
    String number [];


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");

            DbManager db=new DbManager(context);
            String power;

            Bundle b = intent.getExtras();
            assert b != null;
            String No=b.getString("Number");

            Log.i(TAG, "onReceive: Making call");
            Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show();
            // AppUtils.makeCall(context,No);

       /* if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
             number = No.substring(0, 10);
        else*/
            number = No.split(",");

          Cursor cursor=db.getPowerStatus(number[0]);
            if(cursor.getCount()!=0) {
                cursor.moveToFirst();
                power = cursor.getString(cursor.getColumnIndex(db.POWER));
                if (power.equalsIgnoreCase(POWER)) {

                    AppUtils.dial(No, context);


                }


            }



        }



    }






