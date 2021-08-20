package com.example.mycontactlistchp8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    ArrayList<Contact> contacts;
    RecyclerView contactList;

    boolean isDeleting = false;
    ContactAdapter contactAdapter;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //gets a reference to the ViewHolder that produced the click using the tag setup in the adapter
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            //uses ViewHolder to get the position of the ViewHolder in the list
            int position = viewHolder.getAdapterPosition();
            //uses position t
            int contactID = contacts.get(position).getContactID();
            Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
            //passes the contactID to the MainActivity2
            intent.putExtra("contactID", contactID);
            startActivity(intent);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initAddContactButton();
        initDeleteSwitch();
        initListButton();
        initMapButton();
        initSettingsButton();

        BroadcastReceiver batteryReceiver = new BroadcastReceiver (){
            @Override
            public void onReceive(Context context, Intent intent){
                double batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                double levelScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int batteryPercent = (int) Math.floor(batteryLevel/levelScale * 100);
                TextView textBatteryState = (TextView) findViewById(R.id.textBatteryLevel);
                textBatteryState.setText(batteryPercent + "%");
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);


        String sortBy = getSharedPreferences("MyContactList(Chp7))Preferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactList(Chp7)Preferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");
        ContactDataSource ds = new ContactDataSource(this); //I kept this
        //ArrayList<String> names;

        //I NEED TO FIND OUT WHY THE WORD contacts KEEPS GETTING ERRORS!

        try{
            ds.open();
            contacts = ds.getContacts(sortBy, sortOrder);
            ds.close();
            contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);
            contactAdapter = new ContactAdapter(contacts, ContactListActivity.this);
            contactAdapter.setOnItemClickListener(onItemClickListener);
            contactList.setAdapter(contactAdapter);
        }
        catch(Exception e){
            Toast.makeText(this,"Error retrieving contacts", Toast.LENGTH_LONG).show();
        }

    }
    //Copy pasting navigation buttons
    private void initListButton() {
        ImageButton ibList = findViewById(R.id.imageButtonList);
        ibList.setEnabled(false);
    }
    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    // Method for initializing the click of an item on a list
    //Find out what needs change from listing 6.8
    /*private void initItemClick(){
        ListView listView = (ListView) findViewById(R.id.rvContacts);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                //RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                Contact selectedContact = contacts.get(position);
                if(isDeleting){
                    contactAdapter.showDelete(position, itemClicked, ContactListActivity.this, selectedContact);
                }
                else{
                    Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                    intent.putExtra("contactid", selectedContact.getContactID());
                    startActivity(intent);
                }
                Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                intent.putExtra("contactid", selectedContact.getContactID());
                startActivity(intent);

            }
        });
    }

     */
    private void initAddContactButton(){
        Button newContact = (Button) findViewById(R.id.buttonAddContact);
        newContact.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initDeleteSwitch(){
        Switch s = findViewById(R.id.switchDelete);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //reads if the switch is checked
                boolean status = compoundButton.isChecked();
                contactAdapter.setDelete(status);
                //tells the adapter to redraw the list.
                contactAdapter.notifyDataSetChanged();


            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        String sortBy = getSharedPreferences("MyContactList(Chp7)Preferences",
                Context.MODE_PRIVATE).getString("sortField", "contactname");

        String sortOrder = getSharedPreferences("MyContactList(Chp7)Preferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");
        ContactDataSource ds = new ContactDataSource(this);
        try {
            ds.open();
            contacts = ds.getContacts(sortBy, sortOrder);
            ds.close();
            if(contacts.size() > 0){
                contactList = findViewById(R.id.rvContacts);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                contactList.setLayoutManager(layoutManager);
                contactAdapter = new ContactAdapter(contacts, ContactListActivity.this);
                contactAdapter.setOnItemClickListener(onItemClickListener);
                contactList.setAdapter(contactAdapter);
            }
            else{
                Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                startActivity(intent);
            }

        }
        catch(Exception e){
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG);
        }


    }

}
