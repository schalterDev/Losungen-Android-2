package schalter.de.losungen2.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import schalter.de.losungen2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthlyVersesOverviewFragment extends Fragment {


    public MonthlyVersesOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment MonthlyVersesOverviewFragment.
     */
    public static MonthlyVersesOverviewFragment newInstance() {
        return new MonthlyVersesOverviewFragment();
    }

    /**
     * Updates the verses showed to the given date
     * @param date show the verses for this date
     */
    public void setDateToShow(Date date) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_verses_overview, container, false);
    }

}
