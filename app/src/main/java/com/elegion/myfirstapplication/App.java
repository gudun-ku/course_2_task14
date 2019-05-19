package com.elegion.myfirstapplication;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.UserHandle;

import com.elegion.myfirstapplication.db.DataBase;
import com.elegion.myfirstapplication.model.User;

public class App extends Application {

    private DataBase mDatabase;
    private User loggedInUser;

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase = Room.databaseBuilder(getApplicationContext(), DataBase.class, "music_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public DataBase getDatabase() {
        return mDatabase;
    }

    public User getLoggedUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }
}
