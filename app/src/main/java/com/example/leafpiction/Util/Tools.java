package com.example.leafpiction.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tools {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    isConnected = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    isConnected =  true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    isConnected =  true;
                }
            }
        }else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    public static JSONObject fix(JSONObject jsonObject){
        Iterator<String> keys = jsonObject.keys();
        List<String> removeKey = new ArrayList<>();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                boolean propertyValuePresent = jsonObject.get(key) != null
                        && jsonObject.get(key) != "null"
                        && !jsonObject.get(key).toString().isEmpty();
                if(!propertyValuePresent){
                    removeKey.add(key);
                }
            } catch ( Exception e){}
        }

        if(removeKey.size() > 0){
            for(int i = 0; i < removeKey.size(); i++){
                jsonObject.remove(removeKey.get(i));
            }
        }
        return jsonObject;
    }
}
