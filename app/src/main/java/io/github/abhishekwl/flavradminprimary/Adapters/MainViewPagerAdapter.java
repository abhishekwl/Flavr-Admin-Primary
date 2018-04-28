package io.github.abhishekwl.flavradminprimary.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.github.abhishekwl.flavradminprimary.Fragments.AnalyticsFragment;
import io.github.abhishekwl.flavradminprimary.Fragments.MenuFragment;
import io.github.abhishekwl.flavradminprimary.Fragments.OrdersFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new OrdersFragment();
            case 1: return new MenuFragment();
            default: return new AnalyticsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Orders";
            case 1: return "Menu";
            default: return "Analytics";
        }
    }
}
