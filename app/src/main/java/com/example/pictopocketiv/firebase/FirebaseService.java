package com.example.pictopocketiv.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseService {

    // Firebase app instances
    public static FirebaseApp fbApp;
    public static FirebaseAuth fbAuth;

    // Firebase initialization
    public static void init(Context context) {
        fbApp = FirebaseApp.initializeApp(context);
        fbAuth = FirebaseAuth.getInstance();
    }
}
