package com.example.tisisme.ProfessorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tisisme.Classes.APIHelper;
import com.example.tisisme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateClassesActivity extends AppCompatActivity{
    Spinner spinnerWeekDays;
    String weekDaySelected;
    EditText editTextHoraAula,editTextTipoAula;
    RequestQueue queue;
    int IDP,IDC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classes);
        queue= Volley.newRequestQueue(this);
        SharedPreferences SP =getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        IDP=SP.getInt("ID",1);
        IDC=SP.getInt("SelectedCadeira",-1);
        spinnerWeekDays=findViewById(R.id.weekDaySpinner);
        editTextHoraAula=findViewById(R.id.editTextHoraAula);
        editTextTipoAula=findViewById(R.id.editTextTipoAula);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.weekDays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeekDays.setAdapter(adapter);
        spinnerWeekDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weekDaySelected=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void CriarAulaEvent(View v) throws JSONException{
        String TipoAula=editTextTipoAula.getText().toString();
        String HoraAula=editTextHoraAula.getText().toString();
        if(!(TipoAula.isEmpty() && HoraAula.isEmpty())){
            JSONObject reqOBJ=new JSONObject();
            reqOBJ.put("Hora",HoraAula);
            reqOBJ.put("DiaDaSemana",weekDaySelected);
            reqOBJ.put("Tipo",TipoAula);
            reqOBJ.put("IDP",IDP);
            if(IDC==-1){
                Toast.makeText(this, "Error a tentar criar a cadeira!", Toast.LENGTH_SHORT).show();
                return;
            }
            reqOBJ.put("IDC",IDC);
            JsonObjectRequest jsObjRequest =
                    new JsonObjectRequest(Request.Method.POST, APIHelper.URL + "/addAula",
                            reqOBJ,
                            (response -> {
                                try {
                                    if(response.getInt("status")==1){
                                        Toast.makeText(this, "Aula adicionada!", Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(CreateClassesActivity.this,ProfessorClassesActivity.class);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(this, "Aula com esse Tipo já existe!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }), (error -> {

                    }));
            queue.add(jsObjRequest);
        }
    }
}