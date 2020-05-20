package com.ks.crowdcontrol.view.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ks.crowdcontrol.MainActivity;
import com.ks.crowdcontrol.R;
import com.ks.crowdcontrol.persistance.shoppinglist.ShoppingListDAO;

import java.util.ArrayList;
import java.util.Objects;


public class Fragment_2_Shopping_List extends Fragment {
    private static final String TAG = "Shopping_List";

    private ArrayAdapter<String> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2_shopping_list, container, false);
        Button btnNavFrag1 = view.findViewById(R.id.btnNavFrag1);
        Button btnAdd = view.findViewById(R.id.add_button);
        final ListView mShoppingList = view.findViewById(R.id.shopping_listView);
        final EditText mItemEdit = view.findViewById(R.id.item_editText);
        mAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1);
        mShoppingList.setAdapter(mAdapter);
        Log.d(TAG, "onCreateView: started.");

        final ArrayList<String> stringArrayList = ShoppingListDAO.load(getContext());
        for(String temp : stringArrayList) {
            mAdapter.add(temp);
        }
        mAdapter.notifyDataSetChanged();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                mAdapter.add(item);
                stringArrayList.add(item);
                mAdapter.notifyDataSetChanged();
                mItemEdit.setText("");
                ShoppingListDAO.save(Objects.requireNonNull(getContext()), stringArrayList);
            }
        });

        btnNavFrag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Home", Toast.LENGTH_SHORT).show();

                ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(0);
            }
        });

        mShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.remove(mAdapter.getItem(position));
                stringArrayList.remove(position);
                mAdapter.notifyDataSetChanged();
                ShoppingListDAO.save(Objects.requireNonNull(getContext()), stringArrayList);
            }
        });
        return view;
    }
}
