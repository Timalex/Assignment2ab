package com.example.alexander.assignment2a;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Created by Alexander on 2014-09-16.
 */
public class ContactActivityDetails extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Avsluta den här aktiviteten om skärmen är i landskapsläge
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            finish();
            return;
        }

        if (savedInstanceState == null)
        {
            ContactFragmentDetails detailsFragment = new ContactFragmentDetails();
            // Skicka vidare extras i fragmentet från intentet som startade denna aktivitet
            detailsFragment.setArguments(getIntent().getExtras());
            //Lägg till ett nytt fragment
            getFragmentManager().beginTransaction().add(android.R.id.content, detailsFragment).commit();
        }
    }


}
