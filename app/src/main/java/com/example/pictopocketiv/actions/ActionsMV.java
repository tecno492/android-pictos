package com.example.pictopocketiv.actions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActionsMV extends ViewModel  {

    public enum ActionMVType {
        SEARCH, ADD_FROM_CAMERA, ADD_FROM_GALLERY,
        DENY,REMOVE,EDIT,
        BLOCK_TOOLS, UNBLOCK_TOOLS, ADD_CAT
    }

    private MutableLiveData<ActionMVType> mAction = new MutableLiveData<>();

    public void setAction(ActionMVType action) {
        this.mAction.setValue(action);
    }

    public LiveData<ActionMVType> getAction() {
        return this.mAction;
    }
}
