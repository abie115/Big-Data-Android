package com.android.bigdata.stepshunter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.bigdata.databaseconnection.ParseConnection;
import com.android.bigdata.helper.ShowMessage;
import com.parse.ParseUser;

public class RegistrationSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);
    }

    public void goToApplication(View view){
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null)
            ShowMessage.showOkDialog("Nie można wykonać operacji - nie jesteś zalogowany/a.",this);
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
