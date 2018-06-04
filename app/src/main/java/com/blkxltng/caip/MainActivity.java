package com.blkxltng.caip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper;
import com.beautycoder.pflockscreen.security.PFSecurityException;
import com.blkxltng.caip.fragments.HomeFragment;
import com.blkxltng.caip.fragments.SignInFragment;
import com.blkxltng.caip.fragments.StreamFragment;

import static com.blkxltng.caip.PreferenceSettings.PREFS_NAME;

public class MainActivity extends SingleFragmentActivity implements SignInFragment.SignInListener,
        HomeFragment.AddCameraListener,
        HomeFragment.CameraListButtonClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirstRun();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        boolean pinExist = false;

        try {
            pinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
        } catch (PFSecurityException exception) {
            exception.printStackTrace();
        }

        if(pinExist) {
            MenuItem addPin = (MenuItem) menu.findItem(R.id.add_pin);
            addPin.setVisible(false);
        } else {
            MenuItem deletePin = (MenuItem) menu.findItem(R.id.delete_pin);
            deletePin.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        PFFLockScreenConfiguration.Builder builder;
//        FragmentTransaction fragmentTransaction;
        switch (item.getItemId()) {
            case R.id.add_pin:
                PFLockScreenFragment fragment = new PFLockScreenFragment();
                PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(MainActivity.this)
                        .setTitle("Input new PIN code")
                        .setMode(PFFLockScreenConfiguration.MODE_CREATE);
                fragment.setConfiguration(builder.build());
                fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
                    @Override
                    public void onCodeCreated(String encodedCode) {
//                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                        preferences.edit().putString(PREF_SECURITY_PIN_KEY, encodedCode).apply();
                        PreferenceSettings.saveToPref(MainActivity.this, encodedCode);
                        getSupportFragmentManager().popBackStack();
                        invalidateOptionsMenu();
                        Toast.makeText(getApplicationContext(), "PIN saved", Toast.LENGTH_SHORT).show();

                    }
                });
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                return true;
            case R.id.delete_pin:
                Log.d(TAG, "onOptionsItemSelected: delete pin clicked");
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Delete PIN?");
                alertDialog.setMessage("Are you sure you want to remove the lock?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final PFLockScreenFragment lockScreenFragment = new PFLockScreenFragment();
                        PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(getApplicationContext())
                                .setTitle("Input PIN to remove lock");
                        // Check if we're running on Android 6.0 (M) or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //Fingerprint API only available on from Android 6.0 (M)
                            FingerprintManager fingerprintManager = (FingerprintManager) getApplication().getSystemService(Context.FINGERPRINT_SERVICE);
                            if (!fingerprintManager.isHardwareDetected()) {
                                // Device doesn't support fingerprint authentication
                            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                                // User hasn't enrolled any fingerprints to authenticate with
                            } else {
                                // Everything is ready for fingerprint authentication
                                builder.setTitle("Input PIN or scan fingerprint to remove lock").setUseFingerprint(true);
                            }
                        }
                        builder.setMode(PFFLockScreenConfiguration.MODE_AUTH)
