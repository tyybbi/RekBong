package com.tyybbi.rekbong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RekDebug";
    DBHandler mDBHandler;
    ArrayList<Plate> dbContent;
    Date date = new Date();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDBHandler = new DBHandler(this);
        dbContent = mDBHandler.readAllPlates();

        Log.i(TAG, "dbContent.size() = " + dbContent.size());
        ArrayAdapter<Plate> itemsAdapter =
            new ArrayAdapter<Plate>(this, android.R.layout.simple_list_item_1, dbContent);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Alert Dialog stuff
                LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.dialog_addplate, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText plateInput = (EditText) promptsView
						.findViewById(R.id.plateEditText);

				// set dialog message
				alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to inputText
                                        long curDateMillis = date.getTime();
                                        DateFormat simpleFormat = new SimpleDateFormat("yyyy-mm-dd_HH:mm:ss:SSS Z");
                                        Date readableDate = new Date(curDateMillis);
                                        String inputText = plateInput.getText().toString();
                                        mDBHandler.addNewPlate(inputText, curDateMillis);
                                        Log.i(TAG, "readableDate: " + simpleFormat.format(readableDate));
                                        Log.i(TAG, "inputText: " + inputText);
                                        //Snackbar.make(view, "New plate added!", Snackbar.LENGTH_LONG)
                                        //        .setAction("Action", null).show();
                                        //mDBHandler.addNewPlate("IVS-666", curDateMillis);
                                    }
                                })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					    }
					  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
            }
        });
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
}
