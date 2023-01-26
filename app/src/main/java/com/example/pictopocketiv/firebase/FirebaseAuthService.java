package com.example.pictopocketiv.firebase;

public class FirebaseAuthService {

    /** Auth State enum **/
    public enum FirebaseAuthState {
        LOGGED_IN,
        LOGGED_OUT
    }

    /** Response Interface **/
    public interface OnOpenFirebaseSession {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnUserFirebaseCreation {
        void onCreationSuccess();
        void onCreationFailure(Exception e);
    }


    /** Auth state **/
    public static FirebaseAuthState getCurrentStatus() {
        if(FirebaseService.fbAuth.getCurrentUser() != null) {
            return FirebaseAuthState.LOGGED_IN;
        } else {
            return FirebaseAuthState.LOGGED_OUT;
        }
    }


    /** Session **/
    public static void openSession(String userMail, String userPass,
                                   OnOpenFirebaseSession onOpenSession ) {

        FirebaseService.fbAuth.signInWithEmailAndPassword(userMail,userPass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(onOpenSession != null)
                    onOpenSession.onSuccess();
            } else {
                if(onOpenSession != null)
                    onOpenSession.onFailure(task.getException());
            }
        });
    }

    public static void logout() {
        FirebaseService.fbAuth.signOut();
    }


    /** Signup **/
    public static void createaAccount(String userMail, String userPass,
                                      OnUserFirebaseCreation onUserCreation ) {
        FirebaseService.fbAuth.createUserWithEmailAndPassword(userMail,userPass)
                .addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(onUserCreation != null)
                    onUserCreation.onCreationSuccess();
            } else {
                if(onUserCreation != null)
                    onUserCreation.onCreationFailure(task.getException());
            }
        });
    }
}
