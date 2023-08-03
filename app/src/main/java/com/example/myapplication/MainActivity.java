package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the initial fragment to be shown (HomeFragment)
        switchFragment(new HomeFragment());

        // Get reference to the bottom navigation view and set listener for when a menu item is selected
        navbar = findViewById(R.id.nav_bar);
        navbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        switchFragment(new HomeFragment());
                        break;

                    case R.id.nav_market:
                        switchFragment(new MarketFragment());
                        break;

                    case R.id.nav_logbook:
                        switchFragment(new LogbookFragment());
                        break;

                    case R.id.nav_profile:
                        switchFragment(new ProfileFragment());
                        break;
                }

                return true;
            }
        });

        // If the intent has an extra "switch_to_fragment" with a value of "market", switch to the MarketFragment
        if (getIntent().hasExtra("switch_to_fragment")) {
            String fragmentName = getIntent().getStringExtra("switch_to_fragment");
            if (fragmentName.equals("market")) {
                switchFragment(new MarketFragment());
            }
        }

        // If the intent has an extra "switch_to_fragment" with a value of "logbook", switch to the LogbookFragment
        if (getIntent().hasExtra("switch_to_fragment")) {
            String fragmentName = getIntent().getStringExtra("switch_to_fragment");
            if (fragmentName.equals("logbook")) {
                switchFragment(new LogbookFragment());
            }
        }

    }

    // The switchFragment() function will direct the user to different fragments by using the getSupportManager() function.
    public void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity, fragment).commit();
    }
}