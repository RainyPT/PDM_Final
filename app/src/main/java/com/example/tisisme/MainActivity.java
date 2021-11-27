package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    EditText usernameTextBox,passwordTextBox;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameTextBox=findViewById(R.id.usernameTextBoxRegister);
        passwordTextBox=findViewById(R.id.passwordTextBoxRegister);
        queue = Volley.newRequestQueue(this);
    }

    public void registerEvent(View v) throws JSONException{
        if(!usernameTextBox.getText().toString().isEmpty() && !passwordTextBox.getText().toString().isEmpty()) {
            JSONObject jsonOBJ = new JSONObject();
            jsonOBJ.put("username", usernameTextBox.getText().toString());
            jsonOBJ.put("password", passwordTextBox.getText().toString());
            JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/registerUser",
                            jsonOBJ,
                            (response -> {
                                try {
                                    if (response.getInt("status") == 1) {
                                        Toast.makeText(this, "Utilizador registado!", Toast.LENGTH_SHORT).show();
                                        switchToLogin();
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
    private void switchToLogin(){
        Intent i=new Intent(this, LoginActivity.class);
        i.putExtra("RegisterNome",usernameTextBox.getText().toString());
        i.putExtra("RegisterPass",passwordTextBox.getText().toString());
        startActivity(i);
        this.finish();

    }
}