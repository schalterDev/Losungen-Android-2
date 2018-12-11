package schalter.de.losungen2.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import schalter.de.losungen2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteVersesOverviewFragment extends Fragment {


    public FavouriteVersesOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment FavouriteVersesOverviewFragment.
     */
    public static FavouriteVersesOverviewFragment newInstance() {
        return new FavouriteVersesOverviewFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_verses_overview, container, false);
    }

}
