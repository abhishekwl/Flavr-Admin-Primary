package io.github.abhishekwl.flavradminprimary.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.Adapters.MainViewPagerAdapter;
import io.github.abhishekwl.flavradminprimary.Models.Hotel;
import io.github.abhishekwl.flavradminprimary.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.mainToolbar) Toolbar mainToolbar;
    @BindView(R.id.mainViewPager) ViewPager mainViewPager;
    @BindView(R.id.mainTabLayout) TabLayout mainTabLayout;
    @BindColor(R.color.colorPrimaryDark) int colorPrimaryDark;

    private Unbinder unbinder;
    private View headerLayout;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location deviceLocation;
    private MaterialDialog materialDialog;
    private String requestUrl;
    private Hotel hotel;
    private FirebaseStorage firebaseStorage;
    private final int PICK_IMAGE_REQUEST = 987;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initializeNavigationView();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            }
        });
        requestUrl = getString(R.string.BASE_SERVER_URL)+"/hotels?uid="+firebaseAuth.getCurrentUser().getUid();
        performNetworkRequest();
        selectPage(1);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                deviceLocation = location;
            }
        });
    }

    private void performNetworkRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response!=null) processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error!=null && error.getMessage()!=null) {
                    Snackbar.make(mainTabLayout, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    Log.v("MAIN_ACT_VOLLEY", error.getMessage());
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void processResponse(JSONObject response) {
        try {
            String placeImageUrl = response.getString("image_url");
            String placeName = response.getString("name");
            String placeEmailId = response.getString("email_id");
            String placeContactNumber = response.getString("contact_number");
            double placeLatitude = response.getDouble("latitude");
            double placeLongitude = response.getDouble("longitude");
            double placeAltitude = response.getDouble("altitude");
            int placeRange = response.getInt("range");

            hotel = new Hotel(placeName, placeEmailId, placeImageUrl, placeContactNumber, placeLatitude, placeLongitude, placeAltitude, placeRange);
            setupHeaderLayout(hotel);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeNavigationView() {
        setSupportActionBar(mainToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        mainViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager);
    }

    private void setupHeaderLayout(Hotel hotel) {
        ImageView appLogoImageView = headerLayout.findViewById(R.id.navHeaderLogoImageView);
        ImageView placeImageView = headerLayout.findViewById(R.id.navHeaderBackgroundImageView);
        TextView placeNameTextView = headerLayout.findViewById(R.id.navHeaderPlaceNameTextView);
        TextView placeEmailAddressTextView = headerLayout.findViewById(R.id.navHeaderEmailIdTextView);

        Glide.with(getApplicationContext()).load(R.drawable.logo_white).into(appLogoImageView);
        Glide.with(getApplicationContext()).load(hotel.getHotelImageUrl()).into(placeImageView);
        placeNameTextView.setText(hotel.getHotelName());
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
        switch (id) {
            case R.id.nav_reposition:
                repositionHotel();
                break;
            case R.id.nav_edit_place_name:
                renamePlace();
                break;
            case R.id.nav_edit_range:
                editRange();
                break;
            case R.id.nav_camera:
                dispatchGalleryIntent();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dispatchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a new picture for your place :)"), PICK_IMAGE_REQUEST);
    }

    private void renamePlace() {
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Flavr (Admin)")
                .content("Enter a new name for your place :)")
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input("Place Name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", input.toString());
                            performUpdateRequest(jsonObject);

                        } catch (Exception ex) {
                            if (ex.getMessage()!=null) Snackbar.make(mainTabLayout, ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .negativeText("CANCEL")
                .positiveColor(colorPrimaryDark)
                .negativeColor(colorPrimaryDark)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void editRange() {
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Flavr (Admin)")
                .content("Enter new range of visibility (Meters)")
                .iconRes(R.drawable.logo_small)
                .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input("New Range", Integer.toString(hotel.getHotelRange()), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("range", input.toString());
                            performUpdateRequest(jsonObject);

                        } catch (Exception ex) {
                            if (ex.getMessage()!=null) Snackbar.make(headerLayout, ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .negativeText("CANCEL")
                .positiveColor(colorPrimaryDark)
                .negativeColor(colorPrimaryDark)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void performUpdateRequest(JSONObject jsonObject) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();

        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Flavr (Admin)")
                .content("Updating place details...")
                .iconRes(R.drawable.logo_small)
                .progress(true, 0)
                .show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    materialDialog.dismiss();

                    if(response!=null && response.getString("_id")!=null) {
                        Snackbar.make(headerLayout, "Place details updated successfully.", Snackbar.LENGTH_SHORT).show();

                        Log.v("RESPONSE", response.toString());

                        String placeName = response.getString("name");
                        String placeEmailId = response.getString("email_id");
                        String placeContactNumber = response.getString("contact_number");
                        String placeImageUrl = response.getString("image_url");
                        double placeLatitude = response.getDouble("latitude");
                        double placeLongitude = response.getDouble("longitude");
                        double placeAltitude = response.getDouble("altitude");
                        int placeRange = response.getInt("range");

                        hotel = new Hotel(placeName, placeEmailId, placeImageUrl, placeContactNumber, placeLatitude, placeLongitude, placeAltitude, placeRange);
                        setupHeaderLayout(hotel);
                    }

                } catch (Exception error) {
                    if (error.getMessage()!=null) Snackbar.make(headerLayout, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    Log.v("MAIN_ACT_REPOSITION", error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                materialDialog.dismiss();
                if (error.getMessage()!=null) Snackbar.make(headerLayout, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                Log.v("MAIN_ACT_REPOSITION", error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void repositionHotel() {
        if (deviceLocation!=null && firebaseAuth.getCurrentUser()!=null) {
            try {
                final double latitude = deviceLocation.getLatitude();
                final double longitude = deviceLocation.getLongitude();
                double altitude = deviceLocation.getAltitude();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("altitude", altitude);
                //Log.v("LOC_ACC", String.valueOf(deviceLocation.getAccuracy()));
                performUpdateRequest(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else  Snackbar.make(headerLayout, "Please enable location services.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            uploadUriToFirebase(uri);
        }
    }

    private void uploadUriToFirebase(Uri uri) {
        materialDialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Flavr (Admin)")
                .content("Updating place profile picture")
                .iconRes(R.drawable.logo_small)
                .progress(true, 0)
                .show();

        StorageReference storageReference = firebaseStorage.getReference("hotels/"+firebaseAuth.getCurrentUser().getUid()).child("profile_picture.jpg");
        StorageMetadata storageMetadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        storageReference.putFile(uri, storageMetadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("image_url", taskSnapshot.getDownloadUrl().toString());
                    performUpdateRequest(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (materialDialog.isShowing()) materialDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
        performNetworkRequest();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
