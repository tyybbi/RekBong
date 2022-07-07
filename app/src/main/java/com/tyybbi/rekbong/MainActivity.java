package com.tyybbi.rekbong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.tyybbi.rekbong.Helpers.calculatePercent;
import static com.tyybbi.rekbong.Helpers.convertDateToLong;
import static com.tyybbi.rekbong.Helpers.convertDateToStr;
import static com.tyybbi.rekbong.Helpers.getNextPlateNumber;
import static com.tyybbi.rekbong.Helpers.getVersionName;
import static com.tyybbi.rekbong.Helpers.getSpottingTime;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RekDebug";
    public static final String SPACE = " ";
    private static final String APP_PREFS = "RBPrefs";
    private static final String PREF_REVERSE = "reverse";
    private static final String PREF_HIDE_LP = "hideLetterPart";
    private static final String PREF_HIDE_D = "hideDateTime";
    private static final String PREF_QAM = "quickAddMode";
    private SharedPreferences prefs;
    private DBHandler dbHandler;
    private CustomCursorAdapter itemsAdapter;
    private Cursor dbCursor;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences(APP_PREFS, MODE_PRIVATE);

        dbHandler = new DBHandler(this);
        dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_REVERSE, false));

        itemsAdapter = new CustomCursorAdapter(this, dbCursor);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final Plate plate;

                LayoutInflater li = LayoutInflater.from(context);
                @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_edit_plate, null);

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
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.snackbar_empty_field, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            try {
                                                int NP_as_int = Integer.parseInt(inputNP);
                                                if (NP_as_int != 0) {
                                                    plate.setNumberPart(NP_as_int);
                                                    plate.setLetterPart(inputLP);
                                                } else {
                                                    success = false;
                                                    Snackbar.make(findViewById(android.R.id.content),
                                                            R.string.snackbar_zero, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                success = false;
                                                Snackbar.make(findViewById(android.R.id.content),
                                                        R.string.snackbar_invalid_number, Snackbar.LENGTH_LONG)
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
                                            dbCursor = dbHandler.getAllPlates
                                                    (prefs.getBoolean(PREF_REVERSE, false));
                                            itemsAdapter.changeCursor(dbCursor);

                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.snackbar_edit, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }
                                })
                        .setNeutralButton(R.string.dlg_btn_delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dbHandler.deletePlate(plate);
                                        dbCursor = dbHandler.getAllPlates
                                                (prefs.getBoolean(PREF_REVERSE, false));
                                        itemsAdapter.changeCursor(dbCursor);

                                        Snackbar.make(findViewById(android.R.id.content),
                                                R.string.snackbar_delete, Snackbar.LENGTH_LONG)
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
                final Date date = new Date();

                // Guess next plate number to be spotted
                ArrayList<Integer> plateNumberParts = dbHandler.getAllNumberParts();
                int nextPlateNumber = getNextPlateNumber(
                        plateNumberParts, prefs.getBoolean(PREF_REVERSE, false));

                if (prefs.getBoolean(PREF_QAM, false)) {
                    if ((nextPlateNumber > 0) && (nextPlateNumber < 1000)) {
                        plate.setLetterPart("");
                        plate.setNumberPart(nextPlateNumber);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.snackbar_qam_abort, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }
                    plate.setDatetime(date.getTime());
                    dbHandler.addNewPlate(plate);
                    dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_REVERSE, false));
                    itemsAdapter.changeCursor(dbCursor);

                    return;
                }

                LayoutInflater li = LayoutInflater.from(context);
                @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_add_plate, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				alertDialogBuilder.setView(promptsView);

				final EditText plateLetterPartInputEt = promptsView
						.findViewById(R.id.addPlateLetterPartEt);

                final EditText plateNumberPartInputEt = promptsView
                       .findViewById(R.id.addPlateNumberPartEt);

                if ((nextPlateNumber > 0) && (nextPlateNumber < 1000)) {
                    plateNumberPartInputEt.setText(String.valueOf(nextPlateNumber));
                }

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.dlg_btn_save,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        boolean success = true;

                                        // Get plate parts and store them
                                        String inputLP = plateLetterPartInputEt.getText().toString();
                                        String inputNP = plateNumberPartInputEt.getText().toString();

                                        // Allow empty letterPart since only the numbers matter
                                        if (inputNP.equals("")) {
                                            success = false;
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.snackbar_empty_field, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            try {
                                                int NP_as_int = Integer.parseInt(inputNP);
                                                if (NP_as_int != 0) {
                                                    plate.setNumberPart(NP_as_int);
                                                    plate.setLetterPart(inputLP);
                                                } else {
                                                    success = false;
                                                    Snackbar.make(findViewById(android.R.id.content),
                                                            R.string.snackbar_zero, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                success = false;
                                                Snackbar.make(findViewById(android.R.id.content),
                                                        R.string.snackbar_invalid_number, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        if (success) {
                                            // Store datetime
                                            plate.setDatetime(date.getTime());
                                            dbHandler.addNewPlate(plate);

                                            // Refresh listView
                                            dbCursor = dbHandler.getAllPlates(prefs
                                                    .getBoolean(PREF_REVERSE, false));
                                            itemsAdapter.changeCursor(dbCursor);

                                            Snackbar.make(findViewById(android.R.id.content),
                                                    R.string.snackbar_add, Snackbar.LENGTH_LONG)
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

    class CustomCursorAdapter extends CursorAdapter {
        CustomCursorAdapter(Context context, Cursor cursor) {
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

            boolean showDash = true;
            if (letterPart.equals("")) { showDash = false; }

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
                if (!showDash) {
                    String plate = String.valueOf(numberPart);
                    plateText.setText(plate);
                } else {
                    String plate = letterPart + "-" + numberPart;
                    plateText.setText(plate);
                }
            }
        }
    }

    private void showSettings() {
        LayoutInflater li = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_settings, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final Switch reverseSb = promptsView.findViewById(R.id.settingsDlgReverseSb);
        final Switch hideLPSb = promptsView.findViewById(R.id.settingsDlgHideLSb);
        final Switch hideDSb = promptsView.findViewById(R.id.settingsDlgHideDSb);
        final Switch qamSb = promptsView.findViewById(R.id.settingsDlgQamSb);

        reverseSb.setChecked(prefs.getBoolean(PREF_REVERSE, false));
        reverseSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_REVERSE, isChecked).apply();
            }
        });

        hideLPSb.setChecked(prefs.getBoolean(PREF_HIDE_LP, false));
        hideLPSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_HIDE_LP, isChecked).apply();
            }
        });

        hideDSb.setChecked(prefs.getBoolean(PREF_HIDE_D, false));
        hideDSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_HIDE_D, isChecked).apply();
            }
        });

        qamSb.setChecked(prefs.getBoolean(PREF_QAM, false));
        qamSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(PREF_QAM, isChecked).apply();
            }
        });

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_REVERSE, false));
                                itemsAdapter.changeCursor(dbCursor);
                                itemsAdapter.notifyDataSetChanged();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showAbout() {
        ListView listView = findViewById(R.id.listView);

        String progressText;
        ArrayList<Long> plateDates = dbHandler.getAllDates();
        if (plateDates.size() != 0) {
            Collections.sort(plateDates);

            long earliestAdd = Collections.min(plateDates);
            long latestAdd = Collections.max(plateDates);
            String spottingTime = getSpottingTime(context, latestAdd, earliestAdd);
            String spotPercent = String.format(Locale.getDefault(), "%.1f",
                    calculatePercent(listView.getAdapter().getCount()));
            progressText = spotPercent + SPACE + getString(R.string.about_dlg_progress2)
                    + SPACE + spottingTime;
        } else {
            progressText = "0" + SPACE + getString(R.string.about_dlg_progress3);
        }
        LayoutInflater li = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_about, null);

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

    private void exportDB(final Context context) {
        File sd_dl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File data = Environment.getDataDirectory();
        String packageName = context.getApplicationInfo().packageName;

        if (sd_dl.canWrite()) {
            String currentDBPath = String.format("//data//%s//databases//%s",
                    packageName, "Plates.db");
            String backupDBPath = String.format("Plates.db");
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd_dl, backupDBPath);
            FileChannel src = null;
            FileChannel dst = null;
            try {
                if (currentDB.exists()) {
                    src = new FileInputStream(currentDB).getChannel();
                    dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (src != null) {
                    try {
                        src.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dst != null) {
                    try {
                        dst.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Snackbar.make(findViewById(android.R.id.content),
                            R.string.snackbar_export_success, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                            R.string.snackbar_export_failure, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void importDB(final Context context) {
        File sd_dl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File data = Environment.getDataDirectory();
        String packageName = context.getApplicationInfo().packageName;

        if (sd_dl.canWrite()) {
            String currentDBPath = String.format("//data//%s//databases//%s",
                    packageName, "Plates.db");
            String backupDBPath = String.format("Plates.db");
            File backupDB = new File(data, currentDBPath);
            File currentDB = new File(sd_dl, backupDBPath);
            FileChannel src = null;
            FileChannel dst = null;
            try {
                if (currentDB.exists()) {
                    src = new FileInputStream(currentDB).getChannel();
                    dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (src != null) {
                    try {
                        src.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dst != null) {
                    try {
                        dst.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Snackbar.make(findViewById(android.R.id.content),
                            R.string.snackbar_export_success, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                            R.string.snackbar_import_failure, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void deleteDB() {
        LayoutInflater li = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_delete_db, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dlg_btn_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.deleteAll();
                                dbCursor = dbHandler.getAllPlates(prefs.getBoolean(PREF_REVERSE, false));
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

        AlertDialog alertDialog = alertDialogBuilder.create();

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

        switch (id) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_export_db:
                exportDB(context);
                return true;
            case R.id.action_import_db:
                importDB(context);
                return true;
            case R.id.action_delete_db:
                deleteDB();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
