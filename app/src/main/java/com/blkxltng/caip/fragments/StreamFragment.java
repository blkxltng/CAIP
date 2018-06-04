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
import android.widget.Button;
import android.widget.Toast;

import com.blkxltng.caip.R;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

public class StreamFragment extends Fragment implements VlcListener {

    private static final String TAG = "StreamFragment";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private VlcVideoLibrary vlcVideoLibrary = null;
    String url, name;
    boolean isPlaying = false;
    SurfaceView surfaceView;
    Button buttonPlay, buttonScreenshot, buttonRecord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);

        Bundle args = getArguments();

        surfaceView = view.findViewById(R.id.surfaceView);
        vlcVideoLibrary = new VlcVideoLibrary(getActivity(), this, surfaceView);

        url = args.getString("streamURI");
        name = args.getString("camName");
        Log.d(TAG, "onCreateView: url is " + url);
//        vlcVideoLibrary.play(url);

        buttonPlay = view.findViewById(R.id.button_play);
        buttonScreenshot = view.findViewById(R.id.button_picture);
        buttonRecord = view.findViewById(R.id.button_video);

        buttonScreenshot.setEnabled(false);
        buttonRecord.setEnabled(false);
        buttonScreenshot.setVisibility(View.GONE);
        buttonRecord.setVisibility(View.GONE);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying) {
                    vlcVideoLibrary.play(url);
                    buttonPlay.setText("Stop");
//                    buttonScreenshot.setEnabled(true);
//                    buttonRecord.setEnabled(true);
                    isPlaying = !isPlaying;
                } else {
                    vlcVideoLibrary.stop();
                    buttonPlay.setText("Play");
//                    buttonScreenshot.setEnabled(false);
//                    buttonRecord.setEnabled(false);
                    isPlaying = !isPlaying;
                }
            }
        });

//        buttonScreenshot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                surfaceView.setDrawingCacheEnabled(true);
////                Bitmap b = surfaceView.getDrawingCache();
////                try {
////                    b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream("/CAIP/Screenshots/" +
////                            System.currentTimeMillis() + "_" + name + ".jpg"));
////                    Toast.makeText(getContext(), "Screenshot saved", Toast.LENGTH_SHORT).show();
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                }
////                takeScreenshot();
//
//                // Here, thisActivity is the current activity
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Permission is not granted
//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                    } else {
//                        // No explanation needed; request the permission
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//
//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                } else {
//                    // Permission has already been granted
//                    takeScreenshot();
//                }
//            }
//        });
//
//        buttonRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        return view;
    }

    @Override
    public void onDetach() {
        if(isPlaying) {
            vlcVideoLibrary.stop();
        }
        super.onDetach();
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    takeScreenshot();
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    buttonScreenshot.setEnabled(false);
//                    buttonScreenshot.setEnabled(false);
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }

//    private void takeScreenshot() {
//
//        Log.d(TAG, "takeScreenshot: saving");
//
//        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "CAIP", "Screenshots");
//        if (!f1.exists()) {
//            f1.mkdirs();
//        }
//
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//        try {
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/CAIP/Screenshots/" + now + "_" + name + ".jpg";
//
//            // create bitmap screen capture
//            surfaceView.setDrawingCacheEnabled(true);
//            surfaceView.buildDrawingCache();
//            Bitmap bitmap = Bitmap.createBitmap(surfaceView.getDrawingCache());
////            bitmap.setHasAlpha(true);
//            surfaceView.setDrawingCacheEnabled(false);
//
//            File imageFile = new File(mPath);
//
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//        } catch (Throwable e) {
//            // Several error may come out with file handling or OOM
//            e.printStackTrace();
//        }
//    }
}
