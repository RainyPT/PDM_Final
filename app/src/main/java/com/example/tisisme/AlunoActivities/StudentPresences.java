package com.example.tisisme.AlunoActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentPresences extends AppCompatActivity {

    LinearLayout listaPresencas;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presencas_aluno);
        queue= Volley.newRequestQueue(this);
        listaPresencas=findViewById(R.id.listaCadeirasPresencas);
        Intent i=getIntent();
        int IDAu=i.getIntExtra("IDAu",-1);
        SharedPreferences SP =getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        int IDA=SP.getInt("ID",-1);
        JSONObject reqOBJ=new JSONObject();
        try {
            reqOBJ.put("IDA",IDA);
            reqOBJ.put("IDAu",IDAu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getPresencasOfAluno",
                        reqOBJ,
                        (response -> {
                            try {
                                displayPresencas(response.getJSONArray("faltas"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }
    private void displayPresencas(JSONArray f) throws JSONException {
        listaPresencas.removeAllViews();
        for(int i=0;i<f.length();i++){
            TextView a = new TextView(this);
            a.setText("Data: "+f.getJSONObject(i).getString("Data").split("T")[0]);
            a.setBackgroundColor(Color.RED);
            a.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            a.setTextSize(20.0f);
            listaPresencas.addView(a);
        }
    }
}