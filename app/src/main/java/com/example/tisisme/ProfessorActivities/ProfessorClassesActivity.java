package com.example.tisisme.ProfessorActivities;

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
import com.example.tisisme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfessorClassesActivity extends AppCompatActivity {

    LinearLayout aulasProfessor;
    RequestQueue queue;
    int cadeiraID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_classes);
        aulasProfessor=findViewById(R.id.AulasProfessor);
        queue= Volley.newRequestQueue(this);
        SharedPreferences SP =getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        cadeiraID=SP.getInt("SelectedCadeira",-1);
        JSONObject reqOBJ=new JSONObject();
        try {
            reqOBJ.put("cadeiraID",cadeiraID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(cadeiraID!=-1){
            JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getAulas",
                            reqOBJ,
                            (response -> {
                                try {
                                    if(response.getInt("status")==1){
                                        JSONArray aulasJsonArray=response.getJSONArray("aulas");
                                        displayAulas(aulasJsonArray);
                                        if(aulasJsonArray.length()==0){
                                            Toast.makeText(this, "Não existem aulas para esta cadeira", Toast.LENGTH_SHORT).show();
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
    public void switchToCriarAulas(View v){
        Intent i=new Intent(this,CreateClassesActivity.class);
        startActivity(i);
    }
    private void displayAulas(JSONArray aulas) throws JSONException{
        aulasProfessor.removeAllViews();
        for(int i=0; i< aulas.length(); i++){
            Button a = new Button(this);
            a.setText(aulas.getJSONObject(i).getString("Tipo"));
            final int tempi= i;
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int aulaID= aulas.getJSONObject(tempi).getInt("IDAu");
                        Intent i=new Intent(ProfessorClassesActivity.this,ShowClassActivity.class);
                        i.putExtra("IDAu",aulaID);
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            aulasProfessor.addView(a);
        }

    }
}