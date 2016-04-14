package com.wqian001.lovereminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.google.api.services.proximitybeacon.v1beta1.Proximitybeacon;
import com.google.api.services.proximitybeacon.v1beta1.ProximitybeaconRequest;
import com.google.api.services.proximitybeacon.v1beta1.model.AdvertisedId;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by qian on 3/13/2016.
 */
public class ProximityBeacon {

    enum Status{
        ACTIVE,
        DECOMMISSIONED,
        INACTIVE
    }
    enum expectedStability{
        STABLE,
        PORTABLE,
        MOBILE,
        ROVING
    }

    private static String TAG = "tag";

    public String name;
    public Status status;
    public AdvertisedId advertisedId;

    public ProximityBeacon(JSONObject object) {
        this.name = object.optString("beaconName");
        this.status = Status.valueOf(object.optString("status"));
        this.advertisedId = new AdvertisedId();
        try{
            this.advertisedId.setId( object.optJSONObject("advertisedId").getString("id") );
            this.advertisedId.setType( object.optJSONObject("advertisedId").getString("type") );
        }
        catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        //If no description, use the beaconId from the name
    }

    public ProximityBeacon(AdvertisedId advertisedId, Status status) {
        this.name = null;
        this.status = status;
        this.advertisedId = advertisedId;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        if (!TextUtils.isEmpty(this.name)) {
            object.put("beaconName", this.name);
        }
        object.put("advertisedId", idJSon(this.advertisedId));
        object.put("status", this.status.toString());


        return object;
    }

    private JSONObject idJSon(AdvertisedId id){
        JSONObject object = new JSONObject();
        try{
            object.put("type", id.getType());
            object.put("id", id.getId());
        }
        catch (JSONException e){
            e.printStackTrace();
        }


        return object;
    }
}
