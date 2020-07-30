package com.example.leafpiction;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Element versionElement = new Element();
//        versionElement.setTitle("Version 2.0");
//
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
//                "fonts/SourceSansPro-Regular.ttf");
//
//        return new AboutPage(getContext())
//                .isRTL(false)
//                .enableDarkMode(false)
//                .setCustomFont(typeface)
//                .setDescription(getString(R.string.appDesc))
//                .setImage(R.drawable.logo_medium)
//                .addItem(versionElement)
//                .addGroup("Connect with us")
//                .addEmail("angelika.justine.8@gmail.com")
//                .addPlayStore("com.ideashower.readitlater.pro")
//                .addGitHub("AngelikaJustine")
//                .addInstagram("universitasmachung")
//                .create();

        return inflater.inflate(R.layout.fragment_about, container, false);

    }
}
