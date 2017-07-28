package mindvalley.com.navigation;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mindvalley.com.navigation.tracker.GPSTracker;

/**
 * Created by anand_android on 10/1/2016.
 */
public class CommonUtils {

    private static String TAG = "CommonUtils";
    private static CommonUtils commonUtility = null;

    //Single ton method...
    public static CommonUtils getInstance() {
        if (commonUtility != null) {
            return commonUtility;
        } else {
            commonUtility = new CommonUtils();
            return commonUtility;
        }
    }

    public boolean isAboveMarshmallow() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            // Do something for marshmallow and above versions
            return true;
        } else {
            // do something for phones running an SDK before marshmallow
            return false;

        }
    }
    public Location getLocation(Context context) {
        GPSTracker gpsTracker = new GPSTracker(context);
        if (gpsTracker.canGetLocation()) {
            return gpsTracker.getLocation();
        } else {
            return null;
        }

    }
    public boolean isAboveLollipop() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for marshmallow and above versions
            return true;
        } else {
            // do something for phones running an SDK before marshmallow
            return false;

        }
    }

    public boolean hasNetworkConnection(Context context) {
        // TODO Auto-generated method stub

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean valid = false;

        /*NetworkInfo wifiNetwork = cm.getActiveNetworkInfo();
        if (wifiNetwork != null && wifiNetwork.isConnectedOrConnecting()) {
            valid = true;
        }*/

       /* NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnectedOrConnecting()) {
            valid = true;
        }*/

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            valid = true;
        }

        return valid;
    }

    public void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        /* InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
    *//*
     * If no view is focused, an NPE will be thrown
     *
     * Maxim Dmitriev
     *//*
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }*/
    }

    public void showSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Check whether the string contains value (OR) not.
     *
     * @param isNotNull string value which has to be checked
     * @return true if the given string is not null and this will validate if the contains
     * "null" as a String value too
     */

    public boolean isNullCheck(String isNotNull) {

        if (isNotNull != null) {
            if (!isNotNull.equalsIgnoreCase("") && isNotNull.length() > 0) {
                if (!isNotNull.equalsIgnoreCase("null")) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Checks whether the arraylist has values (or) not.
     *
     * @param arrayList which has to be checked
     * @return "true" if the given arraylist is not null and has values; otherwise "false".
     */


    public boolean isNullCheck(ArrayList<?> arrayList) {

        if (arrayList != null) {
            if (arrayList.size() > 0 && !arrayList.isEmpty()) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks whether the list has values (or) not.
     *
     * @param list which has to be checked
     * @return "true" if the given list is not null and has values; otherwise "false".
     */

    public boolean isNullCheck(List<?> list) {

        if (list != null) {
            if (list.size() > 0 && !list.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public String convertDateToString(String timeData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            if (timeData != null) {
                Date createdDate = sdf.parse(timeData);


                Date now = sdf.parse(sdf.format(new Date()));
                long diff = createdDate.getTime() - now.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                System.out.println("Days: " + days);
                if (days <= 0) {
                    return "Today";
                } else if (days <= 1) {
                    return "Yesterday";
                } else {
                    return (days + " days ago");
                }
            } else {
                return timeData;
            }
        } catch (ParseException e) {


            e.printStackTrace();
            return timeData;
        }


    }



    public boolean isAppForground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }


    public String whichActivityVisible(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return null;
            }
        }
        return am.getRunningTasks(1).get(0).topActivity.getClassName().toString();
    }
}
