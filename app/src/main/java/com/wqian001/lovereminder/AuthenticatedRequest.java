package com.wqian001.lovereminder;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qian on 4/4/2016.
 */
public class AuthenticatedRequest extends JsonObjectRequest {

    //private String mAuthToken;

    public AuthenticatedRequest(int method, String url,
                                JSONObject requestBody,
                                Response.Listener listener,
                                Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

/*    public void setAuthToken(String token) {
        mAuthToken = token;
    }*/

    public Map getHeaders() throws AuthFailureError {
        HashMap headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        //headers.put("Authorization", "Bearer " + mAuthToken);

        return headers;
    }
}