package com.example.mycontactlistchp8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class ContactSettingsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        initListButton();
        initMapButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibSettings.setEnabled(false);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("ContactListActivity(Chp6)Preferences",
                Context.MODE_PRIVATE).getString("sortField", "contactname");
        String sortOrder = getSharedPreferences("ContactListActivity(Chp6)Preferences",
                Context.MODE_PRIVATE).getString("sortOrder", "ASC");

        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
        RadioButton rbBirthDay = (RadioButton) findViewById(R.id.radioBirthday);
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        } else {
            rbBirthDay.setChecked(true);
        }
        RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
        RadioButton rbDescending = (RadioButton) findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        } else {
            rbAscending.setChecked(true);
        }

    }

    private void initSortByClick() {
        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
                RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
                if (rbName.isChecked()) {
                    getSharedPreferences("ContactListActivity(Chp6)Preferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "contactname").apply();

                } else if (rbCity.isChecked()) {
                    getSharedPreferences("ContactListActivity(Chp6)Preferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "city").apply();
                } else {
                    getSharedPreferences("ContactListActivity(Chp6)Preferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "birthday").apply();
                }

            }
        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = (RadioGroup) findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()) {
                    getSharedPreferences("ContactListActivity(Chp6)Preferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "ASC").apply();
                } else {
                    getSharedPreferences("ContactListActivity(Chp6)Preferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder", "DESC").apply();

                }
            }

        });

    }
}


