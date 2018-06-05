package com.blkxltng.caip.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blkxltng.caip.CameraInfo;
import com.blkxltng.caip.R;
import com.blkxltng.caip.database.CameraReaderDbHelper;
import com.rvirin.onvif.onvifcamera.OnvifDevice;
import com.rvirin.onvif.onvifcamera.OnvifListener;
import com.rvirin.onvif.onvifcamera.OnvifRequest;
import com.rvirin.onvif.onvifcamera.OnvifResponse;

import static com.rvirin.onvif.onvifcamera.OnvifDeviceKt.currentDevice;

public class SignInFragment extends Fragment implements OnvifListener {

    public interface SignInListener {
        void onClickLoadCamera(String url);
    }

    private static final String TAG = "SignInFragment";

    TextInputEditText edittextNickname, edittextIP, edittextHTTP, edittextRTSP, edittextUsername, edittextPassword;
    ProgressBar loadProgress;
    Button buttonLoadCamera, buttonCancel;
    String mUrl = "";

    private SignInListener signInListener;
    private CameraReaderDbHelper mDbHelper;
    private CameraInfo cameraInfo;

    private int argId;
    private String argName;
    private String argIp;
    private String argUsername;
    private String argPassword;
    private boolean argumentsRead;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInListener = (SignInListener) getActivity();
        mDbHelper = new CameraReaderDbHelper(getContext());
        cameraInfo = new CameraInfo();

//        argumentsRead = false;
//
//        if(!argumentsRead){
//            //Get Arguments
//            argId = getArguments().getInt("id");
//            argName = getArguments().getString("nickname");
//            argIp = getArguments().getString("ipAddress");
//            argUsername = getArguments().getString("username");
//            argPassword = getArguments().getString("password");
//            if(argIp != null && !argIp.equals("")) {
//                argumentsRead = true;
//            }
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_sign_in, container, false);

        edittextNickname = view.findViewById(R.id.editText_nickname);
        edittextIP = view.findViewById(R.id.editText_IP);
        edittextHTTP = view.findViewById(R.id.editText_httpPort);
        edittextRTSP = view.findViewById(R.id.editText_rtspPort);
        edittextUsername = view.findViewById(R.id.editText_username);
        edittextPassword = view.findViewById(R.id.editText_password);

//        edittextRTSP.setVisibility(View.GONE);
//        edittextHTTP.setVisibility(View.GONE);

        TextInputLayout rtspInputLayout = view.findViewById(R.id.textInputLayout_rtsp);
        TextInputLayout httpInputLayout = view.findViewById(R.id.textInputLayout_http);
        rtspInputLayout.setVisibility(View.GONE);
        httpInputLayout.setVisibility(View.GONE);

        loadProgress = view.findViewById(R.id.progressBar);


        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonLoadCamera = view.findViewById(R.id.button_loadCamera);
        buttonLoadCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                final boolean connectedToInternet = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                if(connectedToInternet) {

                    cameraInfo = new CameraInfo();

                    buttonLoadCamera.setEnabled(false);
                    String nickname = edittextNickname.getText().toString();
                    String IP = edittextIP.getText().toString();
                    if(!edittextRTSP.getText().toString().isEmpty()) {
                        IP += ":" + edittextRTSP.getText().toString();
//                            cameraInfo.setRtspPort(edittextRTSP.getText().toString());
                    }
                    String username = edittextUsername.getText().toString();
                    String password = edittextPassword.getText().toString();

                    //Make cameraInfo object
                    cameraInfo.setName(nickname);
                    cameraInfo.setIpAddress(IP);
                    if(!edittextRTSP.getText().toString().isEmpty()) {
                        cameraInfo.setRtspPort(edittextRTSP.getText().toString());
                    }
                    cameraInfo.setUsername(username);
                    cameraInfo.setPassword(password);
//                        cameraInfo.setUrl(mUrl);

                    if(!IP.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                        loadProgress.setVisibility(View.VISIBLE);
                        currentDevice = new OnvifDevice(IP, username, password);
                        currentDevice.setListener(SignInFragment.this);
                        currentDevice.getServices();
                    } else {
                        Toast.makeText(getContext(), "Please input a IP Address, username, and password.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Please connect to the internet to stream a camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void requestPerformed(OnvifResponse response) {

        if (!response.getSuccess()) {
            Log.e("ERROR", "request failed: " + response.getRequest().getType() +
                    " \n Response: " + response.getError());
            Toast.makeText(getContext(), "Request failed: " + response.getRequest().getType(), Toast.LENGTH_SHORT).show();
            loadProgress.setVisibility(View.INVISIBLE);
            buttonLoadCamera.setEnabled(true);
        }

        Log.d("ONVIF","Request " + response.getRequest().getType() + " performed.");
        Log.d("ONVIF","Succeeded: " + response.getSuccess() + "\nmessage:" + response.getParsingUIMessage());
        if (response.getRequest().getType() == OnvifRequest.Type.GetServices) {
            currentDevice.getDeviceInformation();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetDeviceInformation) {
            cameraInfo.setDeviceInfo(response.getParsingUIMessage());
//            Toast.makeText(getActivity(), "Device information retrieved", Toast.LENGTH_SHORT).show();
            currentDevice.getProfiles();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetProfiles) {
            int profilesCount = currentDevice.getMediaProfiles().size();
//            Toast.makeText(getActivity(), profilesCount +  " profiles retrieved", Toast.LENGTH_SHORT).show();
            currentDevice.getStreamURI();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetStreamURI) {
//            Toast.makeText(getActivity(), "Stream URI retrieved", Toast.LENGTH_SHORT).show();
            Log.d("ONVIF", "Stream URI retrieved: " + currentDevice.getRtspURI());
            mUrl = currentDevice.getRtspURI();
            cameraInfo.setUrl(mUrl);
            Toast.makeText(getContext(), "Camera loaded", Toast.LENGTH_SHORT).show();
//            buttonLoadCamera.setEnabled(true);
//            buttonLoadCamera.setText("Play Camera");
            loadProgress.setVisibility(View.INVISIBLE);


            if(currentDevice.isConnected()) {
                if(!mDbHelper.checkForCamera(cameraInfo)) {
                    mDbHelper.addCamera(cameraInfo);
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Camera exists!", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onClick: mUrl being sent is " + mUrl);
                //signInListener.onClickLoadCamera(mUrl);
            }
        }
    }
}
