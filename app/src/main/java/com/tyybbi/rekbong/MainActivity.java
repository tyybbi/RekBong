package com.tyybbi.rekbong;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RekDebug";
    //protected String[] plates = new String[1000];
    DBHandler mDBHandler;
    ArrayList<Plate> dbContent;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDBHandler = new DBHandler(this);
        dbContent = mDBHandler.readAllPlates();

        /* Populate plates[] with some lorem ipsum
        int i = 1;
        while (i < 1000) {
            plates[i-1] = "IUU-" + i;
            Log.i(TAG, "plates[i-1] = " + plates[i-1] + ", i = " + i);
            i++;
        }
        */

        ArrayAdapter<Plate> itemsAdapter =
            new ArrayAdapter<Plate>(this, android.R.layout.simple_list_item_1, dbContent);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long curDate = date.getTime();
                mDBHandler.addNewPlate("IVS-666", curDate);
                Snackbar.make(view, "New plate added!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
