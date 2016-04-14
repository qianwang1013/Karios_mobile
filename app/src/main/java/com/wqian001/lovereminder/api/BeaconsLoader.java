package com.wqian001.lovereminder.api;

import android.content.Context;
import android.util.Log;

import com.wqian001.lovereminder.data.Beacon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Loader to execute and parse beacons.list
 */
public class BeaconsLoader extends BaseApiLoader<List<Beacon>> {
    private static final String TAG = BeaconsLoader.class.getSimpleName();

    public BeaconsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartRequest() {
        ProximityApi.getInstance(getContext())
                .getBeaconsList(this, this);
    }

    @Override
    protected List<Beacon> onHandleResponse(JSONObject response) {
        //API Error
        if (response == null) return null;

        try {
            return Beacon.fromJson(response);
        } catch (JSONException e) {
            Log.w(TAG, "Parsing error", e);
            return null;
        }
    }
}
