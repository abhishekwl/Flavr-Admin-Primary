package io.github.abhishekwl.flavradminprimary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.Adapters.MainViewPagerAdapter;
import io.github.abhishekwl.flavradminprimary.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.mainViewPager) ViewPager mainViewPager;
    @BindView(R.id.mainTabLayout) TabLayout mainTabLayout;

    private Unbinder unbinder;
    private View headerLayout;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initializeNavigationView();
        //Snackbar.make(mainTabLayout, "Signed in as "+firebaseAuth.getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            }
        });
        selectPage(1);
    }

    private void initializeNavigationView() {
        setSupportActionBar(mainToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        setupHeaderLayout();
        mainViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager);
    }

    private void setupHeaderLayout() {
        ImageView appLogoImageView = headerLayout.findViewById(R.id.navHeaderLogoImageView);
        ImageView placeImageView = headerLayout.findViewById(R.id.navHeaderBackgroundImageView);
        TextView placeNameTextView = headerLayout.findViewById(R.id.navHeaderPlaceNameTextView);
        TextView placeEmailAddressTextView = headerLayout.findViewById(R.id.navHeaderEmailIdTextView);

        Glide.with(getApplicationContext()).load(R.drawable.logo_white).into(appLogoImageView);
        Glide.with(getApplicationContext()).load(getString(R.string.temp_place_image_url)).into(placeImageView); //TODO: Perform Network Request to retrieve place image and place name
        placeNameTextView.setText("Cafe HutTea");
        placeEmailAddressTextView.setText(firebaseAuth.getCurrentUser().getEmail());
    }

    void selectPage(int pageIndex){
        mainTabLayout.setScrollPosition(pageIndex,0f,true);
        mainViewPager.setCurrentItem(pageIndex);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
