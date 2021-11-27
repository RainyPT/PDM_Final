package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    EditText passBox,userBox;
    RequestQueue queue;
    APIHelper apiHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passBox=findViewById(R.id.passwordTextBoxLogin);
        userBox=findViewById(R.id.usernameTextBoxLogin);
        queue = Volley.newRequestQueue(this);
        apiHelper=new APIHelper(this);
        Intent fromRegister=getIntent();
        //if user comes from MainActivity.
        if(fromRegister.getStringExtra("RegisterNome")!=null && fromRegister.getStringExtra("RegisterPass")!=null){
            userBox.setText(fromRegister.getStringExtra("RegisterNome"));
            passBox.setText(fromRegister.getStringExtra("RegisterPass"));
        }
    }
    public void LoginEvent(View v) throws JSONException, InterruptedException {
        if(!userBox.getText().toString().isEmpty() && !passBox.getText().toString().isEmpty()) {
            JSONObject jsonOBJ = new JSONObject();
            jsonOBJ.put("username", userBox.getText().toString());
            jsonOBJ.put("password", passBox.getText().toString());
           /* JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/login",
                            jsonOBJ,
                            (response -> {
                                try {
                                    if (response.getInt("status") == 1) {
                                        Toast.makeText(this, "Loggado!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Credenciais ou Password errada.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }), (error -> {
                        Toast.makeText(this, "Algo correu mal...", Toast.LENGTH_SHORT).show();
                    }));
            queue.add(jsObjRequest);*/
            JSONObject receivedObj=apiHelper.sendPost(jsonOBJ,"/login");
            if(receivedObj.length()>0){
                if (receivedObj.getInt("status") == 1) {
                    Toast.makeText(this, "Loggado!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Credenciais ou Password errada.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(this, "Preenche todos os campos.", Toast.LENGTH_SHORT).show();
        }
    }
    public void switchToRegister(View v){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        this.finish();
    }
}