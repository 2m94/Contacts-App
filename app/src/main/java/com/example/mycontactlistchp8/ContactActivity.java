package com.example.mycontactlistchp8;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class ContactActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {


    private Contact currentContact;
    public final int PERMISSION_REQUEST_PHONE = 102;
    public final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initListButton();
        initMapButton();
        initSettingsButton();
        initToggleButton(); //So that it's not in editing mode when app opens
        initTextChangedEvents();
        initSaveButton();
        initChangeDateButton();
        initCallFunction();
        initImageButton();
        hideKeyboard();
        /*Page 107/108 of book instructs to add this code . It gets the ID passed to it
        and calls the initContact method to displat the contact
         */
        Bundle extras = getIntent().getExtras(); //This code corresponds to onItemClick in ContactListActivity
        if (extras != null) {
            initContact(extras.getInt("contactid"));
        } else {
            currentContact = new Contact();
        }
        setForEditing(false);

    }
    //Listing 8.6, a listener is added for phone number edit text for press and hold user action
    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() { //is it okay to use view?

            @Override
            public boolean onLongClick(View arg0) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });
        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener(){

        @Override
        public boolean onLongClick (View arg0){
            checkCallingOrSelfPermission(currentContact.getCellNumber());
            return false;
        }
    });
}

    //Method for checking phone permission
    private void checkPhonePermission(String phoneNumber) {
    if(Build.VERSION.SDK_INT >= 23) {
        if (ContextCompat.checkSelfPermission(ContactActivity.this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {

                Snackbar.make(findViewById(R.id.activity_contact),
                        "MyContactList requires this permission to place a call from the app",
                        Snackbar.LENGTH_INDEFINITE).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ActivityCompat.requestPermissions
                                (ContactActivity.this, new String[]

                                                {android.Manifest.permission.CALL_PHONE},
                                        PERMISSION_REQUEST_PHONE);
                    }
                })
                        .show();
            } else {
                ActivityCompat.requestPermissions(ContactActivity.this, new
                                String[]{android.Manifest.permission.CALL_PHONE},
                        PERMISSION_REQUEST_PHONE);

            }
        } else {
            callContact(phoneNumber);
        }
    }
            else{
                callContact(phoneNumber);
            }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactActivity.this, "You may now call from this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactActivity.this, "You will not be able to make calls" +
                            "from this app", Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }else{
                    Toast.makeText(ContactActivity.this, "You will not be able to save"+
                           "contact pictures from this app", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            startActivity(intent);
        }
    }
        private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList); //Variable to hold List Button is declared
        ibList.setOnClickListener(new View.OnClickListener() { //This listener allows the widget to respond to the user clicking it (the event)
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Intent flag alerts the system to not make multiple copies of the same activity
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactMapActivity.class);
                if(currentContact.getContactID() == -1){
                    Toast.makeText(getBaseContext(), "Contact must be saved before it can be mapped",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    intent.putExtra("contactid", currentContact.getContactID());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButton); //"final" prevents the variable assignment from changing
        editToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                setForEditing(editToggle.isChecked()); //Set to true if button is toggled for editing and false if button is not toggled for editing

            }
        });
    }

    private void setForEditing(boolean enabled) {
        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipcode = (EditText) findViewById(R.id.editZipcode);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        Button buttonChange = (Button) findViewById(R.id.btnBirthday);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipcode.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);

        if (enabled) {
            editPhone.setInputType(InputType.TYPE_CLASS_PHONE);  //We are enabling the EditText in order for it to respond to the long click event
            editCell.setInputType(InputType.TYPE_CLASS_PHONE);
            editName.requestFocus();
        }    //It will be null in viewing mode and to prevent editing in viewing mode. It should only accept numbers in editing mode.
        else {
            editPhone.setInputType(InputType.TYPE_NULL);
            editCell.setInputType(InputType.TYPE_NULL);

        }
    } //Now the app should be able to call contacts by pressing and holding a phone number

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                selectedTime.getTimeInMillis()).toString());
    }

    private void initChangeDateButton() {
        Button changeDate = (Button) findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }

        });

    }

    private void initTextChangedEvents() {
        final EditText etContactName = (EditText) findViewById(R.id.editName);
        etContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
            }
        });
        final EditText etStreetAddress = (EditText) findViewById(R.id.editAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());

            }
        });



        final EditText etCity = (EditText) findViewById(R.id.editCity);
        etCity.addTextChangedListener(new

                                              TextWatcher() {
                                                  @Override
                                                  public void beforeTextChanged (CharSequence s,int start, int count, int after){
                                                  }
                                                  @Override
                                                  public void onTextChanged (CharSequence s,int start, int before, int count){
                                                  }
                                                  @Override
                                                  public void afterTextChanged (Editable s){
                                                      currentContact.setCity(etCity.getText().toString());

                                                  }
                                              });
        final EditText etState = (EditText) findViewById(R.id.editState);
        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());

            }
        });


        final EditText etZipCode = (EditText) findViewById(R.id.editZipcode);
        etZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZipCode.getText().toString());

            }
        });
        final EditText etPhoneNumber = (EditText) findViewById(R.id.editHome);
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(etPhoneNumber.getText().toString());

            }
        });
        final EditText etCellNumber = (EditText) findViewById(R.id.editCell);
        etCellNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(etCellNumber.getText().toString());

            }
        });
        final EditText etEmail = (EditText) findViewById(R.id.editEmail);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setEmail(etEmail.getText().toString());

            }
        });

    }




    private void initSaveButton(){
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wasSuccessful = false;
                ContactDataSource ds = new ContactDataSource(ContactActivity.this);
                try{
                    ds.open();
                    if(currentContact.getContactID() == -1){
                        wasSuccessful = ds.insertContact(currentContact);
                    }
                    else{
                        wasSuccessful = ds.insertContact(currentContact);
                    }
                    ds.close();
                }

                catch(Exception e){
                    wasSuccessful = false;
                }

                if(wasSuccessful){
                    ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButton);
                    editToggle.toggle();
                    setForEditing(false);
                    //Need to double check page 91 of textbook.
                    int newId = ds.getLastContactId();
                    currentContact.setContactID(newId);
                }
            }
        });

    }
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = (EditText) findViewById(R.id.editName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);

    }
    /*This method populates the Contact Activity. It retrieves the contact and populates
    the layout with the values of the retrieved contact
     */
    private void initContact(int id){
        ContactDataSource ds = new ContactDataSource(ContactActivity.this);
        try{
            ds.open();
            currentContact = ds.getSpecificContacts(id);
            ds.close();
        }
        catch(Exception e){
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }
        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZipcode);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);
        if(currentContact.getPicture() != null){
            picture.setImageBitmap(currentContact.getPicture());
        }
        else{
            picture.setImageResource(R.drawable.photoicon);
        }

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editEmail.setText(currentContact.getEmail());
        editCell.setText(currentContact.getCellNumber());
        editName.setText(currentContact.getEmail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis()).toString());



    }
    //Listing 8.12 p.150, method for accessing the camera to get the returned picture
    public void takePhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }
    //Listing 8.11 method for initializing image button for onClick event
    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(ContactActivity.this,
                            android.Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale
                                (ContactActivity.this, android.Manifest.permission.CAMERA)) {
                            Snackbar.make(findViewById(R.id.activity_contact),
                                    "The app needs permission to take pictures.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions
                                            (ContactActivity.this, new String[]

                                                            {android.Manifest.permission.CAMERA},
                                                    PERMISSION_REQUEST_CAMERA);
                                }
                            })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(ContactActivity.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);

                    }
                } else {
                    takePhoto();
                }
            }
                else {
                takePhoto();
            }
        }
            });
        }




}






