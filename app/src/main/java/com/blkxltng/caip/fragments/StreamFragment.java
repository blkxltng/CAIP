package com.blkxltng.caip.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.blkxltng.caip.R;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class StreamFragment extends Fragment implements VlcListener {

    private static final String TAG = "StreamFragment";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCREENSHOT = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RECORD = 1;

    private VlcVideoLibrary vlcVideoLibrary = null;
    String url, name;
    boolean isPlaying = false;
//    SurfaceView surfaceView;
    TextureView textureView;
    Button buttonPlay, buttonScreenshot, buttonRecord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);

        Bundle args = getArguments();

//        surfaceView = view.findViewById(R.id.surfaceView);
        textureView = view.findViewById(R.id.textureView);
        vlcVideoLibrary = new VlcVideoLibrary(getActivity(), this, textureView);

        url = args.getString("streamURI");
        name = args.getString("camName");
        Log.d(TAG, "onCreateView: url is " + url);
//        vlcVideoLibrary.play(url);

        buttonPlay = view.findViewById(R.id.button_play);
        buttonScreenshot = view.findViewById(R.id.button_picture);
        buttonRecord = view.findViewById(R.id.button_video);

        buttonScreenshot.setEnabled(false);
        buttonRecord.setEnabled(false);
//        buttonScreenshot.setVisibility(View.GONE);
        buttonRecord.setVisibility(View.GONE);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying) {
                    vlcVideoLibrary.play(url);
                    buttonPlay.setText("Stop");
                    buttonScreenshot.setEnabled(true);
//                    buttonRecord.setEnabled(true);
                    isPlaying = !isPlaying;
                } else {
                    vlcVideoLibrary.stop();
                    buttonPlay.setText("Play");
                    buttonScreenshot.setEnabled(false);
//                    buttonRecord.setEnabled(false);
                    isPlaying = !isPlaying;
                }
            }
        });

        buttonScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                surfaceView.setDrawingCacheEnabled(true);
//                Bitmap b = surfaceView.getDrawingCache();
//                try {
//                    b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream("/CAIP/Screenshots/" +
//                            System.currentTimeMillis() + "_" + name + ".jpg"));
//                    Toast.makeText(getContext(), "Screenshot saved", Toast.LENGTH_SHORT).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                takeScreenshot();

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCREENSHOT);

                        // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCREENSHOT is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    takeScreenshot();
                }
            }
        });

//        buttonRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
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
//                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RECORD);
//
//                        // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RECORD is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                } else {
//                    // Permission has already been granted
//                    recordVideo();
//                }
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
        Toast.makeText(getContext(), "Loading feed...", Toast.LENGTH_SHORT).show();
//        vlcVideoLibrary.play(url);
    }

    @Override
    public void onError() {
        Toast.makeText(getContext(), "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCREENSHOT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // media-related task you need to do.
                    takeScreenshot();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    buttonScreenshot.setEnabled(false);
                    buttonRecord.setEnabled(false);

                }
                return;
            }
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RECORD: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // media-related task you need to do.
//                    recordVideo();
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    buttonScreenshot.setEnabled(false);
//                    buttonRecord.setEnabled(false);
//
//                }
//                return;
//            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void takeScreenshot() {

        Log.d(TAG, "takeScreenshot: saving");

        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "CAIP", "Screenshots");
        if (!f1.exists()) {
            f1.mkdirs();
        }

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/CAIP/Screenshots/" + now + "_" + name + ".jpg";

            // create bitmap screen capture
//            textureView.setDrawingCacheEnabled(true);
//            textureView.buildDrawingCache();
//            Bitmap bitmap = Bitmap.createBitmap(textureView.getDrawingCache());
////            bitmap.setHasAlpha(true);
//            textureView.setDrawingCacheEnabled(false);
            Bitmap bitmap = textureView.getBitmap();

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 95;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(getActivity(), "Screenshot saved to " + mPath, Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

//    private void recordVideo() {
//
//        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "CAIP", "Videos");
//        if (!f1.exists()) {
//            f1.mkdirs();
//        }
//
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//        String mPath = Environment.getExternalStorageDirectory().toString() + "/CAIP/Videos/" + now + "_" + name + ".flv";
//
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(mPath,256,256);
//        AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
//        try {
////            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
//            recorder.setFormat("flv");
////            recorder.setSampleRate(sampleAudioRateInHz);
//            // Set in the surface changed method
//            recorder.setFrameRate(30);
//
//            // The filterString  is any ffmpeg filter.
//            // Here is the link for a list: https://ffmpeg.org/ffmpeg-filters.html
//            String filterString = "transpose=0";
//            FFmpegFrameFilter filter = new FFmpegFrameFilter(filterString, 256, 256);
//
//            //default format on android
//            filter.setPixelFormat(avutil.AV_PIX_FMT_NV21);
//
//            recorder.start(); //.startUnsafe()
//            Log.d(TAG, "recordVideo: recording started");
//            for (int i=0;i < 150;i++)
//            {
////                textureView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = textureView.getBitmap();
////                textureView.setDrawingCacheEnabled(false);
//                Frame outputFrame = androidFrameConverter.convert(bitmap);
//                recorder.record(outputFrame);
//            }
//            recorder.stop();
//            Log.d(TAG, "recordVideo: recording stopped");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
