package com.example.androidapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.androidapp.OrderFragment.OrderViewPagerAdapter;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);

        bottomNavigationView = v.findViewById(R.id.bottom_nav_history);
        viewPager = v.findViewById(R.id.history_viewpager);

        HistoryViewPagerAdapter adapter = new HistoryViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.history_all).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.history_completed).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.history_canceled).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.history_all:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.history_completed:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.history_canceled:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        return v;
    }
}
