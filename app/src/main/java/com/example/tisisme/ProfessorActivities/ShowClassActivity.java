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
import android.widget.ImageView;
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

    private ImageView qrCodeIV;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    int IDAu;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_class);
        queue= Volley.newRequestQueue(this);
        Intent i=getIntent();
        IDAu=i.getIntExtra("IDAu",-1);
        qrCodeIV = findViewById(R.id.idIVQrcode);
    }

    public void genQRForClass(View view) throws JSONException{
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        final int dimenTemp=dimen;
        JSONObject reqOBJ =new JSONObject();
        reqOBJ.put("IDAu",IDAu);
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getIDP2A",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    qrgEncoder = new QRGEncoder(String.valueOf(response.getInt("IDP2A")), null, QRGContents.Type.TEXT, dimenTemp);
                                    try {
                                        bitmap = qrgEncoder.encodeAsBitmap();
                                        qrCodeIV.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        Log.e("Tag", e.toString());
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }

}