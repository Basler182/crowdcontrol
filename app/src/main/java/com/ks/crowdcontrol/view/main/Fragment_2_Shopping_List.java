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

/**
 * Class holding the Shopping List Fragment and handling the input.
 */
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

        //Loads the Shopping List from the shared preferences and adds it to the the adapter to display it
        final ArrayList<String> stringArrayList = ShoppingListDAO.load(getContext());
        for(String temp : stringArrayList) {
            mAdapter.add(temp);
        }
        mAdapter.notifyDataSetChanged();
        //Adds the Text of the EditText Field to the Adapter and afterwards makes the EditTextView empty and notify the Adapter that Data has changed.
        btnAdd.setOnClickListener(v -> {
            String item = mItemEdit.getText().toString();
            mAdapter.add(item);
            stringArrayList.add(item);
            mAdapter.notifyDataSetChanged();
            mItemEdit.setText("");
            ShoppingListDAO.save(Objects.requireNonNull(getContext()), stringArrayList);
        });
        //Returns back to Home
        btnNavFrag1.setOnClickListener(view1 -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).setViewPager(0);
        });
        //Adds a Listener to remove the items from the list again
        mShoppingList.setOnItemClickListener((parent, view12, position, id) -> {
            mAdapter.remove(mAdapter.getItem(position));
            stringArrayList.remove(position);
            mAdapter.notifyDataSetChanged();
            ShoppingListDAO.save(Objects.requireNonNull(getContext()), stringArrayList);
        });
        return view;
    }
}
