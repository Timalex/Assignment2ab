package com.example.alexander.assignment2a;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Alexander on 2014-09-23.
 */
public class ContactDatabaseHelper extends SQLiteOpenHelper implements BaseColumns
{
    SQLiteDatabase database;

    private static final String DATABASE_NAME = "contact_database.db";
    private static final int DATABASE_VERSION = 1;

    // Konstanter för namn i databasen
    public static final String TABLE_NAME = "contact";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_AGE = "age";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_IMAGE_URL = "image_url";

    // Lättare att få med alla komma tecken i SQL-frågor med hjälp av en konstant
    public static final String COMMA_SEP = ", ";
    // Lättare att byta ut datatypen för texter i databasen med denna konstant
    public static final String TEXT_TYPE = " TEXT";

    private static final String SQL_CREATE_TABLE_CONTACTS =
            // SQL-kod som kan skapa kontakttabellen
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_AGE + " INTEGER" + COMMA_SEP +
                    COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_IMAGE_URL + TEXT_TYPE + " )";

    private static final String SQL_DELETE_TABLE_CONTACTS =
            // SQL-kod som kan ta bort kontaktabellen om den finns
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private String[] columnList = {
            // Lista med efterfrågade kolumner
            _ID,
            COLUMN_NAME_NAME,
            COLUMN_NAME_AGE,
            COLUMN_NAME_DESCRIPTION,
            COLUMN_NAME_IMAGE_URL
    };


    // Konstruktor
    public ContactDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            // Skapa kontakttabellen
            db.execSQL(SQL_CREATE_TABLE_CONTACTS);
            Log.d(getClass().getSimpleName(), "Created table " + TABLE_NAME + " version " + DATABASE_VERSION);
        }
        catch (Exception exception)
        {
            // Logga klassnamnet med undantagsmeddelandet
            Log.d(getClass().getSimpleName(), exception.getMessage());
        }
    }

    // När databasen ska uppgraderas
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            // Ta bort tabellen och skapa den igen
            db.execSQL(SQL_DELETE_TABLE_CONTACTS);
            onCreate(db);
        }
        catch (Exception exception)
        {
            Log.d(getClass().getSimpleName(), exception.getMessage());
        }
    }

    // Hämta ut resultatet från databasen representerat som en Cursor, som pekar på en svarsrad i taget
    public Cursor getCursor()
    {
        database = getReadableDatabase();

        return database.query(
                TABLE_NAME,
                columnList,
                null,
                null,
                null,
                null,
                null
        );
    }



    // Få et svars resultat ordnat efter valfri kolumn
    public Cursor getCursorOrderedBy(String columnName)
    {
        database = getReadableDatabase();

        return database.query(
                TABLE_NAME,
                columnList,
                null,
                null,
                null,
                null,
                columnName
        );
    }

    // Skicka in en kontakt, få ut motsvarande ContentValues
    private ContentValues getContactValues(Contact contact)
    {
        ContentValues contactValues = new ContentValues();
        contactValues.put(COLUMN_NAME_NAME, contact.getName());
        contactValues.put(COLUMN_NAME_AGE, contact.getAge());
        contactValues.put(COLUMN_NAME_DESCRIPTION, contact.getDescription());
        contactValues.put(COLUMN_NAME_IMAGE_URL, contact.getImageUrl());

        return contactValues;
    }

    // Stoppa in kontaktens datavärden i databasen
    public void insertContact(Contact contact)
    {
        database = getWritableDatabase();

        // Sätt in en ny rad i databastabellen
        database.insert(TABLE_NAME, null, getContactValues(contact));
    }

    // Ersätt en kontakt i databasen med nya värden
    public void updateContact(Contact contact, int rowToReplace)
    {
       database = getWritableDatabase();

        String[] whereArgs = {String.valueOf(rowToReplace)};
        database.update( TABLE_NAME, getContactValues(contact), _ID + "=?", whereArgs);
    }

    // Hämta ut en kontakt i databasen, beroende på efterfrågad rad, som ett objekt beroende
    public Contact getContact(int row)
    {
        database = getReadableDatabase();

        Cursor cursor = database.query(
                TABLE_NAME, // Efterfrågad tabell
                columnList, // Med dessa kolumner
                _ID + "=?", // Där id:et är lika med...
                new String[]{String.valueOf(row)}, // inskickat radnummer
                null,
                null,
                null
        );

        // Gå till första och enda raden i resultatet
        if (cursor.moveToFirst())
        {
            // Hämta ut värden utifrån respektive kolumns nummer utifrån dess namn
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_NAME));
            int age = cursor.getInt(
                    cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_AGE));
            String imageUrl = cursor.getString(
                    cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_IMAGE_URL));
            String description = cursor.getString(
                    cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_DESCRIPTION));

            // Självförklarande?
            return new Contact(name, age, imageUrl, description);
        }

        // Måste returnera null annars
        return null;
    }


    // Ta bort kontakter vars id:n skickas in
    public void deleteContacts(int rowIds[])
    {
        // Måste naturligtvis kunna skriva till databasen
        database = getWritableDatabase();

        // Ta bort en rad för varje inskicat id
        for (int i = 0; i < rowIds.length; i++)
        {
            database.delete(TABLE_NAME, _ID + "="+String.valueOf(rowIds[i]),null);
        }

    }


}
