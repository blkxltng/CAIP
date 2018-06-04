package com.blkxltng.caip.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;
import com.blkxltng.caip.R;
import com.blkxltng.caip.SettingsActivity;

public class SecurityPreferenceFragment extends PreferenceFragment {

    public interface PreferenceClickListener {
        void onClickPreferenceAddPin();
        void onClickPreferenceChangePin();
        void onClickPreferenceDeletePin();
    }

    PreferenceClickListener mPreferenceClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//            mPreferenceClickListener = (PreferenceClickListener) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_security);
        setHasOptionsMenu(true);
            mPreferenceClickListener = (PreferenceClickListener) getActivity();

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));

        boolean pinExist = false;

        try {
            pinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
        } catch (PFSecurityException exception) {
            exception.printStackTrace();
        }

        if(pinExist) {
            findPreference(getResources().getString(R.string.pref_key_add_pin)).setEnabled(false);
            findPreference(getResources().getString(R.string.pref_key_add_pin)).setSummary("PIN already added");
        } else {
            findPreference(getResources().getString(R.string.pref_key_add_pin)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), "blah", Toast.LENGTH_SHORT).show();
                    mPreferenceClickListener.onClickPreferenceAddPin();
                    return true;
                }
            });
            findPreference(getResources().getString(R.string.pref_key_change_pin)).setEnabled(false);
            findPreference(getResources().getString(R.string.pref_key_change_pin)).setSummary("No PIN set");
            findPreference(getResources().getString(R.string.pref_key_delete_pin)).setEnabled(false);
            findPreference(getResources().getString(R.string.pref_key_delete_pin)).setSummary("No PIN set");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
