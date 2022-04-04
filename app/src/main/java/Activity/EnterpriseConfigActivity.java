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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import model.Enterprise;

public class EnterpriseConfigActivity extends AppCompatActivity {

    private AppCompatImageView ivProfile;
    private AppCompatEditText etName, etDeliveryTime;
    private CurrencyEditText etDeliveryRate;
    private AppCompatSpinner spCategories;
    private AppCompatButton btnSave;

    //Firebase
    private DatabaseReference reference;
    private StorageReference storage;

    //Utils
    private SpotsDialog spotsDialog;
    private static final int GALLERY_SELECTION = 200;
    private String loggedUserId;
    private String urlProfileImage;
    private Bitmap image;
    private byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_config);

        initAndConfigComponents();
        loadSpinnerData();
        recoverEnterpriseData();
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseConfig.getReference();
        storage = FirebaseConfig.getStorage();

        loggedUserId = FirebaseUser.getUserId();

        ivProfile = findViewById(R.id.ivEnterpriseProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent, GALLERY_SELECTION);
                }
            }
        });

        etName = findViewById(R.id.etEnterpriseName);
        spCategories = findViewById(R.id.spEnterpriseCategory);
        etDeliveryTime = findViewById(R.id.etDeliveryTime);

        Locale locale = new Locale("pt", "BR");
        etDeliveryRate = findViewById(R.id.etDeliveryRate);
        etDeliveryRate.setLocale(locale);

        btnSave = findViewById(R.id.btnSaveEC);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });
    }

    private void loadSpinnerData(){

        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categories
        );

        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoriesAdapter);
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

                    ivProfile.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    imageData = baos.toByteArray();

                }

            }catch (Exception e){

                e.printStackTrace();
            }
        }

    }

    private void save(){

        spotsDialog = new SpotsDialog(EnterpriseConfigActivity.this, "Salvando configurações");
        spotsDialog.create();
        spotsDialog.show();

        String name = etName.getText().toString();
        String category = spCategories.getSelectedItem().toString();
        String deliveryTime = etDeliveryTime.getText().toString();
        String deliveryRate = etDeliveryRate.getText().toString();


        if(image != null){

            if(name != null && !name.isEmpty()){

                if(category != null && !category.isEmpty()){

                    if(deliveryTime != null && !deliveryTime.isEmpty()){

                        if(deliveryRate != null && !deliveryRate.isEmpty()){

                            StorageReference imageRef = storage
                                    .child("Images")
                                    .child("EnterpriseProfile")
                                    .child(name + ".jpeg");

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
                                            urlProfileImage = String.valueOf(url);

                                            Enterprise enterprise = new Enterprise();
                                            enterprise.setEnterpriseId(loggedUserId);
                                            enterprise.setUrlImage(urlProfileImage);
                                            enterprise.setEnterpriseName(name);
                                            enterprise.setEnterpriseCategory(category);
                                            enterprise.setDeliveryTime(deliveryTime);
                                            enterprise.setDeliveryRate(deliveryRate);
                                            enterprise.save();

                                            spotsDialog.dismiss();

                                            showMessage("Perfil configurado com sucesso!");
                                            finish();
                                        }
                                    });
                                }
                            });

                        }else{

                            spotsDialog.dismiss();
                            showMessage("Preencha a taxa de entrega da loja.");
                        }
                    }else{

                        spotsDialog.dismiss();
                        showMessage("Preencha o tempo de entrega da loja.");
                    }
                }else{

                    spotsDialog.dismiss();
                    showMessage("Preeencha a categoria da loja.");
                }
            }else{

                spotsDialog.dismiss();
                showMessage("Preencha o nome da loja.");
            }
        }else{

            spotsDialog.dismiss();
            showMessage("Selecione uma imagem de perfil.");
        }
    }

    private void recoverEnterpriseData(){

        DatabaseReference enterpriseRef = reference
                .child("Enterprises")
                .child(loggedUserId);
        enterpriseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Enterprise enterprise = snapshot.getValue(Enterprise.class);

                if(enterprise != null) {

                    if (enterprise.getUrlImage() != null) {

                        Picasso.get().load(enterprise.getUrlImage()).into(ivProfile);
                    }

                    etName.setText(enterprise.getEnterpriseName());

                    String compareValue = enterprise.getEnterpriseCategory();

                    String[] categories = getResources().getStringArray(R.array.categories);
                    ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                            EnterpriseConfigActivity.this, android.R.layout.simple_spinner_item, categories
                    );

                    categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategories.setAdapter(categoriesAdapter);
                    if (compareValue != null) {
                        int spinnerPosition = categoriesAdapter.getPosition(compareValue);
                        spCategories.setSelection(spinnerPosition);
                    }

                    etDeliveryTime.setText(enterprise.getDeliveryTime());
                    etDeliveryRate.setText(enterprise.getDeliveryRate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}