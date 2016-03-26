package pl.nkg.biblospk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    public static boolean checkWiFi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        boolean wifiConnected = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            wifiConnected |= networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET;
        }

        return wifiConnected;
    }
}
