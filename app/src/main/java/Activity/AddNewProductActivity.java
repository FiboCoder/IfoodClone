package Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import model.Product;

public class AddNewProductActivity extends AppCompatActivity {

    //Components
    private AppCompatImageView ivProduct;
    private AppCompatEditText etProductName, etDescription;
    private AppCompatEditText etPrice;
    private AppCompatSpinner spCategories;
    private AppCompatButton btnAddNewProduct;

    //Firebase
    private StorageReference storage;

    //Utils
    private static final int GALLERY_SELECTION = 200;
    private String productUrlImage;
    private String userId;
    private Bitmap image;
    private byte[] imageData;
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        initAndConfigComponents();
        loadSpinnerData();
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Adicionar novo produto");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProduct = findViewById(R.id.ivProductNP);
        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, GALLERY_SELECTION);
                }

            }
        });

        etProductName = findViewById(R.id.etProductNameNP);
        spCategories = findViewById(R.id.spCategory);
        etDescription = findViewById(R.id.etDescriptionNP);

        etPrice = findViewById(R.id.etPriceNP);

        btnAddNewProduct = findViewById(R.id.btnSaveNP);
        btnAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });

        userId = FirebaseUser.getUserId();
        storage = FirebaseConfig.getStorage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            image = null;

            try{

                switch(requestCode){

                    case GALLERY_SELECTION:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                }

                if(image != null){

                    ivProduct.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    imageData = baos.toByteArray();


                }
            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }

    private void loadSpinnerData(){

        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categories
        );

        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoriesAdapter);

    }

    private void save(){

        spotsDialog = new SpotsDialog(AddNewProductActivity.this, "Adicionando Produto");
        spotsDialog.create();
        spotsDialog.show();

        String productName = etProductName.getText().toString();
        String productCategory = spCategories.getSelectedItem().toString();
        String productDescription = etDescription.getText().toString();
        String productPrice = etPrice.getText().toString();

        if(image != null){

            if(productName != null && !productName.isEmpty()){

                if(productCategory != null && !productCategory.isEmpty()){

                    if(productDescription != null && !productDescription.isEmpty()){

                        if(productPrice != null && !productPrice.isEmpty()){

                            StorageReference imageRef = storage
                                    .child("Images")
                                    .child("Products")
                                    .child(userId)
                                    .child(productName + ".jpeg");

                            UploadTask uploadTask = imageRef.putBytes(imageData);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    showMessage("Erro ao fazer upload da imagem, tente novamente.");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            Uri url = task.getResult();
                                            productUrlImage = String.valueOf(url);

                                            Product product = new Product();
                                            product.setUserId(userId);
                                            product.setUrlImage(productUrlImage);
                                            product.setName(productName);
                                            product.setCategory(productCategory);
                                            product.setDescription(productDescription);
                                            product.setPrice(productPrice);
                                            product.save();

                                            spotsDialog.dismiss();

                                            showMessage("Producto adicionado com sucesso.");
                                            finish();
                                        }
                                    });
                                }
                            });

                        }else{

                            spotsDialog.dismiss();
                            showMessage("Preencha o preço do produto.");
                        }
                    }else{

                        spotsDialog.dismiss();
                        showMessage("Preencha a descrição do produto.");
                    }
                }else{

                    spotsDialog.dismiss();
                    showMessage("Preencha a categoria do produto.");
                }
            }else{

                spotsDialog.dismiss();
                showMessage("Preencha o nome do produto.");
            }
        }else{

            spotsDialog.dismiss();
            showMessage("Selecione uma foto para o produto.");
        }
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}