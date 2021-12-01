package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {
    private int ID;
    private String Tipo,PrimeiroNome;
    TextView IDDashLabel,tipoLabelDash,userNameLabelDash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        IDDashLabel=findViewById(R.id.IDDashLabel);
        tipoLabelDash=findViewById(R.id.tipoLabelDash);
        userNameLabelDash=findViewById(R.id.userNameLabelDash);
        SharedPreferences SP =getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        Tipo=SP.getString("Tipo","Aluno");
        ID=SP.getInt("ID",-1);
        IDDashLabel.setText(String.valueOf(ID));
        tipoLabelDash.setText(Tipo);
        PrimeiroNome=SP.getString("PrimeiroNome","Unknown");
        if(PrimeiroNome.isEmpty()){
            switchToAccountSettings();
            this.finish();
            Toast.makeText(this, "Tens de guardar o teu primeiro e segundo nome.", Toast.LENGTH_SHORT).show();
        }
        else{
            userNameLabelDash.setText(PrimeiroNome);
        }
    }
    private void switchToAccountSettings(){
        Intent i = new Intent(this,AccountSettingsActivity.class);
        i.putExtra("firstTimer",true);
        startActivity(i);
    }
}