package com.oulu.daussy.broommate;

/**
 * Created by daussy on 25/03/16.
 * https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/
 */
public class Config {

    public static final String IP_SERVER            =   "http://192.168.0.101:8888/";

    //Address of our scripts of the CRUD
    public static final String URL_ADD_TASK         =   IP_SERVER + "Broommate/addTask.php";
    public static final String URL_UPDATE_TASK      =   IP_SERVER + "Broommate/updateTask.php?id=";
    public static final String URL_GET_ALL_TASKS    =   IP_SERVER + "Broommate/getTasks.php?id_group=";

    //Keys that will be used to send request to php scripts
    public static final String KEY_TASK_ID          =   "id";
    public static final String KEY_TASK_NAME        =   "name";
    public static final String KEY_TASK_OWNER       =   "owner";
    public static final String KEY_TASK_WORKER      =   "worker";
    public static final String KEY_TASK_PRIORITY    =   "priority";
    public static final String KEY_TASK_DATE_START  =   "date_start";
    public static final String KEY_TASK_DATE_END    =   "date_end";
    public static final String KEY_TASK_STATE       =   "state";

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


}
