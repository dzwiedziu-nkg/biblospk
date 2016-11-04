package pl.nkg.biblospk.ui;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.HashMap;

import pl.nkg.biblospk.PreferencesProvider;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.Statics;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private OnFragmentInteractionListener mListener;

    private static final HashMap<String, Integer> SUMMARIES;

    static {
        SUMMARIES = new HashMap<>();
        SUMMARIES.put(PreferencesProvider.PREF_TODAY, R.string.pref_description_today);
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        updateSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()), PreferencesProvider.PREF_TODAY);

        Preference button = findPreference("button");
        button.setVisible(Statics.sGlobalState.isValidCredentials());
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mListener != null) {
                    mListener.onLogout();
                }
                return true;
            }
        });

        if (Statics.sGlobalState.isValidCredentials()) {
            button.setTitle(getString(R.string.pref_title_account, Statics.sGlobalState.getPreferencesProvider().getPrefLogin().toUpperCase()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(sharedPreferences, key);

        if (mListener != null) {
            mListener.onSharedPreferenceChanged(sharedPreferences, key);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        CharSequence summary = "";
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                summary = listPreference.getEntries()[prefIndex];
            }
        } else if (preference instanceof EditTextPreference) {
            summary = sharedPreferences.getString(key, "");
        }

        if (SUMMARIES.containsKey(key)) {
            preference.setSummary(StringUtils.defaultIfBlank(summary, getText(SUMMARIES.get(key))));
        }
    }

    public interface OnFragmentInteractionListener {
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);

        void onLogout();
    }
}
