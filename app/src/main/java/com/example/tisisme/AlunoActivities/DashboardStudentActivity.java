package com.example.tisisme.AlunoActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tisisme.AccountSettingsActivity;
import com.example.tisisme.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DashboardStudentActivity extends AppCompatActivity {
    private int ID;
    private String Tipo, PrimeiroNome;
    TextView IDDashLabel, tipoLabelDash, userNameLabelDash;
    Button QRscanButton;
    Button presencasButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        IDDashLabel = findViewById(R.id.IDDashLabel);
        tipoLabelDash = findViewById(R.id.tipoLabelDash);
        userNameLabelDash = findViewById(R.id.professorNameLabelDash);
        QRscanButton = findViewById(R.id.QRscanButton);
        presencasButton = findViewById(R.id.presencasButton);
        SharedPreferences SP = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        Tipo = SP.getString("Tipo", "Aluno");
        ID = SP.getInt("ID", -1);
        IDDashLabel.setText(String.valueOf(ID));
        tipoLabelDash.setText(Tipo);
        PrimeiroNome = SP.getString("PrimeiroNome", "Unknown");
        if (PrimeiroNome.isEmpty()) {
            switchToAccountSettings();
            this.finish();
            Toast.makeText(this, "Tens de guardar o teu primeiro e segundo nome.", Toast.LENGTH_SHORT).show();
        } else {
            userNameLabelDash.setText(PrimeiroNome);
        }
    }
    public void switchToQR(View v) {

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("SCAN");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Result != null) {
            if (Result.getContents() == null) {
                Toast.makeText(this, "Leitura QR Cancelada.", Toast.LENGTH_SHORT).show();
            } else {
                /*Enviar um monte de merdas para a base de dados, e fazer verificação se a data e hora estão dentro dos limites permitidos.
                Estas verificações devem ser completamente server sided para garantir a melhor proteção/anticheating possivel.*/
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Por favor concede permissões de GPS a esta aplicação.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void switchToAccountSettings(){
        Intent i = new Intent(this, AccountSettingsActivity.class);
        i.putExtra("firstTimer",true);
        startActivity(i);
    }

    public void switchToPresencasAluno(View v){
        Intent i =new Intent(this, StudentPresences.class);
        startActivity(i);
    }
}