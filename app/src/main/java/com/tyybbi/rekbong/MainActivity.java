package com.tyybbi.rekbong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import static com.tyybbi.rekbong.Helpers.calculatePercent;
import static com.tyybbi.rekbong.Helpers.convertDateToLong;
import static com.tyybbi.rekbong.Helpers.convertDateToStr;
import static com.tyybbi.rekbong.Helpers.getVersionName;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RekDebug";
    public static final String APP_PREFS = "RBPrefs";
    public static final String PREF_SORT = "reverse";
    public static final String PREF_HIDE_LP = "hideLetterPart";
    public static final String PREF_HIDE_D = "hideDateTime";
    private static final String DASH = "-";
    private static final String SPACE = " ";
    SharedPreferences prefs;
    DBHandler dbHandler;
    CustomCursorAdapter itemsAdapter;
    Cursor dbCursor;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences(APP_PREFS, MODE_PRIVATE);

        // TODO listView refreshing after setting change
        dbHandler = new DBHandler(this);
        dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));

        itemsAdapter = new CustomCursorAdapter(this, dbCursor);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

                final Plate plate;

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_edit_plate, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder.setView(promptsView);

                final EditText plateEditLPEt = promptsView
                        .findViewById(R.id.editPlateLetterPartEt);
                final EditText plateEditNPEt = promptsView
                        .findViewById(R.id.editPlateNumberPartEt);
                final EditText plateEditDEt = promptsView
                        .findViewById(R.id.editPlateDateEt);

                plate = dbHandler.getPlate(id);
                plateEditLPEt.setText(plate.getLetterPart());
                plateEditNPEt.setText(String.valueOf(plate.getNumberPart()));
                plateEditDEt.setText(convertDateToStr(plate.getDatetime()));

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_btn_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        boolean success = true;

                                        String inputLP = plateEditLPEt.getText().toString();
                                        String inputNP = plateEditNPEt.getText().toString();
                                        String inputD = plateEditDEt.getText().toString();

                                        if (inputNP.equals("")) {
                                            success = false;
                                            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_empty_field, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            try {
                                                int NP_as_int = Integer.parseInt(inputNP);
                                                plate.setNumberPart(NP_as_int);
                                                plate.setLetterPart(inputLP);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                success = false;
                                                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_not_int, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        if (plate.getDatetime() != convertDateToLong(inputD) &&
                                                (convertDateToLong(inputD) != 0)) {
                                            plate.setDatetime(convertDateToLong(inputD));
                                        } else {
                                            plate.setDatetime(plate.getDatetime());
                                        }

                                        if (success) {
                                            plate.setId(plate.getId());
                                            dbHandler.updatePlate(plate);

                                            // Refresh listView
                                            dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));
                                            itemsAdapter.changeCursor(dbCursor);

                                            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_edit, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                })
                        .setNeutralButton(R.string.dlg_btn_delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dbHandler.deletePlate(plate);
                                        dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));
                                        itemsAdapter.changeCursor(dbCursor);

                                        Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_delete, Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                })
                        .setNegativeButton(R.string.dlg_btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Plate plate = new Plate();

                LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.dialog_addplate, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				alertDialogBuilder.setView(promptsView);

				final EditText plateLetterPartInputEt = promptsView
						.findViewById(R.id.addPlateLetterPartEt);

				final EditText plateNumberPartInputEt = promptsView
                       .findViewById(R.id.addPlateNumberPartEt);

				alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_btn_save,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        boolean success = true;
                                        Date date = new Date();

                                        // Get plate parts and store them
                                        String inputLP = plateLetterPartInputEt.getText().toString();
                                        String inputNP = plateNumberPartInputEt.getText().toString();

                                        // Allow empty letterPart since only the numbers matter
                                        if (inputNP.equals("")) {
                                            success = false;
                                            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_empty_field, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            try {
                                                int NP_as_int = Integer.parseInt(inputNP);
                                                plate.setNumberPart(NP_as_int);
                                                plate.setLetterPart(inputLP);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                success = false;
                                                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_not_int, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        if (success) {
                                            // Store datetime
                                            plate.setDatetime(date.getTime());
                                            dbHandler.addNewPlate(plate);

                                            // Refresh listView
                                            dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));
                                            itemsAdapter.changeCursor(dbCursor);

                                            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_add, Snackbar.LENGTH_LONG)
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

				AlertDialog alertDialog = alertDialogBuilder.create();

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
            TextView dateText = view.findViewById(R.id.listViewDateText);
            TextView plateText = view.findViewById(R.id.listViewPlateText);
            // Extract properties from cursor
            long dateMS = cursor.getLong(cursor.getColumnIndexOrThrow("datetime"));
            String letterPart = cursor.getString(cursor.getColumnIndexOrThrow("letterpart"));
            int numberPart = cursor.getInt(cursor.getColumnIndexOrThrow("numberpart"));

            // Populate fields with extracted properties
            if (prefs.getBoolean(PREF_HIDE_D, false)) {
                dateText.setVisibility(View.INVISIBLE);
            } else {
                dateText.setVisibility(View.VISIBLE);
            }
            dateText.setText(convertDateToStr(dateMS));
            if (prefs.getBoolean(PREF_HIDE_LP, false)) {
                String plate = String.valueOf(numberPart);
                plateText.setText(plate);
            } else {
                String plate = letterPart + DASH + String.valueOf(numberPart);
                plateText.setText(plate);
            }
        }
    }

    public void showSettings() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_settings, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final Switch sortCb = promptsView.findViewById(R.id.settingsDlgSortSb);
        final Switch hideLP = promptsView.findViewById(R.id.settingsDlgHideLSb);
        final Switch hideD = promptsView.findViewById(R.id.settingsDlgHideDSb);

        sortCb.setChecked(prefs.getBoolean(PREF_SORT, false));
        sortCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean(PREF_SORT, isChecked).apply();
                } else {
                    prefs.edit().putBoolean(PREF_SORT, isChecked).apply();
                }
            }
        });

        hideLP.setChecked(prefs.getBoolean(PREF_HIDE_LP, false));
        hideLP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean(PREF_HIDE_LP, isChecked).apply();
                } else {
                    prefs.edit().putBoolean(PREF_HIDE_LP, isChecked).apply();
                }
            }
        });

        hideD.setChecked(prefs.getBoolean(PREF_HIDE_D, false));
        hideD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean(PREF_HIDE_D, isChecked).apply();
                } else {
                    prefs.edit().putBoolean(PREF_HIDE_D, isChecked).apply();
                }
            }
        });

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));
                                itemsAdapter.changeCursor(dbCursor);
                                itemsAdapter.notifyDataSetChanged();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showAbout() {
        ListView listView = findViewById(R.id.listView);
        String spotPercent =
                String.format(Locale.getDefault(), "%.1f",
                        calculatePercent(listView.getAdapter().getCount()));
        String progressText = spotPercent + SPACE + getString(R.string.about_dlg_progress2);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_about, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final TextView progressTv = promptsView
                .findViewById(R.id.aboutDlgPercentValTv);

        final TextView versionTv = promptsView
                .findViewById(R.id.aboutDlgVersionValTv);

        final TextView gitTv = promptsView
                .findViewById(R.id.aboutDlgGitTv);

        progressTv.setText(progressText);
        versionTv.setText(getVersionName(context));
        gitTv.setText(R.string.about_dlg_git);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void deleteDB() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_deletedb, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.deleteAll();
                                //TODO Update screen too
                                dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_SORT, false));
                                itemsAdapter.changeCursor(dbCursor);
                                itemsAdapter.notifyDataSetChanged();

                                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_delete_all, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
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
                showSettings();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
