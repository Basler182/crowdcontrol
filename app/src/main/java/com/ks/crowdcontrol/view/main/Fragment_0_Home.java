package com.ks.crowdcontrol.view.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ks.crowdcontrol.MainActivity;
import com.ks.crowdcontrol.R;

import java.util.Objects;

/**
 * Home Fragment. This class handles the Start Page.
 */
public class Fragment_0_Home extends Fragment {
    private static final String TAG = "Fragment_Home";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        //Shows the View
        View view  = inflater.inflate(R.layout.fragment_0_home, container, false);
        //Creates the buttons to swap to the different pages.
        Button btnNavFrag1 = (Button) view.findViewById(R.id.btnNavFrag1);
        Button btnNavFrag2 = (Button) view.findViewById(R.id.btnNavFrag2);
        Button btnNavFrag3 = (Button) view.findViewById(R.id.btnNavFrag3);
        Log.d(TAG, "onCreateView: started.");

        btnNavFrag1.setOnClickListener(view1 -> {
            Toast.makeText(getActivity(), "Going to Map", Toast.LENGTH_SHORT).show();

            ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(1);
        });

        btnNavFrag2.setOnClickListener(view12 -> {
            Toast.makeText(getActivity(), "Going to Shopping List", Toast.LENGTH_SHORT).show();
            ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(2);
        });
        //Creates a Dialog to show the information about the project
        btnNavFrag3.setOnClickListener(view13 -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            alertDialogBuilder.setMessage(R.string.information_dialog_text);
            alertDialogBuilder.setPositiveButton(R.string.information_dialog_positive_button,
                    (arg0, arg1) -> {
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
        return view;
    }
}