package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    Switch isProfSwitch;
    EditText UsernameTextBoxRegister,EmailTextBoxRegister,PasswordTextBoxRegister;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        queue = Volley.newRequestQueue(this);
        isProfSwitch=findViewById(R.id.isProfSwitch);
        UsernameTextBoxRegister=findViewById(R.id.UsernameTextBoxRegister);
        EmailTextBoxRegister=findViewById(R.id.EmailTextBoxRegister);
        PasswordTextBoxRegister=findViewById(R.id.PasswordTextBoxRegister);
        isProfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    UsernameTextBoxRegister.setText("");
                    UsernameTextBoxRegister.setHint("Username");
                    UsernameTextBoxRegister.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else{
                    UsernameTextBoxRegister.setText("");
                    UsernameTextBoxRegister.setHint("Número");
                    UsernameTextBoxRegister.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });
    }
    public void RegisterEvent(View v) throws JSONException {
        JSONObject reqOBJ=new JSONObject();
        reqOBJ.put("numero",UsernameTextBoxRegister.getText());
        reqOBJ.put("email",EmailTextBoxRegister.getText());
        reqOBJ.put("password",PasswordTextBoxRegister.getText());
        if(isProfSwitch.isChecked()){
            reqOBJ.put("isProf",true);
        }
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/register",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    switchToLogin();
                                    Toast.makeText(this, "Registo completado com sucesso!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(this, "Já existe um utilizador com esse identificador!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }
    private void switchToLogin(){
        Intent i =new Intent(this,LoginActivity.class);
        i.putExtra("usernameFromRegister",UsernameTextBoxRegister.getText().toString());
        i.putExtra("passwordFromRegister",PasswordTextBoxRegister.getText().toString());
        i.putExtra("isProfFromRegister",isProfSwitch.isChecked());
        startActivity(i);
        this.finish();
    }
}