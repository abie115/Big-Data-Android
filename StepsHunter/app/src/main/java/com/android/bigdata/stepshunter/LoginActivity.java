package com.android.bigdata.stepshunter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.bigdata.databaseconnection.ParseConnection;
import com.android.bigdata.helper.ShowMessage;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToRegistration(View view){
        //go to registration page
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        EditText login = (EditText) findViewById(R.id.loginUsername);
        EditText password = (EditText) findViewById(R.id.loginPassword);

        //if one fild is empty show error message
        if(login.getText().toString().matches("") || password.getText().toString().matches(""))
            ShowMessage.showOkDialog(getString(R.string.emptyField), this);
        else ParseConnection.login(login.getText().toString(), password.getText().toString(), this);
    }
}
