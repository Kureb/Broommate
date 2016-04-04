package com.oulu.daussy.broommate.Model;

/**
 * Created by daussy on 04/04/16.
 * Singleton used to always have access to the user currently connected
 */
public class CurrentUser extends User {

    private static CurrentUser USER;

    private CurrentUser(){
        super();
    }

    public static CurrentUser getInstance() {
        if (USER == null)
            USER = new CurrentUser();

        return USER;
    }

}
