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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

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

public class AddItemActivity extends AppCompatActivity {

    @BindView(R.id.addItemItemImageView) ImageView itemImageView;
    @BindView(R.id.addItemItemNameEditText) TextInputEditText itemNameEditText;
    @BindView(R.id.addItemItemCostEditText) TextInputEditText itemCostEditText;
    @BindView(R.id.addItemItemVegRadioButton) RadioButton itemVegRadioButton;
    @BindView(R.id.addItemNonVegRadioButton) RadioButton itemNonVegRadioButton;
    @BindView(R.id.addItemCategoriesSpinner) Spinner itemCategoriesSpinner;
    @BindColor(R.color.colorAccent) int colorAccent;

    private Unbinder unbinder;
    private String categoryName;
    private MaterialDialog materialDialog;
    private RequestQueue requestQueue;
    private String itemImageUrl;
    private Bitmap itemImageBitmap;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private final int RC_CAMERA_INTENT = 454;
    private String baseServerUrl;
    private ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        unbinder = ButterKnife.bind(AddItemActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        Glide.with(getApplicationContext()).load(R.drawable.logo_grey).into(itemImageView);
        initializeSpinner();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initializeFirebase();
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Admins/"+firebaseAuth.getCurrentUser().getUid()+"/Items");
        baseServerUrl = getString(R.string.BASE_SERVER_URL)+"/items";
    }

    private void initializeSpinner() {
        String[] spinnerArray = getResources().getStringArray(R.array.categories);
        spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategoriesSpinner.setAdapter(spinnerArrayAdapter);
        itemCategoriesSpinner.setSelection(spinnerArrayAdapter.getPosition(categoryName));
    }

    private void notifyMessage(String message) {
        if (materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(itemImageView, message, Snackbar.LENGTH_SHORT)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { }
                }).setActionTextColor(colorAccent)
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) startActivityForResult(takePictureIntent, RC_CAMERA_INTENT);
        else notifyMessage("No App to handle this action");
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

    @OnClick(R.id.addItemItemImageView)
    public void onItemImageViewPressed() {
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.addItemUpdateMenuButton)
    public void onUpdateMenuButtonPressed() {
        String itemName = itemNameEditText.getText().toString();
        String itemCost = itemCostEditText.getText().toString();
        boolean itemVeg = itemVegRadioButton.isChecked();
        String itemCategory = itemCategoriesSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemCost) || TextUtils.isEmpty(itemCategory)) notifyMessage("Enter valid item details");
        else {
            materialDialog = new MaterialDialog.Builder(AddItemActivity.this)
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

    private void pushItemToDatabase(Item item) {
        try {
            String requestUrl = baseServerUrl+"?uid="+firebaseAuth.getCurrentUser().getUid();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", item.getItemName());
            jsonObject.put("cost", item.getItemCost());
            jsonObject.put("category", item.getItemCategory());
            jsonObject.put("image_url", item.getItemImageUrl());
            jsonObject.put("veg", item.isItemVeg());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    notifyMessage("Menu Updated.");
                    clearFields();
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error!=null && error.getMessage()!=null) notifyMessage(error.getMessage());
                    else notifyMessage(error.toString());
                    Log.e("ADD_ITEM_VOLLEY_ERR", error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ADD_ITEM_PUSH_ITEM", e.getMessage());
        }
    }

    private void clearFields() {
        Glide.with(getApplicationContext()).load(R.drawable.logo_grey).into(itemImageView);
        itemImageBitmap = null;
        itemImageUrl = null;
        itemNameEditText.setText("");
        itemCostEditText.setText("");
        itemVegRadioButton.setChecked(true);
        itemNonVegRadioButton.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
