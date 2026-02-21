package com.example.goldenbite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addProduct extends AppCompatActivity {
    String imageUrl;

    Button pickImg, addProduct;
    EditText etname,etprice,etsize,etdescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        pickImg = findViewById(R.id.choosePicture);
        addProduct  = findViewById(R.id.addProduct);
        etname =findViewById(R.id.name);
        etprice = findViewById(R.id.price);
        etsize = findViewById(R.id.size);
        etdescription = findViewById(R.id.description);






        pickImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        addProduct.setOnClickListener(v -> {
            if (!(etname == null || etdescription == null || etprice == null || etsize == null)){
                String name = etname.getText().toString().trim();
                String description = etdescription.getText().toString().trim();
                int price = Integer.parseInt(etprice.getText().toString());
                int size = Integer.parseInt(etsize.getText().toString());

                if (size > 2 || size < 0){
                    Toast.makeText(this, "size must be between 0-2", Toast.LENGTH_SHORT).show();
                }

                if (imageUrl.isEmpty()){
                    Toast.makeText(this, "image must be added", Toast.LENGTH_SHORT).show();
                }

                Product p = new Product(name,price,size,description,imageUrl);
                p.saveProduct();
            }
            else {
                Toast.makeText(this, "please fill all fields", Toast.LENGTH_SHORT).show();
            }
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