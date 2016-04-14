package com.wqian001.lovereminder;

/**
 * Created by qian on 3/11/2016.
 */
public class Constant {
    public static final String beacons_register = "https://proximitybeacon.googleapis.com/v1beta1/beacons:register?key=AIzaSyDRbSRBpvBsdz3dromGLFxod42ezSzNwdc";
    public static final String http_post = "POST";
    public static final int REQUEST_PICK_ACCOUNT = 42;
    public static final int REQUEST_ERROR_RECOVER = 43;
    public static final int REQUEST_AUTH = 1002;
    public static final int BEACON_SCAN = 1005;
    public static final int Request_Web_View = 1008;
    public static final int REQUEST_MATCH = 1011;
    public static final int Major = 1992;
    public static final int Minor = 1013;
    public static final String SCOPES = "oauth2:https://www.googleapis.com/auth/userlocation.beacon.registry";
}
