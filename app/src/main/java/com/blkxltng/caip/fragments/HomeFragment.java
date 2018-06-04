package com.blkxltng.caip.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blkxltng.caip.CameraInfo;
import com.blkxltng.caip.R;
import com.blkxltng.caip.database.CameraReaderDbHelper;
import com.rvirin.onvif.onvifcamera.OnvifListener;
import com.rvirin.onvif.onvifcamera.OnvifRequest;
import com.rvirin.onvif.onvifcamera.OnvifResponse;

import java.util.ArrayList;
import java.util.List;

import static com.rvirin.onvif.onvifcamera.OnvifDeviceKt.currentDevice;

public class HomeFragment extends Fragment implements OnvifListener {

    public interface AddCameraListener {
        void onClickAddCamera();
    }

    public interface CameraListButtonClickListener {
        void onClickListLoadCamera(String url, String name);
//        void onClickEditCamera();
//        void onClickDeleteCamera();
//        void onClickedAddSecurity();
    }

    private static final String TAG = "HomeFragment";

    private AddCameraListener addCameraListener;
    private CameraListButtonClickListener cameraListButtonClickListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemAdapter mItemAdapter;
    private CameraReaderDbHelper mDbHelper;
    private List<CameraInfo> mCameraInfoList = new ArrayList<CameraInfo>();

    private String mUrl;
    private CameraInfo selectedCameraInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addCameraListener = (AddCameraListener) getActivity();
        cameraListButtonClickListener = (CameraListButtonClickListener) getActivity();
        mDbHelper = new CameraReaderDbHelper(getContext());

//        checkFirstRun();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mCameraInfoList = mDbHelper.getAllCameras();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_home);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mItemAdapter = new ItemAdapter(getLayoutInflater(), mCameraInfoList, R.layout.item_camera);
        mRecyclerView.setAdapter(mItemAdapter);

