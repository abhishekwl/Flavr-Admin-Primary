package io.github.abhishekwl.flavradminprimary.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.abhishekwl.flavradminprimary.Activities.EditItemActivity;
import io.github.abhishekwl.flavradminprimary.Models.Item;
import io.github.abhishekwl.flavradminprimary.R;

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<Item> itemArrayList;
    private LayoutInflater layoutInflater;
    private Typeface futuraTypeface;

    public ItemsRecyclerViewAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList==null? new ArrayList<Item>() : itemArrayList;
        this.layoutInflater = LayoutInflater.from(context);
        this.futuraTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/futura.ttf");
    }

    @NonNull
    @Override
    public ItemsRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.viewitems_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsRecyclerViewAdapter.ItemViewHolder holder, final int position) {
        final Item item = itemArrayList.get(position);

        if (TextUtils.isEmpty(item.getItemImageUrl())) Glide.with(holder.itemView.getContext()).load(R.drawable.logo_grey).into(holder.itemImageView);
        else Glide.with(holder.itemView.getContext()).load(item.getItemImageUrl()).into(holder.itemImageView);
        holder.itemNameTextView.setText(item.getItemName());
        holder.itemNameTextView.setTypeface(futuraTypeface);
        holder.itemCostTextView.setText("\u20b9 "+item.getItemCost());
        holder.itemVegTextView.setText(item.isItemVeg()? "VEG": "NON\nVEG");
        if (!item.isItemVeg()) holder.itemVegTextView.setTextColor(holder.colorNonVeg);
        holder.itemVegTextView.setTypeface(futuraTypeface);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editItemIntent = new Intent(context, EditItemActivity.class);
                editItemIntent.putExtra("ITEM_CATEGORY", item.getItemCategory());
                editItemIntent.putExtra("ITEM_NAME", item.getItemName());
                editItemIntent.putExtra("ITEM_COST", item.getItemCost());
                editItemIntent.putExtra("ITEM_VEG", item.isItemVeg());
                editItemIntent.putExtra("ITEM_IMAGE_URL", item.getItemImageUrl());
                context.startActivity(editItemIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemsListItemImageView) ImageView itemImageView;
        @BindView(R.id.itemsListItemItemName) TextView itemNameTextView;
        @BindView(R.id.itemsListItemItemCost) TextView itemCostTextView;
        @BindView(R.id.itemsListItemItemVeg) TextView itemVegTextView;
        @BindColor(R.color.colorVeg) int colorVeg;
        @BindColor(R.color.colorNonVeg) int colorNonVeg;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
