package com.example.alexander.assignment2a;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Alexander on 2014-09-16.
 */
public class ContactFragmentDetails extends Fragment {
    /**
     * Create a new instance of com.example.alexander.assignment2a.DetailsFragment, initialized to
     * show the text at 'index'.
     */

    // Kontakt som detta fragment arbetar mot
    Contact contact;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Hämta ut ett kontaktobjekt
        contact = getArguments().getParcelable(Contact.EXTRA_PARCELABLE);
    }

    public int getShownIndex()
    {
        return getArguments().getInt(Contact.EXTRA_INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

            // Blås upp en ny layout från XML
            View detailsLayout = inflater.inflate(R.layout.activity_contact_details,container,false);

            // Hitta vyerna från XML
            ImageView viewPortrait = (ImageView) detailsLayout.findViewById(R.id.viewPortrait);
            TextView viewName = (TextView) detailsLayout.findViewById(R.id.viewName);
            TextView viewAge = (TextView) detailsLayout.findViewById(R.id.viewAge);
            TextView viewDescription = (TextView) detailsLayout.findViewById(R.id.viewDescription);

            // Om en kontakt skickats in
            if(contact != null)
            {
                // Visa kontaktens bild från Internet i bildvyn, ersätt annars med en lokal bild
                Picasso.with(getActivity()).load(contact.getImageUrl()).placeholder(R.drawable.ic_action_user).into(viewPortrait);
                // Visa resten av kontaktens uppgifter i textvyerna
                viewName.setText(contact.getName());
                viewAge.setText(String.valueOf(contact.getAge()));
                viewDescription.setText(contact.getDescription());
            }

            return detailsLayout;
    }

}
