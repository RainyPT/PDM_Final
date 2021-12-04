package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.APIHelper;
import com.example.tisisme.R;

import org.json.JSONException;
import org.json.JSONObject;

public class criarCadeirasActivity extends AppCompatActivity {

    Button criarCadeirasFinal;
    EditText editTextNomedaCadeira;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_cadeiras);
        criarCadeirasFinal=findViewById(R.id.criarCadeirasFinal);
        editTextNomedaCadeira=findViewById(R.id.editTextNomedaCadeira);
        queue = Volley.newRequestQueue(this);


    }

    public void RegisterEventCadeiras(View v) throws JSONException {
        JSONObject reqOBJ=new JSONObject();
        reqOBJ.put("nomeCadeira",editTextNomedaCadeira.getText());
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/registerCadeiras",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    switchToCadeiras();
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


    public void switchToCadeiras(){
        Intent i =new Intent(this, CadeirasProfessorActivity.class);
        startActivity(i);
    }
}