package pl.nkg.biblospk;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class PreferencesProvider {
    public final static String PREF_LOGIN = "login";
    public final static String PREF_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    public PreferencesProvider(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getPrefLogin() {
        return sharedPreferences.getString(PREF_LOGIN, "");
    }

    public void setPrefLogin(String login) {
        apply(sharedPreferences.edit().putString(PREF_LOGIN, login));
    }

    public String getPrefPassword() {
        return sharedPreferences.getString(PREF_PASSWORD, "");
    }

    public void setPrefPassword(String password) {
        apply(sharedPreferences.edit().putString(PREF_PASSWORD, password));
    }

    private static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
