package com.oulu.daussy.broommate.Model;

import com.oulu.daussy.broommate.Configuration.Config;

/**
 * Created by daussy on 02/04/16.
 */
public class Task {

    private int id;
    private String priority;
    private String state;
    private String title;
    private String owner;
    private String worker;
    private String date_start;
    private String date_end;
    private String owner_name;
    private String worker_name;
    private String ownerGoogleId;

    public String getOwnerGoogleId() {
        return ownerGoogleId;
    }

    public void setOwnerGoogleId(String ownerGoogleId) {
        this.ownerGoogleId = ownerGoogleId;
    }

    public String getWorker_name() {
        return worker_name;
    }

    public void setWorker_name(String worker_name) {
        this.worker_name = worker_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreviousState() {
        String currentState = this.getState();
        String previousState = null;
        switch (currentState) {
            case Config.STATE_DONE:
                previousState = Config.STATE_DOING;
                break;
            case Config.STATE_DOING:
                previousState = Config.STATE_TODO;
                break;
            case Config.STATE_TODO:
                previousState = Config.STATE_DELETED;
                break;
        }
        return previousState;
    }


    public String getNextState() {
        String currentState = this.getState();
        String nextState = null;
        switch (currentState) {
            case Config.STATE_TODO:
                nextState = Config.STATE_DOING;
                break;
            case Config.STATE_DOING:
                nextState = Config.STATE_DONE;
                break;
        }
        return nextState;
    }


    public boolean ownerIsMe() {
        return this.getOwner().equals(CurrentUser.getInstance().getFacebook_id());
    }

    public boolean workerIsMe() {
        return this.getWorker().equals(CurrentUser.getInstance().getFacebook_id());
    }


}
