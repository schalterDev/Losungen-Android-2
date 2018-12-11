package schalter.de.losungen2.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import schalter.de.losungen2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WidgetsOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WidgetsOverviewFragment extends Fragment {


    public WidgetsOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment WidgetsOverviewFragment.
     */
    public static WidgetsOverviewFragment newInstance() {
        return new WidgetsOverviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_widgets_overview, container, false);
    }

}