//                        .setCodeLength(4)
                                .setLeftButton("Forgot PIN?",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //Todo here too
                                            }
                                        });
                        lockScreenFragment.setConfiguration(builder.build());
                        lockScreenFragment.setEncodedPinCode(PreferenceSettings.getCode(getApplicationContext()));
                        lockScreenFragment.setLoginListener(new PFLockScreenFragment.OnPFLockScreenLoginListener() {
                            @Override
                            public void onCodeInputSuccessful() {
                                try {
                                    PFFingerprintPinCodeHelper.getInstance().delete();
                                } catch (PFSecurityException e) {
                                    e.printStackTrace();
                                }
                                getSupportFragmentManager().popBackStack();
                                invalidateOptionsMenu();
                                Toast.makeText(getApplicationContext(), "Lock removed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFingerprintSuccessful() {
                                try {
                                    PFFingerprintPinCodeHelper.getInstance().delete();
                                } catch (PFSecurityException e) {
                                    e.printStackTrace();
                                }
                                getSupportFragmentManager().popBackStack();
                                invalidateOptionsMenu();
                                Toast.makeText(getApplicationContext(), "Lock removed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPinLoginFailed() {
                                Toast.makeText(getApplicationContext(), "Wrong PIN!", Toast.LENGTH_SHORT).show();
                                lockScreenFragment.getCodeView().clear();
                            }

                            @Override
                            public void onFingerprintLoginFailed() {
                                Toast.makeText(getApplicationContext(), "Wrong fingerprint. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, lockScreenFragment);
                        fragmentTransaction.addToBackStack("");
                        fragmentTransaction.commit();
//                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            case R.id.settings:
                //Start settings activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClickLoadCamera(String url) {

        Log.d(TAG, "onClickLoadCamera: url (main) is " + url);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        StreamFragment f = new StreamFragment();
        Bundle args = new Bundle();
        args.putString("streamURI", url);
        f.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_container, f);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public void onClickAddCamera() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new SignInFragment());
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public void onClickListLoadCamera(String url, String name) {
        Log.d(TAG, "onClickLoadCamera: url (main) is " + url);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        StreamFragment f = new StreamFragment();
        Bundle args = new Bundle();
        args.putString("streamURI", url);
        args.putString("camName", name);
        f.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_container, f);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

//    @Override
//    public void onClickEditCamera() {
//
//    }
//
//    @Override
//    public void onClickDeleteCamera() {
//
//    }

    //Use this to check if this is the first time the user is running the app. If so, do some
    //introductory stuff
    private void checkFirstRun() {

//        final String PREF_SECURITY_PIN_KEY = "security_pin";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PreferenceSettings.PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            Toast.makeText(getApplicationContext(), "This is a regular run", Toast.LENGTH_SHORT).show();

            boolean pinExist = false;

            try {
                pinExist = PFFingerprintPinCodeHelper.getInstance().isPinCodeEncryptionKeyExist();
            } catch (PFSecurityException exception) {
                exception.printStackTrace();
            }

            if(pinExist) {
                final PFLockScreenFragment lockScreenFragment = new PFLockScreenFragment();
                PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(this)
                        .setTitle("Unlock with PIN");

                // Check if we're running on Android 6.0 (M) or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Fingerprint API only available on from Android 6.0 (M)
                    FingerprintManager fingerprintManager = (FingerprintManager) getApplication().getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.isHardwareDetected()) {
                        // Device doesn't support fingerprint authentication
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        // User hasn't enrolled any fingerprints to authenticate with
                    } else {
                        // Everything is ready for fingerprint authentication
                        builder.setTitle("Unlock with PIN or Fingerprint").setUseFingerprint(true);
                    }
                }

                        builder.setMode(PFFLockScreenConfiguration.MODE_AUTH)
//                        .setCodeLength(4)
                        .setLeftButton("Forgot PIN?",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //TODO Make the user delete everything to get access
                                    }
                                });
                lockScreenFragment.setConfiguration(builder.build());
                lockScreenFragment.setEncodedPinCode(PreferenceSettings.getCode(this));
                lockScreenFragment.setLoginListener(new PFLockScreenFragment.OnPFLockScreenLoginListener() {
                    @Override
                    public void onCodeInputSuccessful() {
                        getSupportFragmentManager().popBackStack();
                        Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFingerprintSuccessful() {
                        getSupportFragmentManager().popBackStack();
                        Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPinLoginFailed() {
                        Toast.makeText(getApplicationContext(), "Wrong PIN!", Toast.LENGTH_SHORT).show();
                        lockScreenFragment.getCodeView().clear();
                    }

                    @Override
                    public void onFingerprintLoginFailed() {
                        Toast.makeText(getApplicationContext(), "Wrong fingerprint. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, lockScreenFragment);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
            }
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)

            Toast.makeText(getApplicationContext(), "This is the first run", Toast.LENGTH_SHORT).show();

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Welcome to CAIP!");
            alertDialog.setMessage("Would you like to set a lock? You'll be have to use a PIN to access your cameras.");

            // Check if we're running on Android 6.0 (M) or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Fingerprint API only available on from Android 6.0 (M)
                FingerprintManager fingerprintManager = (FingerprintManager) getApplication().getSystemService(Context.FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                } else {
                    // Everything is ready for fingerprint authentication
                    alertDialog.setMessage("Would you like to set a lock? You'll be have to use a PIN or an enrolled fingerprint" +
                            " to access your cameras.");
                }
            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //Do Stuff

                            PFLockScreenFragment fragment = new PFLockScreenFragment();
                            PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(getApplicationContext())
                                    .setTitle("Input new PIN code")
                                    .setMode(PFFLockScreenConfiguration.MODE_CREATE);
                            fragment.setConfiguration(builder.build());
                            fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
                                @Override
                                public void onCodeCreated(String encodedCode) {
                                    //save somewhere;
//                                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                                    preferences.edit().putString(PREF_SECURITY_PIN_KEY, encodedCode).apply();
                                    PreferenceSettings.saveToPref(MainActivity.this, encodedCode);
                                    getSupportFragmentManager().popBackStack();
                                    Toast.makeText(getApplicationContext(), "PIN saved", Toast.LENGTH_SHORT).show();
                                    invalidateOptionsMenu();
                                }
                            });

                            //show fragment;
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.addToBackStack("");
                            fragmentTransaction.commit();

                            dialogInterface.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    AlertDialog confirmDialog = new AlertDialog.Builder(MainActivity.this).create();
                    confirmDialog.setTitle("Welcome to CAIP!");
                    confirmDialog.setMessage("That's cool, you can always add one later from the menu!");
                    confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //Do Stuff
                                    dialogInterface.dismiss();
                                }
                            });
                    confirmDialog.show();
                }
            });
            alertDialog.show();



        } else if (currentVersionCode > savedVersionCode) {

            //This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PreferenceSettings.PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
}
