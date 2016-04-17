package com.oulu.daussy.broommate.Model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daussy on 03/04/16.
 */
public class User {

    private int id;
    private String name;
    private String facebook_id;
    private int group_id;
    private String posX;
    private String posY;
    private String lastUpdatePos;
    private String groupKey;

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getPosX() {
        return posX;
    }

    public void setPosX(String posX) {
        this.posX = posX;
    }

    public String getPosY() {
        return posY;
    }

    public void setPosY(String posY) {
        this.posY = posY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getLastUpdatePos() {
        return lastUpdatePos;
    }

    public void setLastUpdatePos(String lastUpdatePos) {
        this.lastUpdatePos = lastUpdatePos;
    }

    //Kiitos http://stackoverflow.com/a/26036013/4307336
    public String timeAgo() {
        String datetime = this.getLastUpdatePos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateUpdated = sdf.parse(datetime);
            String nowS = sdf.format(new Date());
            Date now = sdf.parse(nowS); //Wrong date, something is wrong with hours, TODO fix it

            long time_elapsed    = now.getTime() - dateUpdated.getTime();
            time_elapsed    = time_elapsed / 1000; //millisecond to second
            long seconds    = time_elapsed;
            long minutes    = Math.round(time_elapsed / 60 );
            long hours      = Math.round(time_elapsed / 3600);
            long days       = Math.round(time_elapsed / 86400 );
            long weeks      = Math.round(time_elapsed / 604800);
            long months     = Math.round(time_elapsed / 2600640 );
            long years      = Math.round(time_elapsed / 31207680 );


            // Seconds
            if(seconds <= 60){
                return "just now";
            }
            //Minutes
            else if(minutes <60){
                if(minutes==1){
                    return "one minute ago";
                }
                else{
                    return minutes + " minutes ago";
                }
            }
            //Hours
            else if(hours <=24){
                if(hours==1){
                    return "an hour ago";
                }else{
                    return hours + " hours ago";
                }
            }
            //Days
            else if(days <= 7){
                if(days==1){
                    return "yesterday";
                }else{
                    return days + " days ago";
                }
            }
            //Weeks
            else if(weeks <= 4.3){
                if(weeks==1){
                    return "a week ago";
                }else{
                    return weeks + " weeks ago";
                }
            }
            //Months
            else if(months <=12){
                if(months==1){
                    return "a month ago";
                }else{
                    return months + " months ago";
                }
            }
            //Years
            else{
                if(years==1){
                    return "one year ago";
                }else{
                    return years + " years ago";
                }
            }
            //Log.d("TIMEAGO", ": " + time_elapsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
