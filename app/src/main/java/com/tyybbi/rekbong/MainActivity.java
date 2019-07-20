package com.tyybbi.rekbong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RekDebug";
    private static final String DASH = "-";
    DBHandler dbHandler;
    Cursor dbCursor;
    Date date = new Date();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = new DBHandler(this);
        dbCursor = dbHandler.readAllPlates();

        final CustomCursorAdapter itemsAdapter =
                new CustomCursorAdapter(this, dbCursor);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        Log.i(TAG, "Spotting percent: " +
                String.format("%.2f", calculatePercent(listView.getAdapter().getCount())));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

                final Plate plate = new Plate();

                Log.i(TAG, "Long press: pos: " + pos + ", id: " + id);
                final int position = pos;

                // Alert Dialog stuff
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_editdel_plate, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText plateEditdelEt = (EditText) promptsView
                        .findViewById(R.id.editdelPlateEditText);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_btn_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //TODO Edit plate

                                        Log.i(TAG, "Edited plate in position " + position);
                                        //Snackbar.make(view, R.string.snackbar_add, Snackbar.LENGTH_LONG)
                                        //        .setAction("Action", null).show();
                                    }
                                })
                        .setNeutralButton(R.string.dlg_btn_delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //TODO Delete plate

                                        Log.i(TAG, "Deleted plate in position " + position);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(R.string.dlg_btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final Plate plate = new Plate();

                // Alert Dialog stuff
                LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.dialog_addplate, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText plateLetterPartInputEt = (EditText) promptsView
						.findViewById(R.id.addPlateLetterPartEt);

				final EditText plateNumberPartInputEt = (EditText) promptsView
                       .findViewById(R.id.addPlateNumberPartEt);

				// set dialog message
				alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_btn_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        boolean success = true;

                                        // Get plate and datetime, store them
                                        long currentDateMS = date.getTime();
                                        String inputLP = plateLetterPartInputEt.getText().toString();
                                        String inputNP = plateNumberPartInputEt.getText().toString();

                                        if (inputLP.equals("") || inputNP.equals("")) {
                                            success = false;
                                            Snackbar.make(view, R.string.snackbar_empty_field, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            try {
                                                int NP_as_int = Integer.parseInt(inputNP);
                                                plate.setNumberPart(NP_as_int);
                                                plate.setLetterPart(inputLP);
                                            } catch (NumberFormatException e) {
                                                success = false;
                                                Snackbar.make(view, R.string.snackbar_not_int, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        if (success) {
                                            plate.setDatetime(currentDateMS);
                                            dbHandler.addNewPlate(plate);

                                            // Refresh listView
                                            dbCursor = dbHandler.readAllPlates();
                                            itemsAdapter.changeCursor(dbCursor);

                                            Snackbar.make(view, R.string.snackbar_add, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                })
					.setNegativeButton(R.string.dlg_btn_cancel,
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

    public class CustomCursorAdapter extends CursorAdapter {
        public CustomCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.listview_row, viewGroup, false);
        }

        @Override
        public void bindView (View view, Context context, Cursor cursor){
            TextView dateText = (TextView) view.findViewById(R.id.listViewDateText);
            TextView plateText = (TextView) view.findViewById(R.id.listViewPlateText);
            // Extract properties from cursor
            long dateMS = cursor.getLong(cursor.getColumnIndexOrThrow("datetime"));
            String letterPart = cursor.getString(cursor.getColumnIndexOrThrow("letterpart"));
            int numberPart = cursor.getInt(cursor.getColumnIndexOrThrow("numberpart"));
            // Populate fields with extracted properties
            DateFormat simpleFormat = new SimpleDateFormat("d.M.yyyy HH:mm");
            Date readableDate = new Date(dateMS);
            String plate = letterPart + DASH + String.valueOf(numberPart);

            dateText.setText(simpleFormat.format(readableDate));
            plateText.setText(plate);
        }
    }

    private double calculatePercent(double spottedPlates) {
        final double total = 999;
        return (spottedPlates / total) * 100;
    }

    public void deleteDB() {
        // Alert Dialog stuff
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_deletedb, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final CustomCursorAdapter itemsAdapter =
                new CustomCursorAdapter(this, dbCursor);

        //ListView listView = (ListView) findViewById(R.id.listView);
        //listView.setAdapter(itemsAdapter);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.deleteAll();
                                //TODO Update screen too
                                itemsAdapter.changeCursor(dbCursor);

                                //Snackbar.make(view, R.string.snackbar_add, Snackbar.LENGTH_LONG)
                                //        .setAction("Action", null).show();
                            }
                        })
                .setNegativeButton(R.string.dlg_btn_no,
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
        switch (id) {
            case R.id.action_deletedb:
                deleteDB();
                return true;
            case R.id.action_settings:
                //showSettings();
                return true;
            case R.id.action_about:
                //showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
