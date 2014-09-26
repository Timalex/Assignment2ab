package com.example.alexander.assignment2a;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 2014-09-16.
 */
public class Contact implements Parcelable
{
    private String name;
    private int age;
    private String imageUrl;
    private String description;

    // Konstanter för att hålla reda på samma extra som skickas med och packas upp
    public static final String EXTRA_INDEX = "contactIndex";
    public static final String EXTRA_PARCELABLE = "contact";

    // Sätter igång att återskapa ett objekt som skickats som en extra
    public static final Parcelable.Creator<Contact> CREATOR
            = new Parcelable.Creator<Contact>()
    {
        public Contact createFromParcel(Parcel in)
        {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size)
        {
            return new Contact[size];
        }
    };
            

    // Normal konstruktor för att skapa nya kontaktobjekt
    public Contact(String name, int age, String imageUrl, String description) {
        this.name = name;
        this.age = age;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Återskapar en kontakt från ett parcelobjekt
    public Contact(Parcel in)
    {
        name = in.readString();
        age = in.readInt();
        imageUrl = in.readString();
        description = in.readString();
    }


    // Spara objektet som ett parcelobjekt
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeString(imageUrl);
        dest.writeString(description);
    }

    // Textrepresentation av objektet
    @Override
    public String toString()
    {
        return name;
    }

    // Getters som ger kontaktens uppgifter
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Metod som krävs för att implementera Kontaktklassen som parcel
    @Override
    public int describeContents()
    {
        return 0;
    }


}
