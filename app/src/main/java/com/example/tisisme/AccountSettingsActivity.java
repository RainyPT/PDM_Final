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
import com.example.tisisme.AlunoActivities.DashboardStudentActivity;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.ProfessorActivities.DashboardProfessorActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountSettingsActivity extends AppCompatActivity {
    boolean firstTimer;
    EditText PrimeiroNomeText,SegundoNomeText,PasswordTextDefinicoes;
    int IDU;
    String Tipo;
    RequestQueue queue;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        queue = Volley.newRequestQueue(this);
        Intent i =getIntent();
        firstTimer=i.getBooleanExtra("firstTimer",false);
        PrimeiroNomeText=findViewById(R.id.PrimeiroNomeText);
        SegundoNomeText=findViewById(R.id.SegundoNomeText);
        PasswordTextDefinicoes=findViewById(R.id.PasswordTextDefinicoes);
        PasswordTextDefinicoes.setVisibility(View.INVISIBLE);
        sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        IDU=sp.getInt("ID",-1);
        Tipo=sp.getString("Tipo","Aluno");

    }
    private void switchToDash(){
        Intent i=new Intent(this, DashboardStudentActivity.class);
        if(Tipo.equals("Professor")){
            i=new Intent(this, DashboardProfessorActivity.class);
        }
        startActivity(i);
    }
    public void ChangeConfig(View v) throws JSONException {
        String PN=PrimeiroNomeText.getText().toString();
        String SN=SegundoNomeText.getText().toString();
        if(firstTimer) {
            if (PN.isEmpty() || SN.isEmpty()) {
                Toast.makeText(this, "Tens de preencher o campo \" Primeiro Nome \" e o \" Segundo Nome \"", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("PrimeiroNome", PN);
                editor.commit();
                UpdateInfo();
            }
        }
        else{

            UpdateInfo();

        }
    }
    private void UpdateInfo() throws JSONException {
        JSONObject reqOBJ=new JSONObject();
        reqOBJ.put("ID",IDU);
        reqOBJ.put("PN",PrimeiroNomeText.getText());
        reqOBJ.put("SN",SegundoNomeText.getText());
        reqOBJ.put("Password",PasswordTextDefinicoes.getText());
        reqOBJ.put("Tipo",Tipo);
        JsonObjectRequest jsObjRequest =
                new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/updateUserInfo",
                        reqOBJ,
                        (response -> {
                            try {
                                if(response.getInt("status")==1){
                                    switchToDash();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }), (error -> {

                }));
        queue.add(jsObjRequest);
    }
}