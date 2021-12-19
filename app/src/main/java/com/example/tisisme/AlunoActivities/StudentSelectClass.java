package com.example.tisisme.AlunoActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.ProfessorActivities.ShowClassActivity;
import com.example.tisisme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentSelectClass extends AppCompatActivity {
    LinearLayout studentEnrollClassesList;
    int ID;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_select_class);
        studentEnrollClassesList=findViewById(R.id.studentEnrollClassesList);
        SharedPreferences SP = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        ID = SP.getInt("ID", -1);
        queue = Volley.newRequestQueue(this);
        try {
            displayClasses();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void displayClasses() throws JSONException{
        studentEnrollClassesList.removeAllViews();
        JSONObject reqOBJ =new JSONObject();
        reqOBJ.put("IDA",ID);
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getEnrolledClasses",
                        reqOBJ,
                        (response -> {
                            try {
                                if (response.getInt("status") == 1) {
                                    JSONArray presArray = response.getJSONArray("cadeiras");
                                    for (int i = 0; i < presArray.length(); i++) {
                                        Button a = new Button(this);
                                        a.setText(presArray.getJSONObject(i).getString("Nome")+" | "+presArray.getJSONObject(i).getString("Tipo"));
                                        final int IDAu=presArray.getJSONObject(i).getInt("IDAu");
                                        a.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent i = new Intent(StudentSelectClass.this, StudentPresences.class);
                                                i.putExtra("IDAu",IDAu);
                                                startActivity(i);
                                            }
                                        });
                                        studentEnrollClassesList.addView(a);
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