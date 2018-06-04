package com.blkxltng.caip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.blkxltng.caip.fragments.settings.SecurityPreferenceFragment;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity implements SecurityPreferenceFragment.PreferenceClickListener{

    public interface MainActivityCalls {
        void mainActivityAddPin();
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || SecurityPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
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

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class SecurityPreferenceFragment extends PreferenceFragment {
//
//        public interface PreferenceClickListener {
//            void onClickPreferenceAddPin();
//            void onClickPreferenceChangePin();
//            void onClickPreferenceDeletePin();
//        }
//
//        PreferenceClickListener mPreferenceClickListener;
//
//        @Override
//        public void onAttach(Context context) {
//            super.onAttach(context);
////            mPreferenceClickListener = (PreferenceClickListener) getActivity();
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_security);
//            setHasOptionsMenu(true);
////            mPreferenceClickListener = (PreferenceClickListener) getActivity();
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
////            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//
//            boolean pinExist = false;
//
//            try {
//                pinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
//            } catch (PFSecurityException exception) {
//                exception.printStackTrace();
//            }
//
//            if(pinExist) {
//                findPreference(getResources().getString(R.string.pref_key_add_pin)).setEnabled(false);
//                findPreference(getResources().getString(R.string.pref_key_add_pin)).setSummary("PIN already added");
//                findPreference(getResources().getString(R.string.pref_key_add_pin)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                    @Override
//                    public boolean onPreferenceClick(Preference preference) {
//                        mPreferenceClickListener.onClickPreferenceAddPin();
//                        return false;
//                    }
//                });
//            } else {
//                findPreference(getResources().getString(R.string.pref_key_change_pin)).setEnabled(false);
//                findPreference(getResources().getString(R.string.pref_key_change_pin)).setSummary("No PIN set");
//                findPreference(getResources().getString(R.string.pref_key_delete_pin)).setEnabled(false);
//                findPreference(getResources().getString(R.string.pref_key_delete_pin)).setSummary("No PIN set");
//            }
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onClickPreferenceAddPin() {
//        PFLockScreenFragment fragment = new PFLockScreenFragment();
//        PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(getApplicationContext())
//                .setMode(PFFLockScreenConfiguration.MODE_CREATE);
//        builder.setUseFingerprint(true);
//        fragment.setConfiguration(builder.build());
//        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
//            @Override
//            public void onCodeCreated(String encodedCode) {
//                PreferenceSettings.saveToPref(getApplicationContext(), encodedCode);
//                getFragmentManager().popBackStack();
//                Toast.makeText(getApplicationContext(), "PIN saved", Toast.LENGTH_SHORT).show();
//            }
//        });

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, fragment);
//        fragmentTransaction.addToBackStack("");
//        fragmentTransaction.commit();

//        Intent returnIntent = new Intent();
//        setResult(0, returnIntent);
//        finish();

        Intent intent = new Intent (SettingsActivity.this, MainActivity.class);
        intent.putExtra("EXTRA", "addPinFragment");
        startActivity(intent);
    }

    @Override
    public void onClickPreferenceChangePin() {

    }

    @Override
    public void onClickPreferenceDeletePin() {

    }
}
