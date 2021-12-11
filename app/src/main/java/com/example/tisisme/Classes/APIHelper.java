package com.example.tisisme.Classes;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class APIHelper {
    public static String URL="http://192.168.1.83:12345";
    private Context con;
    APIHelper(Context a){
        this.con=a;
    }
    public JSONObject sendPost(JSONObject objectToBeSent,String endpoint) throws InterruptedException {
        RequestQueue queue = Volley.newRequestQueue(this.con);
        JSONObject resOBJ=new JSONObject();
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + endpoint,
                        objectToBeSent,
                        (response -> {
                            try {
                                resOBJ.put("status",response.getInt("status"));
                                resOBJ.put("response",response.getJSONObject("response"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {
                            try {
                                resOBJ.put("status",-1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }));
        queue.add(jsObjRequest);
        //dumb attempt to try and fix async.
        Thread.sleep(500);
        return resOBJ;
    }


}