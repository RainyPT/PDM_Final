package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.APIHelper;
import com.example.tisisme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class CadeirasProfessorActivity extends AppCompatActivity {

    Button buttonCriarCadeiras;
    LinearLayout cadeirasProfessor;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadeiras_professor);
        buttonCriarCadeiras=findViewById(R.id.buttonCriarCadeira);
        cadeirasProfessor=findViewById(R.id.cadeirasProfessor);
        queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/getCadeiras",
                        null,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                   displayCadeiras(response.getJSONArray("cadeiras"));
                                    Toast.makeText(this, "Registo completado com sucesso!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(this, "Já existe uma cadeira com esse identificador!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }

    public void switchToCriarCadeiras(View v){
        Intent i =new Intent(this, criarCadeirasActivity.class);
        startActivity(i);
    }

    private void displayCadeiras(JSONArray nomesCadeiras) throws JSONException {
        cadeirasProfessor.removeAllViews();
        for(int i=0; i<nomesCadeiras.length(); i++){
            Button a = new Button(this);
            a.setText(nomesCadeiras.getJSONObject(i).getString("Nome"));
            cadeirasProfessor.addView(a);
        }
    }

}
