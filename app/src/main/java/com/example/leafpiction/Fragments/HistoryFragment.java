package com.example.leafpiction.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.leafpiction.CardViewAdapter;
import com.example.leafpiction.Model.DataModel;
import com.example.leafpiction.R;
import com.example.leafpiction.Util.HistoryDatabaseCRUD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment  {

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

        SharedPreferences pref = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        Boolean defaultOrder = true;
        if(pref != null) {
            defaultOrder = pref.getBoolean("defaultOrder", true);
        }

        if (defaultOrder){
            Collections.reverse(list);
        }

        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        final FragmentActivity c = getActivity();
        final RecyclerView recyclerView = view.findViewById(R.id.rv_data);
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

        final Button btn_latest = view.findViewById(R.id.btn_latest);
        final Button btn_oldest = view.findViewById(R.id.btn_oldest);

        ColorStateList colorPrimary = ContextCompat.getColorStateList(context, R.color.colorPrimary);
        ColorStateList colorWhite = ContextCompat.getColorStateList(context, R.color.white);

        if(defaultOrder){
            btn_latest.setClickable(false);
            btn_latest.setBackgroundTintList(colorPrimary);
            btn_latest.setTextColor(colorWhite);

            btn_oldest.setClickable(true);
            btn_oldest.setBackgroundTintList(colorWhite);
            btn_oldest.setTextColor(colorPrimary);
        }
        else {
            btn_latest.setClickable(true);
            btn_latest.setBackgroundTintList(colorWhite);
            btn_latest.setTextColor(colorPrimary);

            btn_oldest.setClickable(false);
            btn_oldest.setBackgroundTintList(colorPrimary);
            btn_oldest.setTextColor(colorWhite);
        }

        final ConstraintLayout cl_no_history = view.findViewById(R.id.cl_no_history);
        final LinearLayout ll_sort = view.findViewById(R.id.ll_sort);

        if(list_number == 0){
            cl_no_history.setVisibility(View.VISIBLE);
            ll_sort.setVisibility(View.GONE);
        }

        return view;
//        return inflater.inflate(R.layout.fragment_history, container, false);
    }



}
