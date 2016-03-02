package com.example.o_x.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.o_x.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WczytywanieFragment extends Fragment {


    public WczytywanieFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wczytywanie, container, false);
    }


}
