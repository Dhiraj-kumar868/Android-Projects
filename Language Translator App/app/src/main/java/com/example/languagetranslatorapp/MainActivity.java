package com.example.languagetranslatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner s1,s2;
    private MaterialButton bt1;
    private ImageView mic;
    private TextInputEditText ed1;
    private TextView tv1;

    String[] from={"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};
    String[] to={"To","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};

    private static final int REQUEST_PERMISSION_CODE=1;
    int languageCode,fromLanguageCode,toLanguageCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s1=findViewById(R.id.s1);
        s2=findViewById(R.id.s2);
        bt1=findViewById(R.id.bt1);
        mic=findViewById(R.id.mic);
        ed1=findViewById(R.id.ed1);
        tv1=findViewById(R.id.tv1);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(from[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,from);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(fromAdapter);

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(to[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter=new ArrayAdapter(this,R.layout.spinner_item,to);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(toAdapter);

        bt1.setOnClickListener(this);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try{
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch(Exception e){
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(resultCode==RESULT_OK && data!=null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ed1.setText(result.get(0));
            }
        }
    }

    //String[] from={"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan","Czech","Welsh","Hindi","Urdu"};
    public int getLanguageCode(String language){
        int languageCode=0;
        switch(language)
        {
            case "English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode= FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode= FirebaseTranslateLanguage.AR;
                break;
            case "Bulgarian":
                languageCode= FirebaseTranslateLanguage.BG;
                break;
            case "Belarusian":
                languageCode= FirebaseTranslateLanguage.BE;
                break;
            case "Bengali":
                languageCode= FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                languageCode= FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                languageCode= FirebaseTranslateLanguage.CS;
                break;
            case "Welsh":
                languageCode= FirebaseTranslateLanguage.CY;
                break;
            case "Hindi":
                languageCode= FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode= FirebaseTranslateLanguage.UR;
                break;

            default:
                languageCode=0;
        }
        return languageCode;
    }


    @Override
    public void onClick(View v) {
        tv1.setText("");
        if(ed1.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
        }else if(fromLanguageCode==0){
            Toast.makeText(this, "Please select source language", Toast.LENGTH_SHORT).show();
        }else if(toLanguageCode==0){
            Toast.makeText(this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
        }else{
            translateText(fromLanguageCode,toLanguageCode,ed1.getText().toString());
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
         tv1.setText("Downloading Modal...");
        FirebaseTranslatorOptions options=new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                tv1.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        tv1.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to download language modal"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}