package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.ifood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth auth;

    //Components
    private AppCompatEditText etEmail, etPass;
    private LinearLayoutCompat llUserType;
    private SwitchCompat stAccessType, stUserType;
    private AppCompatButton btnAccess;

    //Utils
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initAndConfigComponents();
        verifyLoggedUser();
    }

    private void initAndConfigComponents(){

        auth = FirebaseConfig.getAuth();

        etEmail = findViewById(R.id.etEmailAuth);
        etPass = findViewById(R.id.etPassAuth);

        llUserType = findViewById(R.id.llUserType);

        stAccessType = findViewById(R.id.stAccessType);
        stAccessType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    llUserType.setVisibility(View.VISIBLE);
                    btnAccess.setText("Cadastrar");
                }else{

                    llUserType.setVisibility(View.GONE);
                    btnAccess.setText("Acessar");
                }
            }
        });

        stUserType = findViewById(R.id.stUserType);

        btnAccess = findViewById(R.id.btnAccess);
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth();
            }
        });
    }

    private void auth(){

        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(email != null && !email.isEmpty()){

            if(pass != null && !pass.isEmpty()){

                if(stAccessType.isChecked()){

                    spotsDialog = new SpotsDialog(AuthActivity.this, "Cadastrando usuário...");
                    spotsDialog.create();
                    spotsDialog.show();

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                spotsDialog.dismiss();
                                showMessage("Sucesso ao cadastrar usuário!");
                                FirebaseUser.updateUserType(getUserType());
                                openMainScreen(getUserType());
                            }else{

                                spotsDialog.dismiss();

                                String exception = "";
                                try{

                                    throw task.getException();
                                }catch (FirebaseAuthWeakPasswordException e){
                                    exception = "Digite uma senha mais forte.";
                                }
                                catch (FirebaseAuthInvalidCredentialsException e){
                                    exception = "Digite um e-mail válido.";
                                }catch (FirebaseAuthUserCollisionException e){
                                    exception = "Já existe uma conta cadastrada com o e-mail informado.";
                                }
                                catch (Exception e){
                                    exception = e.getMessage();
                                    e.printStackTrace();
                                }

                                showMessage("Erro: " + exception);
                            }

                        }
                    });

                }else{

                    spotsDialog = new SpotsDialog(AuthActivity.this, "Acessando conta...");
                    spotsDialog.create();
                    spotsDialog.show();

                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                spotsDialog.dismiss();

                                String userType = task.getResult().getUser().getDisplayName();
                                openMainScreen(userType);

                            }else{

                                spotsDialog.dismiss();

                                showMessage("Erro a fazer login, erro: " + task.getException());
                            }
                        }
                    });
                }

            }else{

                showMessage("Preencha o campo Senha por favor.");
            }

        }else{

            showMessage("Preencha o campo E-mail por favor.");
        }
    }

    private String getUserType(){

        return stUserType.isChecked() ? "E" : "U";
    }

    private void showMessage(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openMainScreen(String userType){

        if(userType.equals("E")){

            startActivity(new Intent(AuthActivity.this, EnterpriseActivity.class));
            finish();
        }else{

            startActivity(new Intent(AuthActivity.this, UserActivity.class));
            finish();
        }
    }

    private void verifyLoggedUser(){

        com.google.firebase.auth.FirebaseUser currentUser = FirebaseUser.getCurrentUser();
        if(currentUser != null){
            String userType = currentUser.getDisplayName();
            openMainScreen(userType);
        }
    }
}