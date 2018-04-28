package io.github.abhishekwl.flavradminprimary.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.Activities.AddItemActivity;
import io.github.abhishekwl.flavradminprimary.Adapters.CategoriesRecyclerViewAdapter;
import io.github.abhishekwl.flavradminprimary.Helpers.SimpleDividerItemDecoration;
import io.github.abhishekwl.flavradminprimary.Models.Category;
import io.github.abhishekwl.flavradminprimary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    @BindView(R.id.menuFragmentRecyclerView) RecyclerView categoriesRecyclerView;
    @BindView(R.id.menuFragmentSwipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private Unbinder unbinder;
    private View rootView;
    private CategoriesRecyclerViewAdapter categoriesRecyclerViewAdapter;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private String[] categoryNames;
    private String[] categoryImages;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initializeViews();
        return rootView;
    }

    private void initializeViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(rootView.getContext());
        initializeSwipeRefreshLayout();
        initializeRecyclerView();
    }

    private void initializeSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performNetworkRequest();
            }
        });
    }

    private void initializeRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        categoriesRecyclerView.setHasFixedSize(true);
        categoriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoriesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(rootView.getContext()));
        categoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(rootView.getContext(), categoryArrayList);
        categoriesRecyclerView.setAdapter(categoriesRecyclerViewAdapter);
        addItemsToCategoryList();
        performNetworkRequest();
    }

    private void performNetworkRequest() {
        String requestUrl = getString(R.string.BASE_SERVER_URL)+"/items?uid="+firebaseAuth.getCurrentUser().getUid();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response!=null) processNetworkResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error!=null && error.getMessage()!=null) {
                    Snackbar.make(categoriesRecyclerView, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void processNetworkResponse(JSONObject response) {
        Iterator<String> stringIterator = response.keys();
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            try {
                JSONArray jsonArray = response.getJSONArray(key);
                for (Category category: categoryArrayList) {
                    if (category.getCategoryName().equalsIgnoreCase(key)) {
                        category.setCategoryCount(jsonArray.length());
                        break;
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                categoriesRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addItemsToCategoryList() {
        categoryArrayList.clear();
        categoryNames = getResources().getStringArray(R.array.categories);
        categoryImages = getResources().getStringArray(R.array.category_images);
        for (int i=0; i<categoryNames.length; i++) {
            categoryArrayList.add(new Category(categoryNames[i], categoryImages[i]));
        }
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.menuFragmentAddItemButton)
    public void onAddNewItemButtonPressed() {
        Intent addNewItemIntent = new Intent(getActivity(), AddItemActivity.class);
        addNewItemIntent.putExtra("CATEGORY_NAME", "Burgers");
        addNewItemIntent.putExtra("ACTION_TYPE", "ADD");
        startActivity(addNewItemIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
