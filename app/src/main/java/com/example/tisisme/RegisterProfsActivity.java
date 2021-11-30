package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterProfsActivity extends AppCompatActivity {
    EditText userBox,passBox,emailBox;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profs);
        userBox=findViewById(R.id.usernameTextBoxRegisterProfs);
        passBox=findViewById(R.id.passwordTextBoxRegisterProfs);
        emailBox=findViewById(R.id.emailTextBoxRegisterProfs);
        queue = Volley.newRequestQueue(this);
    }
    public void registerProfEvent(View v) throws JSONException {
        if(!userBox.getText().toString().isEmpty() && !passBox.getText().toString().isEmpty()) {
            JSONObject jsonOBJ = new JSONObject();
            jsonOBJ.put("numero", userBox.getText().toString());
            jsonOBJ.put("email", emailBox.getText().toString());
            jsonOBJ.put("password", passBox.getText().toString());
            jsonOBJ.put("isProf",1);
            JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/register",
                            jsonOBJ,
                            (response -> {
                                try {
                                    if (response.getInt("status") == 1) {
                                        Toast.makeText(this, "Utilizador registado!", Toast.LENGTH_SHORT).show();
                                        switchToProfLogin();
                                    } else {
                                        Toast.makeText(this, "Nome já existe na base de dados!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }), (error -> {
                        Toast.makeText(this, "Algo correu mal...", Toast.LENGTH_SHORT).show();
                    }));
            queue.add(jsObjRequest);
        }
        else{
            Toast.makeText(this, "Preenche todos os campos.", Toast.LENGTH_SHORT).show();
        }

    }
    private void switchToProfLogin(){
        Intent i=new Intent(this, LoginProfsActivity.class);
        i.putExtra("RegisterNome",userBox.getText().toString());
        i.putExtra("RegisterPass",passBox.getText().toString());
        startActivity(i);
    }
}