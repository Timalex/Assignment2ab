package com.example.alexander.assignment2a;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Alexander on 2014-09-23.
 */
public class ContactCursorAdapter extends CursorAdapter
{
    LayoutInflater inflater;
    ViewHolder viewHolder;
    ContactDatabaseHelper databaseHelper;

    public ContactCursorAdapter(Context context, Cursor cursor, boolean autoRequery)
    {
        super(context, cursor, autoRequery);
        // Enkelt sätt att hämta en inflater
        inflater = LayoutInflater.from(context);
        databaseHelper = new ContactDatabaseHelper(context);
    }

    // Anropas när en ny vy behöver skapas
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // Håller kontaktvyn så att den kan återanvändas
        viewHolder = new ViewHolder();

        View view = inflater.inflate(R.layout.row_contact, parent, false);
        viewHolder.name = (TextView) view.findViewById(R.id.viewRowName);
        viewHolder.image = (ImageView) view.findViewById(R.id.viewRowImage);

        // Låt kontaktlayouten hålla reda på sin viewHolder
        view.setTag(viewHolder);

        return view;
    }

    // Anropas när data behöver knytas till en vy
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // Hämta viewholdern som kontaktvyn höll reda på
        viewHolder = (ViewHolder) view.getTag();

        // Hämta namn och bildadress för vyn
        String contactName = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_NAME));
        String contactImageUrl = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME_IMAGE_URL));

        // Fyll i kontakten namn i vyn
        viewHolder.name.setText(contactName);
        // Ladda asynkront in en bild i kontaktens bildvy från Internet, ersätt annars med en lokal bild
        Picasso.with(context).load(contactImageUrl).placeholder(R.drawable.ic_action_user).into(viewHolder.image);
    }

    public static class ViewHolder{
        public TextView name;
        public ImageView image;
    }
}
