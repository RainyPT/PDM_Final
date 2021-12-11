package com.example.tisisme.AlunoActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.AccountSettingsActivity;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardStudentActivity extends AppCompatActivity {
    private int ID;
    private String Tipo, PrimeiroNome;
    TextView IDDashLabel, tipoLabelDash, userNameLabelDash;
    Button QRscanButton;
    Button presencasButton;
    protected LocationManager locationManager;
    private Location userLocation;
    RequestQueue queue;

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
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            startTrackingLocation();
            queue = Volley.newRequestQueue(this);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this, "Impedido de usar a funcionalidade de marcar presenças!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
    private void startTrackingLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        userLocation = location;
                    }
                });
            }
            else{
                Toast.makeText(this, "GPS desligado. Por favor ligue e reinicie a aplicação.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Result != null) {
            if (Result.getContents() == null) {
                Toast.makeText(this, "Leitura QR Cancelada.", Toast.LENGTH_SHORT).show();
            } else {
                int IDP2A=Integer.parseInt(Result.getContents());
                if(userLocation!=null){
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    String name = wifiInfo.getSSID();
                    JSONObject reqOBJ = new JSONObject();
                    try {
                        reqOBJ.put("ID",ID);
                        reqOBJ.put("WifiName",name);
                        reqOBJ.put("IDP2A",IDP2A);
                        reqOBJ.put("X",userLocation.getLatitude());
                        reqOBJ.put("Y",userLocation.getLongitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsObjRequest =
                            new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/addPresence",
                                    reqOBJ,
                                    (response -> {
                                        try {
                                            if(response.getInt("status")==1){
                                                Toast.makeText(this, "Presença marcada!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(this,"Já marcou presença hoje!",Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }), (error -> {

                            }));
                    queue.add(jsObjRequest);
                }
                else{
                    Toast.makeText(this, "Certifique-se que o GPS está ligado e/ou espere uns minutos.", Toast.LENGTH_SHORT).show();
                }
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