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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.goldenbite.Classes.Product;
import com.example.goldenbite.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addProduct extends BaseActivity {
    String imageUrl;

    Button pickImg, addProduct, goBack;
    EditText etname, etprice, etsize, etdescription;
    Spinner categorySpinner;
    String selectedCategory = Product.CATEGORY_HOT_DRINKS;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        goBack = findViewById(R.id.btn_add_go_back);
        goBack.setOnClickListener(v -> finish());


        pickImg = findViewById(R.id.choosePicture);
        addProduct = findViewById(R.id.addProduct);
        etname = findViewById(R.id.name);
        etprice = findViewById(R.id.price);
        etsize = findViewById(R.id.size);
        etdescription = findViewById(R.id.description);
        categorySpinner = findViewById(R.id.category_spinner);
        categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] categories = getResources().getStringArray(R.array.menu_categories);
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });






        pickImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        addProduct.setOnClickListener(v -> {
            String name = etname.getText().toString().trim();
            String description = etdescription.getText().toString().trim();
            String priceStr = etprice.getText().toString().trim();
            String sizeStr = etsize.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || sizeStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUrl == null || imageUrl.isEmpty()) {
                Toast.makeText(this, "Image must be added", Toast.LENGTH_SHORT).show();
                return;
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
            if (size > 2 || size < 0) {
                Toast.makeText(this, "Size must be between 0-2", Toast.LENGTH_SHORT).show();
                return;
            }

            Product p = new Product(name, price, size, description, imageUrl, selectedCategory);
            p.saveProduct();
            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(addProduct.this, MainActivity3.class);
            startActivity(intent);

        });


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