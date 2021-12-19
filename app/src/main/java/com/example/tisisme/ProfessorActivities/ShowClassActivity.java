package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.R;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ShowClassActivity extends AppCompatActivity {
    int IDAu;
    RequestQueue queue;
    LinearLayout presencasLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_class);
        presencasLayout=findViewById(R.id.listaPresencasNaAula);
        queue= Volley.newRequestQueue(this);
        Intent i=getIntent();
        IDAu=i.getIntExtra("IDAu",-1);

        try {
            displayPresencas();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayPresencas() throws JSONException {
        JSONObject reqOBJ =new JSONObject();
        reqOBJ.put("IDAu",IDAu);
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getPresencas",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    presencasLayout.removeAllViews();
                                    JSONArray presArray=response.getJSONArray("presencas");
                                    for(int i=0;i<presArray.length();i++){
                                        Button a = new Button(this);
                                        a.setText(presArray.getJSONObject(i).getString("IDA"));
                                        final int tempi= i;
                                        a.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    int aulaID = presArray.getJSONObject(tempi).getInt("IDAu");
                                                    Toast.makeText(ShowClassActivity.this, "" + aulaID, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        presencasLayout.addView(a);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }
    public void genQRForClass(View view) throws JSONException{
        JSONObject reqOBJ =new JSONObject();
        reqOBJ.put("IDAu",IDAu);
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getIDP2A",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    switchToQRActivity(response.getInt("IDP2A"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }

    private void switchToQRActivity(int idp2a){
        Intent i =new Intent(this,ShowQRActivity.class);
        i.putExtra("IDP2A",idp2a);
        startActivity(i);
    }

    public void refreshPage(View view) throws JSONException {
        displayPresencas();
    }
}