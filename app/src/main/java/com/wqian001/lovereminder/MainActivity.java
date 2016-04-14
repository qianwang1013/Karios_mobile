package com.wqian001.lovereminder;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.api.services.proximitybeacon.v1beta1.model.AdvertisedId;

import org.apache.commons.codec.binary.Base64;



public class MainActivity extends Activity{


    private static String TAG = "Test";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    //private Beacon mBeacon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPref = getSharedPreferences("myApp", Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();

        setContentView(R.layout.activity_main);


//     new Beacon(mac, uuid, **, **);
/*        Uri uriUrl = Uri.parse("http://10.0.2.2:3000");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);*/

        /*
        *  If the beacon is not registered, we grab auth and make proximity
        * */

        String user_beaconName = sharedPref.getString("user_beaconName", "");
        String user_beaconID = sharedPref.getString("user_beaconID", "");
        String user_token = sharedPref.getString("user_token", "");
        if( user_beaconID.isEmpty() || user_beaconName.isEmpty() || user_token.isEmpty() ){
            // if one of them is empty, the auth
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivityForResult(intent, Constant.REQUEST_AUTH);
        }
        else{
            // start browser
            //Intent intent = new Intent(this, WebProfile.class);
            //startActivityForResult(intent, Constant.Request_Web_View);
            Intent intent = new Intent(this, BeaconMsg.class);
            intent.putExtra("token", user_token);
            startActivityForResult(intent, Constant.BEACON_SCAN);

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.REQUEST_AUTH){
            if(resultCode == RESULT_OK){
                String mToken = data.getStringExtra("Token");
                editor.putString("user_token", mToken);
                editor.commit();
                // ready to register
                AdvertisedId mId = new AdvertisedId();
                mId.setType("EDDYSTONE");
                ProximityAPI api = new ProximityAPI(mToken, this);
                mId.setId(new String(Base64.encodeBase64(Utils.generateID(getApplicationContext()))));
                //api.registerBeacon(new ProximityBeacon(mId, ProximityBeacon.Status.ACTIVE));

                // start browser
                //Intent intent = new Intent(this, WebProfile.class);
                //startActivityForResult(intent, Constant.Request_Web_View);

                //api.createAttachment(sharedPref.getString("user_beaconName", ""), "123654", "lovereminder-1241/string");
            }
            else{
                finish();
            }
        }

    }

    }
