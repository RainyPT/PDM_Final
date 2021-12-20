package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tisisme.AccountSettingsActivity;
import com.example.tisisme.R;

public class DashboardProfessorActivity extends AppCompatActivity {

    private int ID;
    private String PrimeiroNome;
    TextView IDDashLabelProfessor,professorNameLabelDash;
    Button buttonCriarCadeira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_professor);
        IDDashLabelProfessor=findViewById(R.id.IDDashLabelProfessor);
        professorNameLabelDash=findViewById(R.id.professorNameLabelDash);
        SharedPreferences SP =getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        PrimeiroNome=SP.getString("PrimeiroNome","Unknown");
        IDDashLabelProfessor.setText(String.valueOf(SP.getInt("ID",-1)));
        buttonCriarCadeira=findViewById(R.id.buttonCriarCadeira);
        if(PrimeiroNome.isEmpty()){
            switchToAccountSettings();
            this.finish();
            Toast.makeText(this, "Tens de guardar o teu primeiro e segundo nome.", Toast.LENGTH_SHORT).show();
        }
        else{
            professorNameLabelDash.setText(PrimeiroNome);
        }
    }

    private void switchToAccountSettings(){
        Intent i = new Intent(this, AccountSettingsActivity.class);
        i.putExtra("firstTimer",true);
        startActivity(i);
    }

    public void switchToCadeiras(View v){
        Intent i =new Intent(this, ProfessorsCoursesActivity.class);
        startActivity(i);
    }
}