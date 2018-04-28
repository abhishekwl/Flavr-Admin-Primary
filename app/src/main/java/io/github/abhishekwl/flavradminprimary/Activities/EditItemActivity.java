package io.github.abhishekwl.flavradminprimary.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.Models.Item;
import io.github.abhishekwl.flavradminprimary.R;

public class EditItemActivity extends AppCompatActivity {

    private final int RC_CAMERA_INTENT = 101;
    @BindView(R.id.editItemItemImageView) ImageView itemImageView;
    @BindView(R.id.editItemItemNameEditText) TextInputEditText itemNameEditText;
    @BindView(R.id.editItemItemCostEditText) TextInputEditText itemCostEditText;
    @BindView(R.id.editItemItemVegRadioButton) RadioButton itemVegRadioButton;
    @BindView(R.id.editItemNonVegRadioButton) RadioButton itemNonVegRadioButton;
    @BindView(R.id.editItemCategoriesSpinner) Spinner itemCategoriesSpinner;
    @BindColor(R.color.colorPrimaryDark) int colorPrimaryDark;

    private Unbinder unbinder;
    private RequestQueue requestQueue;
    private MaterialDialog materialDialog;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String itemCategory, itemName, itemImageUrl;
    private int itemCost;
    private boolean itemVeg;
    private Bitmap itemImageBitmap;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String baseServerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        unbinder = ButterKnife.bind(EditItemActivity.this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initializeViews();
    }

    private void initializeViews() {
        Item item = populateItem();
        renderItem(item);
        initializeFirebase();
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Admins/"+firebaseAuth.getCurrentUser().getUid()+"/Items");
        baseServerUrl = getString(R.string.BASE_SERVER_URL)+"/items";
    }

    private void renderItem(Item item) {
        if (TextUtils.isEmpty(item.getItemImageUrl())) Glide.with(getApplicationContext()).load(R.drawable.logo_grey).into(itemImageView);
        else Glide.with(getApplicationContext()).load(item.getItemImageUrl()).into(itemImageView);
        itemNameEditText.setText(item.getItemName());
        itemCostEditText.setText(Integer.toString(item.getItemCost()));
        itemVegRadioButton.setChecked(item.isItemVeg());
        itemNonVegRadioButton.setChecked(!item.isItemVeg());
        initializeSpinner();
    }

    private Item populateItem() {
        itemName = getIntent().getStringExtra("ITEM_NAME");
        itemCategory = getIntent().getStringExtra("ITEM_CATEGORY");
        itemImageUrl = getIntent().getStringExtra("ITEM_IMAGE_URL");
        itemCost = getIntent().getIntExtra("ITEM_COST", 0);
        itemVeg = getIntent().getBooleanExtra("ITEM_VEG", true);
        return new Item(itemName, itemCategory, itemCost, itemImageUrl, itemVeg);
    }

    private void initializeSpinner() {
        String[] spinnerArray = getResources().getStringArray(R.array.categories);
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategoriesSpinner.setAdapter(spinnerArrayAdapter);
        itemCategoriesSpinner.setSelection(spinnerArrayAdapter.getPosition(itemCategory));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) startActivityForResult(takePictureIntent, RC_CAMERA_INTENT);
        else notifyMessage("No App to handle this action");
    }


    private void notifyMessage(String message) {
        if (materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(itemImageView, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.editItemItemImageView)
    public void onItemImageButtonPressed() {
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CAMERA_INTENT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            itemImageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            Glide.with(getApplicationContext()).load(itemImageBitmap).into(itemImageView);
        }
    }

    private void uploadBitmapToFirebase(Bitmap imageBitmap, final Item item) {
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageReference.child(item.getItemName()+".jpg").putBytes(data, metadata).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyMessage(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                itemImageUrl = taskSnapshot.getDownloadUrl().toString();
                Glide.with(getApplicationContext()).load(itemImageUrl).into(itemImageView);
                item.setItemImageUrl(itemImageUrl);
                pushItemToDatabase(item);
            }
        });
    }

    private void pushItemToDatabase(Item item) {
        try {
            String requestUrl = baseServerUrl+"?uid="+firebaseAuth.getCurrentUser().getUid()+"&category="+item.getItemCategory()+"&name="+item.getItemName();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", item.getItemName());
            jsonObject.put("cost", item.getItemCost());
            jsonObject.put("category", item.getItemCategory());
            jsonObject.put("image_url", item.getItemImageUrl());
            jsonObject.put("veg", item.isItemVeg());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    notifyMessage("Menu Updated.");
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error!=null && error.getMessage()!=null) notifyMessage(error.getMessage());
                    else notifyMessage(error.toString());
                    Log.e("EDIT_ITEM_VOLLEY_ERR", error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EDIT_ITEM_PUSH_ITEM", e.getMessage());
        }
    }

    @OnClick(R.id.editItemUpdateMenuButton)
    public void onUpdateMenuButtonPressed() {
        String itemName = itemNameEditText.getText().toString();
        String itemCost = itemCostEditText.getText().toString();
        boolean itemVeg = itemVegRadioButton.isChecked();
        String itemCategory = itemCategoriesSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemCost) || TextUtils.isEmpty(itemCategory)) notifyMessage("Enter valid item details");
        else {
            materialDialog = new MaterialDialog.Builder(EditItemActivity.this)
                    .title("Flavr (Admin)")
                    .content("Updating Menu...")
                    .iconRes(R.drawable.logo_small)
                    .progress(true, 0)
                    .show();

            Item item = new Item(itemName, itemCategory, Integer.parseInt(itemCost), "", itemVeg);
            if (itemImageBitmap!=null) uploadBitmapToFirebase(itemImageBitmap, item);
            else pushItemToDatabase(item);
        }
    }

    @OnClick(R.id.editItemDeleteItemButton)
    public void onDeleteItemButtonPressed() {
        materialDialog = new MaterialDialog.Builder(EditItemActivity.this)
                .title("Flavr (Admin)")
                .iconRes(R.drawable.logo_small)
                .content("Are you sure you want to delete "+itemName+" from your menu?")
                .positiveText("YES")
                .negativeText("NO")
                .positiveColor(colorPrimaryDark)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteItemFromMenu();
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void deleteItemFromMenu() {
        try {
            String requestUrl = getString(R.string.BASE_SERVER_URL)+"/items?uid="+firebaseAuth.getCurrentUser().getUid()+"&category="+itemCategory+"&name="+itemName;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, requestUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int responseCode = response.getInt("ok");
                        if (responseCode==1 && !TextUtils.isEmpty(itemImageUrl)) {
                            firebaseStorage.getReferenceFromUrl(itemImageUrl).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    notifyMessage("Menu updated.");
                                    finish();
                                }
                            });
                        } else if (responseCode==1) {
                            notifyMessage("Menu updated.");
                            finish();
                        }
                        else notifyMessage("There was an error deleting the item. Please try again :(");
                    } catch (JSONException e) {
                        notifyMessage(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error!=null && error.getMessage()!=null) {
                        notifyMessage(error.getMessage());
                        error.printStackTrace();
                    }
                }
            });
            requestQueue.add(jsonObjectRequest);

        } catch (Exception ex) {
            notifyMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent previousIntent = new Intent(EditItemActivity.this, ViewItemsActivity.class);
        previousIntent.putExtra("CATEGORY_NAME", itemCategory);
        startActivity(previousIntent);
        finish();
    }
}
