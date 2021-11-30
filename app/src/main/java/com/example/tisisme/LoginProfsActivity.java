package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class LoginProfsActivity extends AppCompatActivity {
    EditText passBox,userBox;
    RequestQueue queue;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profs);
        queue = Volley.newRequestQueue(this);
        passBox=findViewById(R.id.passwordTextBoxLoginProfs);
        userBox=findViewById(R.id.usernameTextBoxLoginProfs);
    }
    public void LoginProfsEvent (View v) throws JSONException {
        if(!userBox.getText().toString().isEmpty() && !passBox.getText().toString().isEmpty()) {
            JSONObject jsonOBJ = new JSONObject();
            jsonOBJ.put("numero", userBox.getText().toString());
            jsonOBJ.put("password", passBox.getText().toString());
            jsonOBJ.put("isProf",1);
            String endpoint=APIHelper.URL +"/login";
            JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, endpoint,
                            jsonOBJ,
                            (response -> {
                                try {
                                    if (response.getInt("status") == 1) {
                                        changeToDashboard(response.getInt("ID"));
                                    } else {
                                        Toast.makeText(this, "Credenciais ou Password errada.", Toast.LENGTH_SHORT).show();
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
    public void switchToAlunosLogin(View v){
        Intent i= new Intent(this,LoginAlunosActivity.class);
        startActivity(i);
        this.finish();
    }
    public void switchToRegisterProfs(View v){
        Intent i=new Intent(this, RegisterProfsActivity.class);
        startActivity(i);
    }
    private void changeToDashboard(int IDA){
        Intent i= new Intent(this,DashboardAlunoActivity.class);
        initSharedPref(IDA);
        startActivity(i);
        this.finish();
    }
    private void initSharedPref(int IDA){
        sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("ID",IDA);
        editor.commit();
    }
}