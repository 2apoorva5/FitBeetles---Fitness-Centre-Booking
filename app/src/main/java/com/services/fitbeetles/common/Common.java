package com.services.fitbeetles.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.services.fitbeetles.model.Cities;
import com.services.fitbeetles.model.User;

public class Common {

    public static User currentUser;
    public static String currentCity = null;
    public static String currentCategory = null;
    public static String currentFacility = null;
    public static String currentOfferingsAmenities = null;
    public static String currentReview = null;
    public static String currentWebViewTitle = null;

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i = 0; i < info.length; i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
