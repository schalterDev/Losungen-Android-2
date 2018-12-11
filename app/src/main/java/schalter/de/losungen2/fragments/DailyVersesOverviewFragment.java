package schalter.de.losungen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import schalter.de.losungen2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyVersesOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyVersesOverviewFragment extends Fragment {

    public DailyVersesOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment DailyVersesOverviewFragment.
     */
    public static DailyVersesOverviewFragment newInstance() {
        return new DailyVersesOverviewFragment();
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
        return inflater.inflate(R.layout.fragment_daily_verses_overview, container, false);
    }
}
