package com.example.mycontactlistchp8;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mycontacts.db";
    private static final int DATABASE_VERSION = 2;

    //Database creation SQL statement
    private static final String CREATE_TABLE_CONTACT =
            "create table contact(_id integer primary key autoincrement,"
                    + "contactname text not null, streetaddress text, "
                    + "city text, state text, zipcode text,"
                    + "phonenumber text, cellnumber text, "
                    +"email text, birthday text, contactphoto blob);";

    //Page 151 told us to add contactphoto blob
    //Blob means "Binary large object"
    //Data type blob is used for picture, audio, and video objects

    public ContactDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //It previously was programmed to delete a contacts database and replace it with a new one when the databse gets upgraded
        //In accordance to listing 8.15 it was modified so that the database structure can handle change without losing all user data
       // Log.w(ContactDBHelper.class.getName(),
         //       "Upgrading database from version " + oldVersion + " to "
           //             + newVersion + ", which will destroy all old data");
        //db.execSQL("DROP TABLE IF EXISTS contact");
        //onCreate(db);
        try{
            db.execSQL("ALTER TABLE contact ADD COLUMN contactphoto blob");
        }
        catch (Exception e){
            //do nothing
        }
    }
}


