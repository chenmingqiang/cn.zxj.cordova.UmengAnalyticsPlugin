package cn.zxj.cordova;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;
import com.umeng.analytics.social.UMPlatformData.GENDER;
import com.umeng.analytics.social.UMPlatformData.UMedia;

// import com.tulip.doctor_appointment.R;

public class UmengAnalyticsPlugin extends CordovaPlugin {
    private static Context mContext;
    private final  String mPageName = "index";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mContext = this.cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray data,
            CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            init();
        }
        if (action.equals("setDebugMode")) {
            boolean mode = data.getBoolean(0);
            setDebugMode(mode);
        }
        if (action.equals("onKillProcess")) {
            onKillProcess();
        }

        if (action.equals("onEventValue")) {
            String eventId = data.getString(0);
            JSONObject json = data.getJSONObject(1);
            Map<String, String> map = fromJSONObject(json);
            int du = data.getInt(2);
            onEventValue(eventId, map, du);
        }

        if (action.equals("onEvent")) {
            String eventId = data.getString(0);
            JSONObject json = data.getJSONObject(1);
            Map<String, String> map = fromJSONObject(json);
            onEvent(eventId, map);
        }

        if (action.equals("onEventCounter")) {
            String eventId = data.getString(0);
            String key  = data.getString(1);
            onEventCounter(eventId, key);
        }

        callbackContext.success();
        return true;
    }

    void init() {
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setAutoLocation(true);
        MobclickAgent.updateOnlineConfig(mContext);
    }

    void onEventCounter(String eventId, String eventName) {
        MobclickAgent.onEvent(mContext, eventId, eventName);
    }

    void onEvent(String eventId, Map<String, String> map) {
        MobclickAgent.onEvent(mContext, eventId, map);
    }

    void onEventValue(String eventId, Map<String, String> map, int du) {
        MobclickAgent.onEventValue(mContext, eventId, map, du);
    }

    void setDebugMode(boolean mode) {
        MobclickAgent.setDebugMode(mode);
    }

    @Override
    public void onPause(boolean multitasking) {
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onResume(boolean multitasking) {
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    void onKillProcess() {
        MobclickAgent.onKillProcess(mContext);
    }

    Map<String, String> fromJSONObject(JSONObject obj) throws JSONException {
        Iterator iter = obj.keys();
        Map<String, String> map = new HashMap<String, String>();

        while (iter.hasNext()) {
            String key = (String)iter.next();
            map.put(key, (String)obj.get(key));
        }

        return map;
    }
}
