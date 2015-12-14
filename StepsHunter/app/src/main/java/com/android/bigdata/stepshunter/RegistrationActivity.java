package com.android.bigdata.stepshunter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.bigdata.databaseconnection.ParseConnection;
import com.android.bigdata.helper.ShowMessage;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registerAccount(View view){
        EditText login = (EditText) findViewById(R.id.registrationLogin);
        EditText password = (EditText) findViewById(R.id.registrationPassword);
        EditText repeatedPassword = (EditText) findViewById(R.id.repeatedPassword);

        //if one fild is empty show error message
        if(login.getText().toString().matches("") || password.getText().toString().matches("")
                || repeatedPassword.getText().toString().matches(""))
            ShowMessage.showOkDialog(getString(R.string.emptyField), this);

        else {
            //if 2 passwords are not equals
            if(!password.getText().toString().equals(repeatedPassword.getText().toString()))
                ShowMessage.showOkDialog(getString(R.string.passwordsNotEquals), this);

            else {
                //Intent intent = new Intent(this, RegistrationFailureActiviry.class);
                //startActivity(intent);
                ParseConnection.register(login.getText().toString(),password.getText().toString(),this);
                //ShowMessage.showOkDialog("Wszystko wstępnie OK | "+login.getText().toString(), this); //wydruk kontrolny
            }
        }

    }
}
