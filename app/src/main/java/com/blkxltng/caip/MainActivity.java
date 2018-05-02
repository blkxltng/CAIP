package com.blkxltng.caip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.blkxltng.caip.fragments.SignInFragment;
import com.blkxltng.caip.fragments.StreamFragment;

public class MainActivity extends SingleFragmentActivity implements SignInFragment.SignInListener {

    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new SignInFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
