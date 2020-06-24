package com.ks.crowdcontrol;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ks.crowdcontrol.database.SupermarketDTO;
import com.ks.crowdcontrol.view.main.Fragment_0_Home;
import com.ks.crowdcontrol.view.main.Fragment_1_Map;
import com.ks.crowdcontrol.view.main.Fragment_2_Shopping_List;
import com.ks.crowdcontrol.view.main.Fragment_3_Details;
import com.ks.crowdcontrol.view.main.adapter.SectionsStatePagerAdapter;

import lombok.Data;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static FirebaseAuth mAuth;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    private SupermarketDTO currentSupermarketDTO = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started.");

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        //setup the pager
        setupViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("test@test.de", "123456");

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment_0_Home(), "Home");
        adapter.addFragment(new Fragment_1_Map(), "Map");
        adapter.addFragment(new Fragment_2_Shopping_List(), "Shopping List");
        adapter.addFragment(new Fragment_3_Details(), "Details");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public void setViewPager(int fragmentNumber, SupermarketDTO supermarket){
        setSupermarketDetailsID(supermarket);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public SupermarketDTO getSupermarketDetailsID() {
        return currentSupermarketDTO;
    }

    public void setSupermarketDetailsID(SupermarketDTO supermarket){
        currentSupermarketDTO = supermarket;
    }
}