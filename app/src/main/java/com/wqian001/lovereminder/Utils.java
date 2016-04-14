package com.wqian001.lovereminder;

/**
 * Created by qian on 4/4/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     * Convert section of a byte[] into a hexadecimal string
     */
    public static String toHexString(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();

        for (int i=offset; i < length; i++) {
            sb.append(String.format("%02x", data[i] & 0xFF));
        }

        return sb.toString();
    }

    /**
     * Show a Toast message
     */
    public static void showToast(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static byte[] generateID(Context context){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        // Access the default SharedPreferences
        SharedPreferences sharedPref =  context.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        String email = sharedPref.getString("user_email", "default");
        Log.d("Test", timeStamp.getBytes().length + " " + email.getBytes().length + " " + email);
        byte[] uni_id = new byte[16];

        for(int i = 0; i != 16; ++i){
            if( i < timeStamp.getBytes().length && i < email.getBytes().length){
                uni_id[i] = (byte) (timeStamp.getBytes()[i] ^ email.getBytes()[i]);
            }
            else{
                uni_id[i] = (byte)0xff;
            }
        }

       // timeStamp.getBytes() ^ email.getBytes();

        //return;
        return uni_id;
    }
}
