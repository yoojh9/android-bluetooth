package com.example.android.bluetooth_prototype.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bluetooth_prototype.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private TextView tvCurrentTemp;
    private TextView tvMode;
    private TextView tvRefrigeratorTemp;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        tvCurrentTemp = (TextView)view.findViewById(R.id.tvCurrentTemp);
        tvMode = (TextView)view.findViewById(R.id.tvMode);
        tvRefrigeratorTemp = (TextView)view.findViewById(R.id.tvRefrigeratorTemp);
        return view;
    }

    public void setData(String mode, double currentTempVal, double refTempVal){
        if(tvCurrentTemp==null && tvMode == null || tvRefrigeratorTemp == null) return;
        tvCurrentTemp.setText(String.valueOf(currentTempVal));
        tvMode.setText(mode);
        tvRefrigeratorTemp.setText(String.valueOf(refTempVal));
    }
}