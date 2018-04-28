package io.github.abhishekwl.flavradminprimary.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.abhishekwl.flavradminprimary.Activities.ViewItemsActivity;
import io.github.abhishekwl.flavradminprimary.Models.Category;
import io.github.abhishekwl.flavradminprimary.R;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoryViewHolder> {

    private ArrayList<Category> categoryArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public CategoriesRecyclerViewAdapter(Context context, ArrayList<Category> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CategoriesRecyclerViewAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.category_list_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRecyclerViewAdapter.CategoryViewHolder holder, int position) {
        final Category category = categoryArrayList.get(position);

        Glide.with(holder.itemView.getContext()).load(category.getCategoryImageUrl()).into(holder.categoryImageView);
        holder.categoryNameTextView.setText(category.getCategoryName());
        holder.categoryCountTextView.setText(Integer.toString(category.getCategoryCount())+" items");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewItemsIntent = new Intent(context, ViewItemsActivity.class);
                viewItemsIntent.putExtra("CATEGORY_NAME", category.getCategoryName());
                context.startActivity(viewItemsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.categoryListItemImageView) ImageView categoryImageView;
        @BindView(R.id.categoryListItemCategoryNameTextView) TextView categoryNameTextView;
        @BindView(R.id.categoryListItemCountTextView) TextView categoryCountTextView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
