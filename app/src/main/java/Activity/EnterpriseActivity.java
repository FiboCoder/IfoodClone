package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.ifood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.ProductsListAdapter;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import helper.RecyclerItemClickListener;
import model.Product;

public class EnterpriseActivity extends AppCompatActivity {

    //List products
    private List<Product> productsList = new ArrayList<>();
    private RecyclerView rvProducts;
    private ProductsListAdapter adapter;

    private ValueEventListener valueEventListenerProducts;

    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise);

        initAndConfigComponents();
        recoverProductsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverProductsList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListenerProducts);
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Ifood - Empresa");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        auth = FirebaseConfig.getAuth();
        reference = FirebaseConfig.getReference();

        adapter = new ProductsListAdapter(getApplicationContext(), productsList);

        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvProducts.setHasFixedSize(true);
        rvProducts.setAdapter(adapter);

        rvProducts.addOnItemTouchListener( new RecyclerItemClickListener(
                getApplicationContext(),
                rvProducts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(EnterpriseActivity.this);
                        builder.setTitle("Excluir Produto");
                        builder.setMessage("Deseja realmente remover esse produto do seu cardápio?");

                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Product product = productsList.get(position);
                                productsList.remove(product);
                                product.remove();
                                dialog.dismiss();
                                Toast.makeText(EnterpriseActivity.this, "Produto removido com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_enterprise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuOrders:
                doSelectedMenuOption("orders");
                break;

            case R.id.menuNewProduct:
                doSelectedMenuOption("newProduct");
                break;

            case R.id.menuConfigEnterprise:
                doSelectedMenuOption("config");
                break;

            case R.id.menuLogout:
                doSelectedMenuOption("logout");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doSelectedMenuOption(String type){

        if(type.equals("orders")){

            startActivity(new Intent(EnterpriseActivity.this, OrdersActivity.class));

        }else if(type.equals("newProduct")){

            startActivity(new Intent(EnterpriseActivity.this, AddNewProductActivity.class));

        }else if(type.equals("config")){

            startActivity(new Intent(EnterpriseActivity.this, EnterpriseConfigActivity.class));

        }else if(type.equals("logout")){

            auth.signOut();
            finish();
            startActivity(new Intent(EnterpriseActivity.this, AuthActivity.class));
        }
    }

    private void recoverProductsList(){

        String loggedUserId = FirebaseUser.getUserId();
        DatabaseReference productsRef = reference.
                child("Products")
                .child(loggedUserId);
        valueEventListenerProducts = productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productsList.clear();

                for(DataSnapshot product : snapshot.getChildren()){

                    Product product1 = product.getValue(Product.class);
                    productsList.add(product1);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}