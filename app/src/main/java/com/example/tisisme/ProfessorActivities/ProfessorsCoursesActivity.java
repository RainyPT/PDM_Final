package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class ProfessorsCoursesActivity extends AppCompatActivity {

    Button buttonCriarCadeiras;
    LinearLayout cadeirasProfessor;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadeiras_professor);
        buttonCriarCadeiras=findViewById(R.id.buttonCriarCadeira);
        cadeirasProfessor=findViewById(R.id.AulasProfessor);
        queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.GET, APIHelper.URL + "/getCadeiras",
                        null,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    JSONArray cadeirasJsonArray=response.getJSONArray("cadeiras");
                                   displayCadeiras(cadeirasJsonArray);
                                   if(cadeirasJsonArray.length()==0){
                                       Toast.makeText(this, "Não existem cadeiras", Toast.LENGTH_SHORT).show();
                                   }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }

    public void switchToCriarCadeiras(View v){
        Intent i =new Intent(this, CreateCoursesActivity.class);
        startActivity(i);
    }

    private void displayCadeiras(JSONArray cadeirasArray) throws JSONException {
        cadeirasProfessor.removeAllViews();
        for(int i=0; i<cadeirasArray.length(); i++){
            Button a = new Button(this);
            a.setText(cadeirasArray.getJSONObject(i).getString("Nome"));
            final int tempi= i;
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int cadeiraID=cadeirasArray.getJSONObject(tempi).getInt("IDC");
                        Intent i=new Intent(ProfessorsCoursesActivity.this,ProfessorClassesActivity.class);
                        SharedPreferences sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putInt("SelectedCadeira",cadeiraID);
                        editor.commit();
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            cadeirasProfessor.addView(a);
        }
    }

}
