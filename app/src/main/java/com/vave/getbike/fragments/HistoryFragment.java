package com.vave.getbike.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vave.getbike.R;
import com.vave.getbike.activity.ShowCompletedRideActivity;
import com.vave.getbike.adapter.RideAdapter2;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.model.Ride;
import com.vave.getbike.syncher.RideSyncher;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView myHistoryRidesListView;
    int tabIndex = 0;
    List<Ride> result = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(int position, String pageTitle) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        if (getArguments() != null) {
            tabIndex = getArguments().getInt(ARG_PARAM1);
        }
        myHistoryRidesListView = (ListView) view.findViewById(R.id.myHistoryRides);
        updateFieldsBasedOnIndex();

        return view;
    }

    private void updateFieldsBasedOnIndex() {
        if (tabIndex == 0) {
            new GetBikeAsyncTask(getContext()) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    result = rideSyncher.getMyCompletedRides();
                }

                @Override
                public void afterPostExecute() {
                    if (result != null) {
                        myHistoryRidesListView.setAdapter(new RideAdapter2(getContext(), result, false));
                    }
                }
            }.execute();

        }
        if (tabIndex == 1) {
            new GetBikeAsyncTask(getContext()) {

                @Override
                public void process() {
                    RideSyncher rideSyncher = new RideSyncher();
                    result = rideSyncher.getRidesGivenByMe();
                }

                @Override
                public void afterPostExecute() {
                    if (result != null) {
                        myHistoryRidesListView.setAdapter(new RideAdapter2(getContext(), result, false));
                    }
                }
            }.execute();
        }
        myHistoryRidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (result != null) {
                    Intent intent = new Intent(getContext(), ShowCompletedRideActivity.class);
                    intent.putExtra("rideId", result.get(position).getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
