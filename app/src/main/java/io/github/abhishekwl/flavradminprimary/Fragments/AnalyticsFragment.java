package io.github.abhishekwl.flavradminprimary.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.abhishekwl.flavradminprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyticsFragment extends Fragment {


    public AnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

}
