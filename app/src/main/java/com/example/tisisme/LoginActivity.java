package com.example.tisisme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText passBox,userBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passBox=findViewById(R.id.passwordTextBoxLogin);
        userBox=findViewById(R.id.usernameTextBoxLogin);
        Intent fromRegister=getIntent();
        if(fromRegister.getStringExtra("RegisterNome")!=null && fromRegister.getStringExtra("RegisterPass")!=null){
            userBox.setText(fromRegister.getStringExtra("RegisterNome"));
            passBox.setText(fromRegister.getStringExtra("RegisterPass"));
        }
    }
    public void switchToRegister(View v){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        this.finish();
    }
}