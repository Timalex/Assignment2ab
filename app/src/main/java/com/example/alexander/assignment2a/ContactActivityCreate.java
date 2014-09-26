package com.example.alexander.assignment2a;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;



public class ContactActivityCreate extends Activity {

    EditText viewName;
    EditText viewAge;
    EditText viewImageUrl;
    EditText viewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        viewName = (EditText) findViewById(R.id.editTextName);
        viewAge = (EditText) findViewById(R.id.editTextAge);
        viewImageUrl = (EditText) findViewById(R.id.editTextImageUrl);
        viewDescription = (EditText) findViewById(R.id.editTextDescription);

        // Hämta kontakt som skickats med
        Contact existingContact = getIntent().getParcelableExtra(Contact.EXTRA_PARCELABLE);

        // Fyll textvyer om en kontakt tagits emot
        if (existingContact != null)
        {
            setTextViews(existingContact);
        }
    }

    // Fyll alla vyer med text baserat på en kontakt
    private void setTextViews(Contact contact)
    {
        viewName.setText(contact.getName());
        viewAge.setText(String.valueOf(contact.getAge()));
        viewImageUrl.setText(contact.getImageUrl());
        viewDescription.setText(contact.getDescription());
    }



    // Fyll menyn med en "knapp" att bekräfta med när användaren är färdig
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Om användaren fyllt i allt korrekt så används resultatet
        if (id == R.id.action_contact_save)
        {
            saveIfDone();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getViewString(TextView view)
    {
        return view.getText().toString().trim();
    }

    public void saveIfDone()
    {
        // Hämtar trimmade textsträngar från vyerna med en underlättande metod
        String name = getViewString(viewName);
        String age = getViewString(viewAge);
        String imageUrl = getViewString(viewImageUrl);
        String description = getViewString(viewDescription);

        // Metod namnen är självförklarande om inget annat står
        if(name.isEmpty())
        {
            viewName.setError(getString(R.string.error_name));
        }
        else if(age.isEmpty())
        {
            viewAge.setError(getString(R.string.error_age));
        }
        // Kolla om bildadressen matchar mönstret för en korrekt formaterad webbadress
        else if(!(Patterns.WEB_URL.matcher(imageUrl).matches()))
        {
            // Om bildadressen är ogiltigt formaterad, informera användaren.
            viewImageUrl.setError(getString(R.string.error_url));
            // Det är okej att lämna fältet tomt, då kommer en standardbild användas istället
        }
        else if(description.isEmpty())
        {
            viewDescription.setError(getString(R.string.error_description));
        }
        else
        {
//            Omvandla nummertexten till ett riktigt heltal
            int ageNumber = Integer.parseInt(age);

            // Skicka tillbaka kontakten med intentet när aktiviteten avslutas
            Intent returnIntent = getIntent();
            returnIntent.putExtra(Contact.EXTRA_PARCELABLE, new Contact(name, ageNumber, imageUrl, description));
            setResult(RESULT_OK, returnIntent);
            finish();
        }



    }
}
