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
import android.widget.TextView;
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
    TextView assMediaLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_class);
        presencasLayout=findViewById(R.id.listaPresencasNaAula);
        assMediaLabel=findViewById(R.id.assMediaLabel);
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
                                if (response.getInt("status") == 1) {
                                    presencasLayout.removeAllViews();
                                    JSONArray presArray = response.getJSONArray("presencas");
                                    Double assMedia=response.getDouble("assMedia");
                                    assMediaLabel.setText(assMedia+"%");
                                    for (int i = 0; i < presArray.length(); i++) {
                                        Button a = new Button(this);
                                        a.setText(presArray.getJSONObject(i).getString("IDA"));
                                        a.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(ShowClassActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
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
        Intent i =new Intent(this,ShowQRActivity.class);
        i.putExtra("IDAu",IDAu);
        startActivity(i);
    }

    public void refreshPage(View view) throws JSONException {
        displayPresencas();
    }
}