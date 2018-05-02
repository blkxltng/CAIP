package com.blkxltng.caip.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blkxltng.caip.R;
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

    EditText edittextIP, edittextHTTP, edittextRTSP, edittextUsername, edittextPassword;
    Button buttonLoadCamera;
    String mUrl = "";

    private SignInListener signInListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInListener = (SignInListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_sign_in, container, false);

        edittextIP = view.findViewById(R.id.editText_IP);
        edittextHTTP = view.findViewById(R.id.editText_httpPort);
        edittextRTSP = view.findViewById(R.id.editText_rtspPort);
        edittextUsername = view.findViewById(R.id.editText_username);
        edittextPassword = view.findViewById(R.id.editText_password);

        buttonLoadCamera = view.findViewById(R.id.button_loadCamera);
        buttonLoadCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentDevice.isConnected()) {
                    Log.d(TAG, "onClick: mUrl being sent is " + mUrl);
                    signInListener.onClickLoadCamera(mUrl);
                } else {
                    buttonLoadCamera.setEnabled(false);
                    String IP = edittextIP.getText().toString();
                    if(!edittextRTSP.getText().toString().isEmpty()) {
                        IP += ":" + edittextRTSP.getText().toString();
                    }
                    String username = edittextUsername.getText().toString();
                    String password = edittextPassword.getText().toString();

                    if(!IP.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                        currentDevice = new OnvifDevice(IP, username, password);
                        currentDevice.setListener(SignInFragment.this);
                        currentDevice.getServices();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void requestPerformed(OnvifResponse response) {
        Log.d("ONVIF","Request " + response.getRequest().getType() + " performed.");
        Log.d("ONVIF","Succeeded: " + response.getSuccess() + "\nmessage:" + response.getParsingUIMessage());
        if (response.getRequest().getType() == OnvifRequest.Type.GetServices) {
            currentDevice.getDeviceInformation();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetDeviceInformation) {
            currentDevice.getProfiles();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetProfiles) {
            currentDevice.getStreamURI();
        } else if (response.getRequest().getType() == OnvifRequest.Type.GetStreamURI) {
            Log.d("ONVIF", "Stream URI retrieved: " + currentDevice.getRtspURI());
            mUrl = currentDevice.getRtspURI();
            Toast.makeText(getContext(), "Camera loaded", Toast.LENGTH_SHORT).show();
            buttonLoadCamera.setEnabled(true);
            buttonLoadCamera.setText("Play Camera");
        }
    }
}
