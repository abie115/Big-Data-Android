package com.android.bigdata.stepshunter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.bigdata.databaseconnection.ParseConnection;

public class RegistrationFailureActiviry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_failure);

        Intent intent = getIntent();
        String errorMessage = intent.getStringExtra(ParseConnection.ERROR_MESSAGE);

        TextView errorMessageView = (TextView) findViewById(R.id.registration_failure_show_error_message);
        errorMessageView.setText(errorMessage);

    }

    public void backToLoginPage(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
