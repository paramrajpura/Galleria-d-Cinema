package app.com.example.android.galleriadcinema;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Admin on 16-Apr-16.
 */
public class Utility {
    static String API_KEY = ""; //ENTER API KEY HERE
    public static boolean checkInternetConnection(Context context){
        if(context!=null){
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        else{
            return false;
        }

    }
}
