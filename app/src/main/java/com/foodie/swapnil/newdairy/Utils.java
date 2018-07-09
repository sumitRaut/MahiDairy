package com.foodie.swapnil.newdairy;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sumit on 6/14/2018.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}