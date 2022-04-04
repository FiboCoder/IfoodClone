package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ifood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import model.User;

public class UserConfigActivity extends AppCompatActivity {

    //Firebase
    private DatabaseReference reference;

    //Components
    private AppCompatEditText etUserName, etStreet, etDistrict, etCity;
    private AppCompatButton btnSaveUC;

    //Utils
    private String userId;
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);

        initAndConfigComponents();
        recoverUserConfig();
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Configurações");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseConfig.getReference();

        userId = FirebaseUser.getUserId();

        etUserName = findViewById(R.id.etUserNameUC);
        etStreet = findViewById(R.id.etStreetUC);
        etDistrict = findViewById(R.id.etDistrictUC);
        etCity = findViewById(R.id.etCityUC);

        btnSaveUC = findViewById(R.id.btnSaveUC);
        btnSaveUC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });

    }

    private void recoverUserConfig(){

        DatabaseReference userRef = reference
                .child("Users")
                .child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if(user != null){

                    etUserName.setText(user.getUserName());
                    etStreet.setText(user.getStreet());
                    etDistrict.setText(user.getDistrict());
                    etCity.setText(user.getCity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void save(){

        spotsDialog = new SpotsDialog(UserConfigActivity.this, "Salvando configurações");
        spotsDialog.create();
        spotsDialog.show();

        String name = etUserName.getText().toString();
        String street = etStreet.getText().toString();
        String district = etDistrict.getText().toString();
        String city = etCity.getText().toString();

        if(name != null && !name.isEmpty()){

            if(street != null && !street.isEmpty()){

                if(district != null && !district.isEmpty()){

                    if(city != null && !city.isEmpty()){

                        User user = new User();

                        user.setUserId(userId);
                        user.setUserName(name);
                        user.setStreet(street);
                        user.setDistrict(district);
                        user.setCity(city);
                        user.save();

                        spotsDialog.dismiss();
                        finish();

                    }else{

                        spotsDialog.dismiss();
                        showMessage("Por favor, preencha o nome do usuário.");
                    }
                }else{

                    spotsDialog.dismiss();
                    showMessage("Por favor, preencha a rua.");
                }
            }else{

                spotsDialog.dismiss();
                showMessage("Por favor, preencha o bairro.");
            }
        }else{

            spotsDialog.dismiss();
            showMessage("Por favor, preencha a cidade.");
        }
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}