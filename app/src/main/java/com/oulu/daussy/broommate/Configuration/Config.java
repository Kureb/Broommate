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

    //public static final String IP_SERVER            =   "http://192.168.0.102:8888/";
    public static final String IP_SERVER            =   "http://alexandredaussy.fr/";

    //Address of our scripts of the CRUD
    public static final String URL_ADD_TASK         =   IP_SERVER + "Broommate/addTask.php";
    public static final String URL_UPDATE_TASK      =   IP_SERVER + "Broommate/updateTask.php?id=";
    public static final String URL_GET_ALL_TASKS    =   IP_SERVER + "Broommate/getTasks.php?id_group=";
    public static final String URL_ADD_USER         =   IP_SERVER + "Broommate/addUser.php";
    public static final String URL_GET_ALL_USERS    =   IP_SERVER + "Broommate/getUsers.php?id_group=";
    public static final String URL_UPDATE_LOCATION  =   IP_SERVER + "Broommate/updateLocation.php";
    public static final String URL_UPDATE_HOME      =   IP_SERVER + "Broommate/updateHome.php";
    public static final String URL_ADD_GROUP        =   IP_SERVER + "Broommate/addGroup.php";
    public static final String URL_JOIN_GROUP       =   IP_SERVER + "Broommate/joinGroup.php";
    public static final String URL_GET_USER         =   IP_SERVER + "Broommate/getUser.php?id_group=";
    public static final String URL_GET_UNIQUE_USER  =   IP_SERVER + "Broommate/getUniqueUser.php?id_facebook=";
    public static final String URL_UPDATE_USER      =   IP_SERVER + "Broommate/updateUser.php";
    public static final String URL_REMOVE_USER      =   IP_SERVER + "Broommate/removeUser.php";

    public static final String PROJECT_NUMBER       =   "731765884851";
    public static final String URL_GOOGLE_CLOUD_MSG =   "https://android.googleapis.com/gcm/send";
    public static final String API_KEY              =   "AIzaSyBuzanSYxT1NGi3ghf73G2nHSGkClYJzi0";
    public static final double SIZE_CIRCLE          =   50.0; //50 meters is the perimeter in which we can say
                                                                //that we are at home


    public static final String NOTIF_TITLE              =   "Request from Broommate";
    public static final String NOTIF_CONTENT_LOCATION   =   " asks for your location";
    public static final String NOTIF_CONTENT_TASK_DOING =   " is doing a task you've added";
    public static final String NOTIF_CONTENT_TASK_DONE  =   " has done a task you've added";
    public static final String HOME                     =   "Home";

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
    public static final String KEY_USER_GROUP_ID    =   "group_id";
    public static final String KEY_USER_FACEBOOK_ID =   "facebook_id";
    public static final String KEY_USER_PICTURE     =   "picture";
    public static final String KEY_USER_POSX        =   "posX";
    public static final String KEY_USER_POSY        =   "posY";
    public static final String KEY_USER_LAST_UPDATE =   "last_update";
    public static final String KEY_USER_GROUP_NAME  =   "name";
    public static final String KEY_USER_G_NAME      =   "group_name";
    public static final String KEY_USER_GROUP_KEY   =   "key";
    public static final String KEY_USER_GOOGLE_ID   =   "google_id";

    public static final String TAG_USER_ID          =   "id";
    public static final String TAG_USER_NAME        =   "name";

    public static final String HOME_POSX            =   "posX_home";
    public static final String HOME_POSY            =   "posY_home";

    public static final String EMPTY_JSON           =   "{\"result\":[]}\n";
    
}
