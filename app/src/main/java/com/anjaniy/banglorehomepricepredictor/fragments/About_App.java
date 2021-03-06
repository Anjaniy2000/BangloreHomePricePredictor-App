package com.anjaniy.banglorehomepricepredictor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anjaniy.banglorehomepricepredictor.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About_App extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (inflater.inflate(R.layout.fragment_about_app,container,false));

        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");

        return new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.mipmap.logo)
                .setDescription(getString(R.string.about_app))
                .addItem(versionElement)
                .addEmail("anjaniy01salekar@gmail.com")
//                .addPlayStore("com.ideashower.readitlater.pro")
                .addGitHub("Anjaniy2000/BangloreHomePricePredictor-App")
                .create();
    }
}
