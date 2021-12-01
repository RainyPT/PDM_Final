package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Switch isProfSwitch;
    EditText UsernameTextBoxLogin,PasswordTextBoxLogin;
    RequestQueue queue;
    /*
    i.putExtra("usernameFromRegister",UsernameTextBoxRegister.getText().toString());
    i.putExtra("passwordFromRegister",PasswordTextBoxRegister.getText().toString());
    i.putExtra("isProfFromRegister",isProfSwitch.isChecked());
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        isProfSwitch=findViewById(R.id.isProfSwitch);
        UsernameTextBoxLogin=findViewById(R.id.UsernameTextBoxLogin);
        PasswordTextBoxLogin=findViewById(R.id.PasswordTextBoxLogin);
        Intent i =getIntent();
        if(i.getStringExtra("usernameFromRegister")!=null){
            isProfSwitch.setChecked(i.getBooleanExtra("isProfFromRegister",false));
            UsernameTextBoxLogin.setText(i.getStringExtra("usernameFromRegister"));
            PasswordTextBoxLogin.setText(i.getStringExtra("passwordFromRegister"));
        }
        isProfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    UsernameTextBoxLogin.setText("");
                    UsernameTextBoxLogin.setHint("Username");
                    UsernameTextBoxLogin.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else{
                    UsernameTextBoxLogin.setText("");
                    UsernameTextBoxLogin.setHint("Número");
                    UsernameTextBoxLogin.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });
    }
    public void LoginEvent(View V) throws JSONException {
        JSONObject reqOBJ=new JSONObject();
        reqOBJ.put("numero",UsernameTextBoxLogin.getText());
        reqOBJ.put("password",PasswordTextBoxLogin.getText());
        if(isProfSwitch.isChecked()){
            reqOBJ.put("isProf",true);
        }
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/login",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    initSharedPrefs(UsernameTextBoxLogin.getText().toString(),isProfSwitch.isChecked(),response.getInt("ID"),response.getString("PN"));
                                    switchToDashboard();
                                }
                                else{
                                    Toast.makeText(this, "Credenciais erradas!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }
    private void initSharedPrefs(String username,Boolean perms,int ID,String PN){
        String tipo="Aluno";
        SharedPreferences sp=getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Username",username);
        if(perms)
            tipo="Professor";
        editor.putString("PrimeiroNome",PN);
        editor.putString("Tipo",tipo);
        editor.putInt("ID",ID);
        editor.commit();
    }
    public void switchToDashboard(){
        Intent i =new Intent(this,DashboardActivity.class);
        startActivity(i);
        this.finish();
    }
    public void switchToRegisterActivity(View v){
        Intent i =new Intent(this,RegisterActivity.class);
        startActivity(i);
    }
}