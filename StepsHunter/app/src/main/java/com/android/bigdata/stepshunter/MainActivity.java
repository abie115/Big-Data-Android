package com.android.bigdata.stepshunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        frequency = (EditText) findViewById(R.id.frequency_value);
        frequency.setText(Long.toString(HunterService.getCurrentFrequenct()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFrequency(View view){
        String message;

        if(frequency.getText().toString().matches(""))
            message = "Musisz podać wartość.";
        else {
            long newFrequency = Long.parseLong(frequency.getText().toString());

            if (newFrequency < HunterService.getMinFrequency())
                message = "Podana wartość jest zbyt niska.";
            else if (newFrequency > HunterService.getMaxFrequency())
                message = "Podana wartość jest zbyt wysoka.";
            else {
                HunterService.setCurrentFrequency(newFrequency);
                message = "Zmieniono na " + HunterService.getCurrentFrequenct() + " sek.";
            }
        }

        showDialog(message);
    }

    public void setDefaultFrequency(View view){
        HunterService.setDefaultFrequency();

        frequency.setText(Long.toString(HunterService.getCurrentFrequenct()));

        showDialog("Przywrócono " + HunterService.getCurrentFrequenct() + " sek.");
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
