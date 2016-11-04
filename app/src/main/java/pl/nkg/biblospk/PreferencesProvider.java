package pl.nkg.biblospk;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import pl.nkg.biblospk.data.Account;

public class PreferencesProvider {
    private final static String PREF_LOGIN = "login";
    private final static String PREF_PASSWORD = "password";
    private final static String PREF_LAST_CHECKING = "last_checking";
    private final static String PREF_LAST_CHECKED = "last_checked";

    private final static String PREF_CARDNUMBER = "card_number";
    private final static String PREF_NAME = "name";
    private final static String PREF_BORROWERNUMBER = "borrower_number";
    private final static String PREF_DEBTS = "debts";

    public final static String PREF_ACRA = "acra";
    public final static String PREF_PARSE = "parse";

    public final static String PREF_DEBUG = "debug";
    public final static String PREF_MOCK = "mock";
    public final static String PREF_CUSTOM_TODAY = "customtoday";
    public final static String PREF_TODAY = "today";

    private final static String PREFS_ACCOUNT[] = {PREF_CARDNUMBER, PREF_NAME, PREF_BORROWERNUMBER, PREF_DEBTS};

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

    /**
     * Time when refresh by it successful or error.
     *
     * @return mils form UNIX-epoch
     */
    public long getLastChecking() {
        return sharedPreferences.getLong(PREF_LAST_CHECKING, 0);
    }

    public void setLastChecking(long lastChecking) {
        apply(sharedPreferences.edit().putLong(PREF_LAST_CHECKING, lastChecking));
    }

    /**
     * Time when refresh by it successful.
     * @return mils form UNIX-epoch
     */
    public long getLastChecked() {
        return sharedPreferences.getLong(PREF_LAST_CHECKED, 0);
    }

    public void setLastChecked(long lastChecking) {
        apply(sharedPreferences.edit().putLong(PREF_LAST_CHECKED, lastChecking));
    }

    public String getPrefPassword() {
        return sharedPreferences.getString(PREF_PASSWORD, "");
    }

    public void setPrefPassword(String password) {
        apply(sharedPreferences.edit().putString(PREF_PASSWORD, password));
    }

    public boolean isACRA() {
        return sharedPreferences.getBoolean(PREF_ACRA, false);
    }

    public void setACRA(boolean acra) {
        apply(sharedPreferences.edit().putBoolean(PREF_ACRA, acra));
    }

    public boolean isParseBugReport() {
        return sharedPreferences.getBoolean(PREF_PARSE, false);
    }

    public void setParseBugReport(boolean report) {
        apply(sharedPreferences.edit().putBoolean(PREF_PARSE, report));
    }

    public void storeAccountProperties(Account account) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_CARDNUMBER, account.getCardNumber());
        editor.putString(PREF_NAME, account.getName());
        editor.putInt(PREF_BORROWERNUMBER, account.getBorrowerNumber());
        editor.putFloat(PREF_DEBTS, account.getDebts());
        apply(editor);
    }

    public boolean loadAccountProperties(Account account) {
        for (String key : PREFS_ACCOUNT) {
            if (!sharedPreferences.contains(key)) {
                return false;
            }
        }

        account.setCardNumber(sharedPreferences.getString(PREF_CARDNUMBER, ""));
        account.setName(sharedPreferences.getString(PREF_NAME, ""));
        account.setBorrowerNumber(sharedPreferences.getInt(PREF_BORROWERNUMBER, 0));
        account.setDebts(sharedPreferences.getFloat(PREF_DEBTS, 0));
        return true;
    }

    public void cleanAccountPropertiesAndCredentials(boolean loginToo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String key : PREFS_ACCOUNT) {
            editor.remove(key);
        }


        if (loginToo) {
            editor.remove(PREF_LOGIN);
        }

        editor.remove(PREF_PASSWORD);
        apply(editor);
    }

    private static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public boolean containsLoginAndPassword() {
        return sharedPreferences.contains(PREF_LOGIN) && sharedPreferences.contains(PREF_PASSWORD);
    }
}
