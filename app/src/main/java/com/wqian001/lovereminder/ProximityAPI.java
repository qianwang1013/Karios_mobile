package com.wqian001.lovereminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.api.services.proximitybeacon.v1beta1.model.AdvertisedId;
import com.wqian001.lovereminder.api.AuthenticatedRequest;
import com.wqian001.lovereminder.Attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qian on 4/8/2016.
 */
public class ProximityAPI {
    private String mAuthToken;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private static final String BEACON_ROOT = "https://proximitybeacon.googleapis.com/v1beta1";
    private static final String TAG = "Test";

    public ProximityAPI(String mAuthToken, Context mContext){
        this.mAuthToken = mAuthToken;
        this.mContext = mContext;
        this.mRequestQueue = Volley.newRequestQueue(mContext);
    }

    private void performPostRequest(String endpoint, JSONObject body,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        com.wqian001.lovereminder.api.AuthenticatedRequest request = new com.wqian001.lovereminder.api.AuthenticatedRequest(
                Request.Method.POST, endpoint, body, listener, errorListener);
        request.setAuthToken(mAuthToken);
        mRequestQueue.add(request);
    }


    private void performDeleteRequest(String endpoint,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        com.wqian001.lovereminder.api.AuthenticatedRequest request = new com.wqian001.lovereminder.api.AuthenticatedRequest(
                Request.Method.DELETE, endpoint, null, listener, errorListener);
        request.setAuthToken(mAuthToken);
        mRequestQueue.add(request);
    }

    private void performGetRequest(String endpoint,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        com.wqian001.lovereminder.api.AuthenticatedRequest request = new AuthenticatedRequest(
                Request.Method.GET, endpoint, null, listener, errorListener);
        request.setAuthToken(mAuthToken);
        mRequestQueue.add(request);
    }

    //Callback to receive beacon register/get responses
    private class BeaconDataCallback implements
            Response.Listener<JSONObject>, Response.ErrorListener {
        private AdvertisedId mAdvertisedId;
        public BeaconDataCallback(AdvertisedId id) {
            mAdvertisedId = id;
        }

        @Override
        public void onResponse(JSONObject response) {
            //Called for 2xx responses
            ProximityBeacon beacon = new ProximityBeacon(response);
            // name,
            // Access the default SharedPreferences
            SharedPreferences sharedPref = mContext.getSharedPreferences("myApp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user_beaconName",beacon.name);
            editor.putString("user_beaconID", beacon.advertisedId.getId());
            editor.commit();
            Log.w(TAG, "Response Obj" + response.toString());

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse == null) {
                Log.w(TAG, "Unknown error response from Proximity API", error);
                return;
            }

            //Called for 4xx responses
            switch (error.networkResponse.statusCode) {
                case 400:
                    Log.v(TAG, "case 400", error);
                    break;
                case 403:
                    Log.w(TAG, "case 403", error);
                    break;
                case 404:
                    Log.w(TAG, "case 404", error);
                    break;
                case 409:
                    Log.v(TAG, "409 message: " + error.getMessage() + "\n" + error.getStackTrace());
                default:
                    Log.w(TAG, "Unknown error response from Proximity API", error);
            }
        }

    }

    //Callback to receive attachment create/delete responses
    private class AttachmentDataCallback implements
            Response.Listener<JSONObject>, Response.ErrorListener {
        @Override
        public void onResponse(JSONObject response) {
            Log.v(TAG, "Attachment Request Completed");

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(TAG, "Attachment API Error", error);
        }

    }

    /**
     * beacons.register
     * Post an observed beacon to attach it to this API project
     */
    public void registerBeacon(ProximityBeacon beacon) {
        BeaconDataCallback callback = new BeaconDataCallback(beacon.advertisedId);
        final String endpoint = String.format("%s/beacons:register", BEACON_ROOT);
        Log.v(TAG, "Creating beacon: " + endpoint);
        try {
            Log.d(TAG, endpoint + " " + beacon.toJson().toString());
            performPostRequest(endpoint, beacon.toJson(), callback, callback);
        } catch (JSONException e) {
            Log.w(TAG, "Unable to serialize beacon", e);
            callback.onErrorResponse(new VolleyError(e));
        }
    }

    /**
     * beacons.attachments.list
     * Return a list of attachments for the given beacon name
     */
    public void getAttachmentsList(String beaconName,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        final String endpoint = String.format("%s/%s/attachments",
                BEACON_ROOT, beaconName);
        Log.v(TAG, "Getting attachments: " + endpoint);
        errorListener = new AttachmentDataCallback();
        performGetRequest(endpoint, listener, errorListener);
    }

    /**
     * beacons.attachments.create
     * Post a new attachment to the given beacon name
     */
    public void createAttachment(String beaconName, String data,
                                 String namespacedType) {
        AttachmentDataCallback callback = new AttachmentDataCallback();
        com.wqian001.lovereminder.Attachment toCreate = new com.wqian001.lovereminder.Attachment(data, namespacedType);
        final String endpoint = String.format("%s/%s/attachments",
                BEACON_ROOT, beaconName);
        Log.d(TAG, "Creating attachment: " + endpoint + "data " + toCreate.toString());
        try {
            performPostRequest(endpoint, toCreate.toJson(), callback, callback);
        } catch (JSONException e) {
            Log.w(TAG, "Unable to create attachment object");
        }
    }


    /**
     * beacons.attachments.delete
     * Delete the data matching the given attachment name
     */
    public void deleteAttachment(Attachment attachment) {
        AttachmentDataCallback callback = new AttachmentDataCallback();
        //Attachment name contains beacon name
        final String endpoint = String.format("%s/%s",
                BEACON_ROOT, attachment.name);
        Log.v(TAG, "Deleting attachment: " + endpoint);
        performDeleteRequest(endpoint, callback, callback);
    }

    public void getNamespaces(Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        AttachmentDataCallback callback = new AttachmentDataCallback();
        final String endpoint = String.format("%s/namespaces", BEACON_ROOT);
        Log.v(TAG, "Getting namespaces: " + endpoint);
        performGetRequest(endpoint, callback, callback);
    }
}