        FloatingActionButton addFab = view.findViewById(R.id.fab_add);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCameraListener.onClickAddCamera();
            }
        });

        return view;
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private final List<CameraInfo> mCameraInfoList;
        private LayoutInflater mLayoutInflater;
        private int mLayout;

        private ItemAdapter(LayoutInflater layoutInflater, List<CameraInfo> cameraInfoList, @LayoutRes int layout) {
            this.mLayoutInflater = layoutInflater;
            this.mCameraInfoList = cameraInfoList;
            this.mLayout = layout;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(mLayout, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            CameraInfo cameraInfo = mCameraInfoList.get(position);
            holder.bind(cameraInfo);
        }

        @Override
        public int getItemCount() {
            return mCameraInfoList.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView cameraNameTextView;
        private TextView cameraInfoTextView;
        private Button buttonLoad, buttonInfo, buttonDelete;
//        private ImageView mImageView;

        private ItemHolder(View view) {
            super(view);

            cameraNameTextView = (TextView) itemView.findViewById(R.id.item_camera_name);
            cameraInfoTextView = (TextView) itemView.findViewById(R.id.item_camera_info);
            buttonLoad = (Button) itemView.findViewById(R.id.item_camera_buttonLoad);
            buttonInfo = (Button) itemView.findViewById(R.id.item_camera_buttonInfo);
            buttonDelete = (Button) itemView.findViewById(R.id.item_camera_buttonDelete);
//            mImageView = (ImageView) itemView.findViewById(R.id.list_item_imageView);

            buttonLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CameraInfo cameraInfo = mCameraInfoList.get(getAdapterPosition());
                    Log.d(TAG, "onClick: value is " + cameraInfo.getName());

                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    final boolean connectedToInternet = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

                    if(connectedToInternet) {

                        Log.d(TAG, "onClickloadcambutt: url is " + cameraInfo.getUrl());
                        cameraListButtonClickListener.onClickListLoadCamera(cameraInfo.getUrl(), cameraInfo.getName().replace(" ", "_"));

//                        if(currentDevice.isConnected()) {
////                            if(!mDbHelper.checkForCamera(cameraInfo)) {
////                                mDbHelper.addCamera(cameraInfo);
////                            } else {
////                                Toast.makeText(getContext(), "Camera exists!", Toast.LENGTH_SHORT).show();
////                            }
////                            Log.d(TAG, "onClick: mUrl being sent is " + mUrl);
////                            signInListener.onClickLoadCamera(mUrl);
//                        } else {
////                            cameraInfo = new CameraInfo();
//
//                            buttonLoad.setEnabled(false);
//                            String nickname = cameraInfo.getName();
//                            String IP = cameraInfo.getIpAddress();
//                            if(cameraInfo.getRtspPort() != null) {
//                                IP += ":" + cameraInfo.getRtspPort();
////                            cameraInfo.setRtspPort(edittextRTSP.getText().toString());
//                            }
//                            String username = cameraInfo.getUsername();
//                            String password = cameraInfo.getPassword();
//
////                            //Make cameraInfo object
////                            cameraInfo.setName(nickname);
////                            cameraInfo.setIpAddress(IP);
////                            if(!edittextRTSP.getText().toString().isEmpty()) {
////                                cameraInfo.setRtspPort(edittextRTSP.getText().toString());
////                            }
////                            cameraInfo.setUsername(username);
////                            cameraInfo.setPassword(password);
//
//                            if(!IP.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
////                                loadProgress.setVisibility(View.VISIBLE);
//                                currentDevice = new OnvifDevice(IP, username, password);
//                                currentDevice.setListener(HomeFragment.this);
//                                currentDevice.getServices();
//                            } else {
//                                Toast.makeText(getContext(), "Please input a IP Address, username, and password.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
                    } else {
                        Toast.makeText(getContext(), "Please connect to the internet to stream a camera", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            buttonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CameraInfo cameraInfo = mCameraInfoList.get(getAdapterPosition());
                    selectedCameraInfo = cameraInfo;
//                    SignInFragment signInFragment = new SignInFragment();
//                    Bundle args = new Bundle();
//                    args.putInt("id", selectedCameraInfo.getId());
//                    args.putString("nickname", selectedCameraInfo.getName());
//                    args.putString("ipAddress",selectedCameraInfo.getIpAddress());
//                    args.putString("username", selectedCameraInfo.getUsername());
//                    args.putString("password", selectedCameraInfo.getPassword());
//                    signInFragment.setArguments(args);
//
//                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_container, signInFragment);
//                    fragmentTransaction.addToBackStack("");
//                    fragmentTransaction.commit();

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Device Information: " + selectedCameraInfo.getName());
                    alertDialog.setMessage(selectedCameraInfo.getDeviceInfo());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.show();

                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CameraInfo cameraInfo = mCameraInfoList.get(getAdapterPosition());
                    selectedCameraInfo = cameraInfo;
//                    DeleteCameraDialogFragment dialogFragment = new DeleteCameraDialogFragment();
//                    dialogFragment.show(getFragmentManager(), "Test");

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Delete " + cameraInfo.getName() + "?");
                    alertDialog.setMessage("Are you sure you want to delete " + cameraInfo.getName() + "?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    mDbHelper.deleteCamera(cameraInfo);
//                                    mCameraInfoList = mDbHelper.getAllCameras();
                                    mCameraInfoList.remove(getAdapterPosition());
                                    mItemAdapter.notifyDataSetChanged();
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
        }

        public void bind(CameraInfo cameraInfo) {
            cameraNameTextView.setText(cameraInfo.getName());
            cameraInfoTextView.setText("IP: " + cameraInfo.getIpAddress());
        }
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
//            cameraListButtonClickListener.onClickListLoadCamera(mUrl, );

//            buttonLoadCamera.setEnabled(true);
//            buttonLoadCamera.setText("Play Camera");
//            loadProgress.setVisibility(View.INVISIBLE);
        }
    }
}
