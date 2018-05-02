package com.blkxltng.caip.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blkxltng.caip.R;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

public class StreamFragment extends Fragment implements VlcListener {

    private static final String TAG = "StreamFragment";

    private VlcVideoLibrary vlcVideoLibrary = null;
    String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);

        Bundle args = getArguments();

        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        vlcVideoLibrary = new VlcVideoLibrary(getActivity(), this, surfaceView);
        url = args.getString("streamURI");
        Log.d(TAG, "onCreateView: url is " + url);
        vlcVideoLibrary.play(url);

        return view;
    }

    @Override
    public void onComplete() {
        Toast.makeText(getContext(), "Playing", Toast.LENGTH_SHORT).show();
//        vlcVideoLibrary.play(url);
    }

    @Override
    public void onError() {
        Toast.makeText(getContext(), "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
    }
}
