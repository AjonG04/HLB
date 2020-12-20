package com.example.leafpiction.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leafpiction.R;

public class BottomCameraFragment extends Fragment {
    private static TextView kloro, karo, anto;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_bottom__camera, container, false);
        final FragmentActivity c = getActivity();
        kloro = (view.findViewById(R.id.textkloro));
        karo = (view.findViewById(R.id.textkaro));
        anto = (view.findViewById(R.id.textanto));

        return view;
    }

    public void setCloro(String cloro){
        kloro.setText(cloro);
    }

    public void setCaro(String caro){
        karo.setText(caro);
    }

    public void setAnto(String antos){
        anto.setText(antos);
    }

}