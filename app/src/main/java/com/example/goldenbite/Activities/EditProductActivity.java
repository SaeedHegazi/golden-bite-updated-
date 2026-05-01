package com.example.goldenbite.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.goldenbite.Classes.Product;
import com.example.goldenbite.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProductActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_DOC_ID = "product_doc_id";
    Button btnChangePicture, btnUpdate, goBack;

    private String documentId, imageUrl;
    private EditText etName, etPrice, etSize, etDescription;
    private Spinner categorySpinner;
    private String selectedCategory = Product.CATEGORY_HOT_DRINKS;
    private String currentImageUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        goBack = findViewById(R.id.btn_edit_go_back);
        goBack.setOnClickListener(v -> finish());


        documentId = getIntent().getStringExtra(EXTRA_PRODUCT_DOC_ID);

        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "No product selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }






        showEditForm();
    }

    private void showEditForm() {
        setTitle("Edit product");

        etName = findViewById(R.id.edit_name);
        etPrice = findViewById(R.id.edit_price);
        etSize = findViewById(R.id.edit_size);
        etDescription = findViewById(R.id.edit_description);
        categorySpinner = findViewById(R.id.edit_category_spinner);
        btnUpdate = findViewById(R.id.edit_update_product);
        btnChangePicture = findViewById(R.id.edit_choose_picture);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] categories = getResources().getStringArray(R.array.menu_categories);
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        loadProduct();

        btnChangePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);


        });

        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void loadProduct() {
        FirebaseFirestore.getInstance()
                .collection("Product")
                .document(documentId)
                .get()
                .addOnSuccessListener(this, doc -> {
                    if (doc == null || !doc.exists()) {
                        Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    DocumentSnapshot d = doc;
                    String name = d.getString("name");
                    Long priceLong = d.getLong("price");
                    Long sizeLong = d.getLong("size");
                    String description = d.getString("description");
                    if (description == null) description = d.getString("descirption");
                    String imagUrl = d.getString("imagUrl");
                    String category = d.getString("category");

                    etName.setText(name);
                    etPrice.setText(priceLong != null ? String.valueOf(priceLong.intValue()) : "");
                    etSize.setText(sizeLong != null ? String.valueOf(sizeLong.intValue()) : "");
                    etDescription.setText(description);
                    currentImageUrl = imagUrl != null ? imagUrl : "";

                    if (category != null) {
                        String[] categories = getResources().getStringArray(R.array.menu_categories);
                        for (int i = 0; i < categories.length; i++) {
                            if (categories[i].equals(category)) {
                                categorySpinner.setSelection(i);
                                selectedCategory = category;
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Failed to load product", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String sizeStr = etSize.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || sizeStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl != null){
            currentImageUrl = imageUrl;
        }

        int price;
        int size;
        try {
            price = Integer.parseInt(priceStr);
            size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price and size must be numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (size < 0 || size > 2) {
            Toast.makeText(this, "Size must be 0, 1, or 2", Toast.LENGTH_SHORT).show();
            return;
        }

        Product p = new Product(documentId, name, price, size, description, currentImageUrl, selectedCategory);
        p.updateProduct();
        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            uploadImageToImgBB(imageUri);
                        }
                    });

    private String imageToBase64(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadImageToImgBB(Uri imageUri) {
        new Thread(() -> {
            try {
                String base64Image = imageToBase64(imageUri);

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("key", "00aea36538a632134f0599295138f76b")
                        .add("image", base64Image)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.imgbb.com/1/upload")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                JSONObject jsonObject = new JSONObject(responseBody);
                imageUrl = jsonObject
                        .getJSONObject("data")
                        .getString("url");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
