package com.blkxltng.caip.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blkxltng.caip.CameraInfo;
import com.blkxltng.caip.R;
import com.blkxltng.caip.database.CameraReaderDbHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public interface AddCameraListener {
        void onClickAddCamera();
    }

    private static final String TAG = "HomeFragment";

    private AddCameraListener listener;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CameraReaderDbHelper mDbHelper;
    private List<CameraInfo> mCameraInfoList = new ArrayList<CameraInfo>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (AddCameraListener) getActivity();
        mDbHelper = new CameraReaderDbHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mCameraInfoList = mDbHelper.getAllCameras();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_home);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(new ItemAdapter(getLayoutInflater(), mCameraInfoList, R.layout.item_camera));

        FloatingActionButton addFab = view.findViewById(R.id.fab_add);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickAddCamera();
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
//        private ImageView mImageView;

        private ItemHolder(View view) {
            super(view);

            cameraNameTextView = (TextView) itemView.findViewById(R.id.item_camera_name);
            cameraInfoTextView = (TextView) itemView.findViewById(R.id.item_camera_info);
//            mImageView = (ImageView) itemView.findViewById(R.id.list_item_imageView);
        }

        public void bind(CameraInfo cameraInfo) {
            cameraNameTextView.setText(cameraInfo.getName());
            cameraInfoTextView.setText("IP: " + cameraInfo.getIpAddress());
//            Glide.with(getApplicationContext()).load(contact.getImgUrl()).into(mImageView);
        }


    }
}
