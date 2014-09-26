package com.example.alexander.assignment2a;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ContactActivityMain extends Activity implements ContactFragmentList.OnContactSelectedListener {

    // Sant om layouten har två huvudkolumner
    boolean dualPanes;
    // Håller reda på kontaktens ordningsnummer
    private int contactIndex;
    // Vald kontakt
    private Contact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_main);
        // Bestäm om två kolumner är synliga
        dualPanes = isDetailViewVisible();
    }

    private boolean isDetailViewVisible()
    {
        // Försök hämta detaljvyn från aktiviteten
        View detailsFrame = this.findViewById(R.id.contact_details);

        // Sant om detaljvyn finns och syns
        return detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    public void showDetails(){


        if (dualPanes)
        {
            // Försök hämta fragmentet som innehåller kontaktdetaljer
            ContactFragmentDetails details = (ContactFragmentDetails)
                    getFragmentManager().findFragmentById(R.id.contact_details);


            // Om det inte finns eller gäller för en annan kontakt i listan...
            if (details == null || details.getShownIndex() != contactIndex)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Contact.EXTRA_PARCELABLE,selectedContact);
                bundle.putInt(Contact.EXTRA_INDEX,contactIndex);
                //Skapa ett nytt fragment för kontaktdetaljer
                details = new ContactFragmentDetails();
                details.setArguments(bundle);

                // Ersätt högra kolumnvyn med en detaljvy
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.contact_details, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
            }
        }
        else
        {
            /*
             Om detaljvyn inte finns och syns så startas istället
             en aktivitet för att visa detaljvyn.
             Skicka också med nummret för den valda kontakten
              */
            Intent intent = new Intent();
            intent.setClass(this, ContactActivityDetails.class);
            intent.putExtra(Contact.EXTRA_INDEX, this.contactIndex);
            intent.putExtra(Contact.EXTRA_PARCELABLE, selectedContact);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onContactSelected(Contact selected, int rowId)
    {
        // Spara den klickade kontaktens index i en klassvariabel
        contactIndex = rowId;
        selectedContact = selected;

        // Visa detaljvyn när en rad i listan klickas
        showDetails();
    }


}
