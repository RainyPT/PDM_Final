package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardAlunoActivity extends AppCompatActivity {
    TextView userIDLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_aluno);
        userIDLabel=findViewById(R.id.UserIDLabel);
        SharedPreferences sp=getSharedPreferences("UserInfo",MODE_PRIVATE);
        int ID=sp.getInt("ID",-1);
        if(ID==-1){
            Toast.makeText(this, "Algo correu muito mal.", Toast.LENGTH_SHORT).show();
        }
        else {
            userIDLabel.setText(String.valueOf(ID));
        }
    }
}