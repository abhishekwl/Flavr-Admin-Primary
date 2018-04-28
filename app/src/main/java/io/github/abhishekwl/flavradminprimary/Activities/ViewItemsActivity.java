package io.github.abhishekwl.flavradminprimary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.Adapters.ItemsRecyclerViewAdapter;
import io.github.abhishekwl.flavradminprimary.Helpers.SimpleDividerItemDecoration;
import io.github.abhishekwl.flavradminprimary.Models.Item;
import io.github.abhishekwl.flavradminprimary.R;

public class ViewItemsActivity extends AppCompatActivity {

    @BindView(R.id.viewItemsRecyclerView) RecyclerView itemsRecyclerView;
    @BindView(R.id.viewItemsSwipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindColor(R.color.colorAccent) int colorAccent;

    private Unbinder unbinder;
    private String categoryName;
    private ArrayList<Item> itemArrayList = new ArrayList<>();
    private ItemsRecyclerViewAdapter itemsRecyclerViewAdapter;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        unbinder = ButterKnife.bind(ViewItemsActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        initializeActionBar();
        initializeRecyclerView();
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        performNetworkRequest();
    }

    private void performNetworkRequest() {
        try {
            itemArrayList.clear();
            itemsRecyclerViewAdapter.notifyDataSetChanged();
            String requestUrl = getString(R.string.BASE_SERVER_URL)+"/items?uid="+firebaseAuth.getCurrentUser().getUid()+"&category="+categoryName;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    processResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(itemsRecyclerView, "Failed to fetch items from menu.", Snackbar.LENGTH_SHORT)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    performNetworkRequest();
                                }
                            }).setActionTextColor(colorAccent)
                            .show();
                    error.printStackTrace();
                }
            });
            requestQueue.add(jsonArrayRequest);

        } catch (Exception e) {
            notifyMessage(e.getMessage());
            e.printStackTrace();
            Log.e("VIEW_ITEMS_ERR", e.getMessage());
        }
    }

    private void processResponse(JSONArray response) {
        if (response==null || response.length()==0) notifyMessage("This one's empty :(");
        else {
            try {
                itemArrayList.clear();
                for (int i=0; i<response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String itemName = jsonObject.getString("name");
                    String itemImageUrl = jsonObject.getString("image_url");
                    String itemCategory = jsonObject.getString("category");
                    int itemCost = jsonObject.getInt("cost");
                    boolean itemVeg = jsonObject.getBoolean("veg");
                    itemArrayList.add(new Item(itemName, itemCategory, itemCost, itemImageUrl, itemVeg));
                }
                itemsRecyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                notifyMessage(e.getMessage());
            }
        }
    }

    private void notifyMessage(String message) {
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        Snackbar.make(itemsRecyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void initializeRecyclerView() {
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemsRecyclerView.setHasFixedSize(true);
        itemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        itemsRecyclerViewAdapter = new ItemsRecyclerViewAdapter(getApplicationContext(), itemArrayList);
        itemsRecyclerView.setAdapter(itemsRecyclerViewAdapter);
    }

    private void initializeActionBar() {
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        Objects.requireNonNull(getSupportActionBar()).setTitle(categoryName);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performNetworkRequest();
            }
        });
    }

    @OnClick(R.id.viewItemsAddNewItemButton)
    public void onAddNewItemButtonPressed() {
        Intent addNewItemIntent = new Intent(ViewItemsActivity.this, AddItemActivity.class);
        addNewItemIntent.putExtra("CATEGORY_NAME", categoryName);
        addNewItemIntent.putExtra("ACTION_TYPE", "ADD");
        startActivity(addNewItemIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeViews();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
