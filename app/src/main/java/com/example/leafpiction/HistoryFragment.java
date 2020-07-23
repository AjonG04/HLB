package com.example.leafpiction;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leafpiction.Model.DataModel;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

//    private RecyclerView rvHeroes;
    int list_number;
    private List<DataModel> list = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HistoryDatabaseCRUD dbHandler = new HistoryDatabaseCRUD();
        Activity activity = getActivity();
        Context context = activity.getApplicationContext();
        list = dbHandler.getAllRecords(context);
        list_number = list.size();

//        list.addAll(TrialData.getListData());
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        final FragmentActivity c = getActivity();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final CardViewAdapter adapter = new CardViewAdapter(list);
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();

        final ConstraintLayout cl_no_history = (ConstraintLayout)view.findViewById(R.id.cl_no_history);

        if(list_number == 0){
            cl_no_history.setVisibility(View.VISIBLE);
        }

        return view;
//        return inflater.inflate(R.layout.fragment_history, container, false);
    }

}
