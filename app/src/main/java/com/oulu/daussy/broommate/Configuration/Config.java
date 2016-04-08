package com.oulu.daussy.broommate.Configuration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by daussy on 25/03/16.
 * to be able to use basic CRUD functions
 */
public class Config {

    public static final String IP_SERVER            =   "http://192.168.0.104:8888/";

    //Address of our scripts of the CRUD
    public static final String URL_ADD_TASK         =   IP_SERVER + "Broommate/addTask.php";
    public static final String URL_UPDATE_TASK      =   IP_SERVER + "Broommate/updateTask.php?id=";
    public static final String URL_GET_ALL_TASKS    =   IP_SERVER + "Broommate/getTasks.php?id_group=";
    public static final String URL_ADD_USER         =   IP_SERVER + "Broommate/addUser.php";

    public static final String STATE_TODO           =   "TODO";
    public static final String STATE_DOING          =   "DOING";
    public static final String STATE_DONE           =   "DONE";
    public static final String STATE_DELETED        =   "DELETED";

    /************* TASK **************/
    //Keys that will be used to send request to php scripts
    public static final String KEY_TASK_ID          =   "id";
    public static final String KEY_TASK_NAME        =   "name";
    public static final String KEY_TASK_OWNER       =   "owner";
    public static final String KEY_TASK_WORKER      =   "worker";
    public static final String KEY_TASK_PRIORITY    =   "priority";
    public static final String KEY_TASK_DATE_START  =   "date_start";
    public static final String KEY_TASK_DATE_END    =   "date_end";
    public static final String KEY_TASK_STATE       =   "state";
    public static final String KEY_TASK_OWNER_NAME  =   "owner_name";
    public static final String KEY_TASK_WORKER_NAME =   "worker_name";

    //JSON Tags
    public static final String TAG_JSON_ARRAY       =   "result";
    public static final String TAG_TASK_ID          =   "id";
    public static final String TAG_TASK_NAME        =   "name";
    public static final String TAG_TASK_OWNER       =   "owner";
    public static final String TAG_TASK_WORKER      =   "worker";
    public static final String TAG_TASK_PRIORITY    =   "owner";
    public static final String TAG_TASK_DATE_START  =   "date_start";
    public static final String TAG_TASK_DATE_END    =   "date_end";
    public static final String TAG_TASK_STATE       =   "state";


    //Task id to pass with the intent
    public final static String TASK_ID = "task_id";


    /************* USER ***************/
    public static final String KEY_USER_ID          =   "id";
    public static final String KEY_USER_NAME        =   "name";
    public static final String KEY_USER_SURNAME     =   "surname";
    public static final String KEY_USER_GROUP_ID    =   "group";
    public static final String KEY_USER_FACEBOOK_ID =   "facebook_id";
    public static final String KEY_USER_PICTURE     =   "picture";
    
}
