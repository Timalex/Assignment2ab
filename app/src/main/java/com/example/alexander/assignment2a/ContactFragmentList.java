package com.example.alexander.assignment2a;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Alexander on 2014-09-21.
 */
// Fragment för kontaktlistan
    public class ContactFragmentList extends ListFragment implements AbsListView.MultiChoiceModeListener
    {
        // Request koder så att aktivitens resultat tas om hand bra
        public static final int CREATE_CONTACT_REQUEST = 1;
        public static final int EDIT_CONTACT_REQUEST = 2;

        // Hjälper till att kommunicera mot databasen
        private ContactDatabaseHelper databaseHelper;

        // Action knapp för redigering
        private MenuItem menuItemEdit;

        // Ska innehålla indexnumret för vald kontakt
        private int contactIndex;

        // Kan anpassa kontaktlistan för visning i en vy
        public static ContactCursorAdapter contactAdapter;

        // Interface implementation från fragmentets aktivitet
        OnContactSelectedListener activityListener;



        // Definierar gränssnittet för en lyssnare som andra klasser kan implementera
        public interface OnContactSelectedListener
        {
            public void onContactSelected(Contact selectedContact, int contactPosition);
        }



        // När fragmentet kopplas till en aktivitet
        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);

            // Försök "casta" aktiviteten, vilket bör gå bra om den implementerar lyssnaren
            try { activityListener = (OnContactSelectedListener) activity; }
            // Kasta ett lämpligt undantag om försöket misslyckas
            catch (ClassCastException e)
            {
                throw new ClassCastException(activity.toString() + activity.getString(R.string.exception_implement_interface));
            }
        }

        // Efter att _aktiviteten_ har skapats
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);


            // Underlättar att arbeta med databasen
            databaseHelper = new ContactDatabaseHelper(getActivity());

            // Använd ContactAdapter för att visa kontakterna i listvyn
            contactAdapter = new ContactCursorAdapter(getActivity(), databaseHelper.getCursor(), false);
            setListAdapter(contactAdapter);

            // Lägg till kontakter om listan är tom
            populateContactList();

            // Ställer in så att listvyn kan hantera flera val
            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(this);

            // Det finns en meny som tillhör fragmentet
            setHasOptionsMenu(true);

            // Hämta det sparade platsnummret för en kontakt som valts tidigare
            if (savedInstanceState != null) {
                // Hämta ut sparad information för att återställa fragmentets tillstånd
                contactIndex = savedInstanceState.getInt(Contact.EXTRA_INDEX, 0);
            }
        }


        // Spara indexnumret för vald kontakt tills nästa gång aktiviteten skapas
        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            super.onSaveInstanceState(outState);
            outState.putInt(Contact.EXTRA_INDEX, contactIndex);
        }

        // När fragmentet fortsätter köras
        @Override
        public void onResume()
        {
            super.onResume();

            // Hämta en ny representation av databasen till adaptern
            contactAdapter.changeCursor(databaseHelper.getCursor());

            // Uppdatera listvyn med den nya datan
            contactAdapter.notifyDataSetChanged();
        }

        // När en kontakt i listan klickas
        @Override
        public void onListItemClick(ListView l, View v, int position, long id)
        {
            int rowId = (int) id;
            // Hämta den valda kontakten från databasen
            Contact selectedContact = databaseHelper.getContact(rowId);

            // Skicka vidare vald kontakt till aktivitetens lyssnare
            activityListener.onContactSelected(selectedContact, rowId);
        }

        // När menyn i actionbaren skapas
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.list_overview_actions, menu);
        }

        // När någonting väljs i menyn
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch (item.getItemId())
            {
                // Starta en annan aktivitet för att skapa en ny kontakt
                case R.id.action_create_contact:
                    Intent addContact = new Intent(getActivity(),ContactActivityCreate.class);
                    startActivityForResult(addContact, CREATE_CONTACT_REQUEST);
                    return true;
                // Sortera kontakterna i listan efter ålder
                case R.id.action_sort_contacts:
                    contactAdapter.changeCursor(databaseHelper.getCursorOrderedBy(ContactDatabaseHelper.COLUMN_NAME_AGE));
                    contactAdapter.notifyDataSetChanged();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        // När en kontakt markeras/avmarkeras i action mode
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
        {
            // Om fler än en kontakt är markerad, dölj redigeringsknappen
            if(getListView().getCheckedItemCount() > 1)
            {
                menuItemEdit.setVisible(false);
            }
            else
            {
                // ..visa den annars igen
                menuItemEdit.setVisible(true);
            }
        }


        // Contextual action mode listeners

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.action_mode_actions,menu);
            menuItemEdit = mode.getMenu().findItem(R.id.action_edit_contact);
            return true;
        }

        // När action mode förbereds
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        // När en action mode "knapp" väljs
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                // Radera kontakt, uppdatera adaptern
                case R.id.action_delete_contacts:
                    databaseHelper.deleteContacts(getSelectedIds());
                    contactAdapter.changeCursor(databaseHelper.getCursor());
                    contactAdapter.notifyDataSetChanged();
                    return true;
                // Starta en aktivitet för att ersätta en befintlig kontakt med en modifierad
                case R.id.action_edit_contact:
                    Intent editContact = new Intent(getActivity(), ContactActivityCreate.class);
                    int contactId = (int) getListView().getCheckedItemIds()[0];
                    // Hämta kontakten utifrån id:et från den enda markerade saken i listan
                    Contact contact = databaseHelper.getContact(contactId);
                    // Skicka med kontakten och dess id
                    editContact.putExtra(Contact.EXTRA_PARCELABLE, contact);
                    editContact.putExtra(Contact.EXTRA_INDEX, contactId);
                    startActivityForResult(editContact, EDIT_CONTACT_REQUEST);
                    return true;

                default:
                    return false;
            }
        }

        // När action mode försvinner
        @Override
        public void onDestroyActionMode(ActionMode mode)
        {

        }

        // End of contextual action mode


        // När en aktivitet returnerar ett resultat
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK)
            {
                // Hämta ut kontakten som kom tillbaka
                Contact receieved = data.getParcelableExtra(Contact.EXTRA_PARCELABLE);
                switch (requestCode)
                {
                    // Sätt in den nya kontakten i databasen
                    case CREATE_CONTACT_REQUEST:
                        databaseHelper.insertContact(receieved);
                        break;
                    // Använd id:et som kom tillbaka till att uppdatera databasen och välja att visa den nya kontakten
                    case EDIT_CONTACT_REQUEST:
                        int contactId = data.getIntExtra(Contact.EXTRA_INDEX,0);
                        databaseHelper.updateContact(receieved,contactId);
                        activityListener.onContactSelected(receieved,contactId);
                        break;
                }
            }
        }

        private int[] getSelectedIds()
        {
            // Hämta ut vilka listpositioner som är markerade
            SparseBooleanArray contactPositions = getListView().getCheckedItemPositions();

            // En dynamisk lista för alla id:n eftersom antalet _markerade_ positioner är okänt
            ArrayList<Long> contactIds = new ArrayList<Long>();
            for (int position = 0; position < contactAdapter.getCount(); position++)
            {
                // Spara id om positionen är markerad
                if (contactPositions.get(position))
                {
                    contactIds.add(contactAdapter.getItemId(position));
                }
            }

            // Konvertera värdena till integers i en vanlig array
            int selectedIds[] = new int[contactIds.size()];
            for (int i=0; i < selectedIds.length; i++)
            {
                selectedIds[i] = contactIds.get(i).intValue();
            }

            return selectedIds;
        }


        // Fyller på en tom databas för att underlätta testning
        private void populateContactList()
        {
            if (databaseHelper.getCursor().getCount() == 0)
            {
                databaseHelper.insertContact(new Contact("David Elbe", 45, "http://standout.se/img/david.elbe.jpg",
                        "David är VD men kallar sig hellre webbutvecklare. Han spelar gärna en runda golf eller drar fram en gammal akustisk gitarr framför lägerelden. Han är även en av personerna bakom podcasten Webbradion, en show för webbutvecklare."));
                databaseHelper.insertContact(new Contact("Alexander Sverla", 35, "http://standout.se/img/alexander.sverla.jpg",
                        "Alexander är vår .Net-expert på kontoret, men han är lika glad för det. Han har börjat att titta på Ruby on Rails, vilket han finner vara lite läskigt men spännande. Han uppskattar större projekt med fokus på användarna ute i verksamheten. På fritiden ägnar han sig åt sin familj, hitta goda whiskysorter och ännu mer tid framför datorn."));
                databaseHelper.insertContact(new Contact("Metin Ucar", 48, "http://standout.se/img/metin.ucar.jpg",
                        "Metin är vår grafiker som växte upp i trädgården av HTML & CSS samt aprikoser. Han brinner för 'simplicity' och väldigt svag för bra musik och film."));
                databaseHelper.insertContact(new Contact("Huseyin Ozturk", 33, "http://standout.se/img/huseyin.ozturk.jpg",
                        "Hüseyin är en back-end-utvecklare som förvandlar kaffe till webb- och mobilapplikationer. Före detta fotbollsspelare som älskar musik och att resa. "));
                databaseHelper.insertContact(new Contact("Albert Arvidsson", 50, "http://standout.se/img/albert.arvidsson.jpg",
                        "Programmerare och linuxnörd från Ljungby. Gillar systemkameror och joggning. Sysslar gärna med geocaching, en sport där man letar efter skattgömmor med gps."));
            }
        }
    }
