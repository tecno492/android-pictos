package com.example.pictopocketiv.states;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityStateMV extends ViewModel {


    /** ENUMS **/
    // State Machine Signal enum
    public enum Signal {
        OK,
        KO,
        KO2
    }

    // State Machine State enum
    public enum ActivityState {

        CH_AUTH_STATE,  // checking auth state

        W_DB_POPULATION,    // Waiting for db population
        LOGGED_IN,

        W_USER_CREDENTIALS, // Waiting for user credentials
        W_USER_CREDENTIALS_RSP, // Waiting for loging response
        E_INVALID_CREDENTIALS,  // Error in credentials
        E_INVALID_USER, // Error in user data

        W_SIGNUP_CREDENTIALS,   // Waiting for signup credentials
        W_SIGNUP_RSP,  // Waiting for signup response
        E_INVALID_SIGNUP,   // Error in signup

        E_UNDEFINED  // Undefined error
    }

    /** ATTRS **/
    // Current State
    private MutableLiveData<ActivityState> state = new MutableLiveData<>();


    /** METHODS **/
    // State Machine
    /**
     * nextState:
     *  Use the signal to calculate the new state
     * @param signal
     * @return new state
     */
    private ActivityState nextState(Signal signal ) {

        switch (this.state.getValue()) {

            // On checking auth state
            case CH_AUTH_STATE:
                if(signal == Signal.OK)
                    return ActivityState.W_DB_POPULATION;
                else
                    return ActivityState.W_USER_CREDENTIALS;
            // On loggin
            case W_USER_CREDENTIALS:
                if(signal == Signal.OK)
                    return ActivityState.W_USER_CREDENTIALS_RSP;
                else
                    return ActivityState.W_SIGNUP_CREDENTIALS;

            case W_USER_CREDENTIALS_RSP:
                if(signal == Signal.OK)
                    return ActivityState.W_DB_POPULATION;
                else if(signal == Signal.KO )
                    return ActivityState.E_INVALID_CREDENTIALS;
                else
                    return ActivityState.E_INVALID_USER;

            case LOGGED_IN:
                if(signal == Signal.OK)
                    return ActivityState.LOGGED_IN;
                else
                    return ActivityState.W_USER_CREDENTIALS;

            case W_DB_POPULATION:
                if(signal == Signal.OK)
                    return ActivityState.LOGGED_IN;
                else
                    return ActivityState.W_DB_POPULATION;

            // On signup
            case W_SIGNUP_CREDENTIALS:
                if(signal == Signal.OK)
                    return ActivityState.W_SIGNUP_RSP;
                else
                    return ActivityState.W_USER_CREDENTIALS;
            case W_SIGNUP_RSP:
                if(signal == Signal.OK)
                    return ActivityState.W_DB_POPULATION;
                else
                    return ActivityState.E_INVALID_SIGNUP;

            // On Error
            case E_UNDEFINED:
                return ActivityState.E_UNDEFINED;
            case E_INVALID_CREDENTIALS:
                if(signal == Signal.OK)
                    return ActivityState.W_USER_CREDENTIALS;
                else
                    return ActivityState.E_INVALID_CREDENTIALS;
            case E_INVALID_USER:
                if(signal == Signal.OK)
                    return ActivityState.W_USER_CREDENTIALS;
                else
                    return ActivityState.E_INVALID_USER;
            case E_INVALID_SIGNUP:
                if(signal == Signal.OK)
                    return ActivityState.W_SIGNUP_CREDENTIALS;
                else
                    return ActivityState.E_INVALID_SIGNUP;
        }

        return ActivityState.E_UNDEFINED;
    }


    /** Setters **/
    public void setState(ActivityState state) {
        this.state.setValue(state);
    }

    public void setStatus(Signal signal) {
        this.state.setValue(nextState(signal));
    }

    // Observe this
    public LiveData<ActivityState> getState() {
        return this.state;
    }

}
