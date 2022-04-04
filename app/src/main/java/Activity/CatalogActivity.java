package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.ProductsListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import helper.RecyclerItemClickListener;
import model.Enterprise;
import model.Order;
import model.OrderItems;
import model.Product;
import model.User;

public class CatalogActivity extends AppCompatActivity {

    private CircleImageView civProfile;
    private AppCompatTextView tvEnterpriseName, tvEnterpriseCategory, tvDeliveryTime, tvDeliveryRate;
    private AppCompatTextView tvQtt, tvAmount;

    private Enterprise enterprise;
    private User user;
    private Order recoveredOrder;
    private String enterpriseId;
    private String loggedUserId;
    private SpotsDialog spotsDialog;
    private AlertDialog dialog;
    private List<OrderItems> orderItemsList = new ArrayList<>();
    private int qttItems;
    private Double amountOrder;
    private int paymentMethod;


    //Firebase
    private DatabaseReference reference;

    //List product
    private List<Product> productsList = new ArrayList<>();
    private RecyclerView rvProductsList;
    private ProductsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initAndConfigComponents();
        recoverProductsList();
        recoverUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalog, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch(item.getItemId()){

            case R.id.menuConfirmOrder:
                confirmOrder();
                break;
        }
        return true;
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            enterprise = (Enterprise) bundle.getSerializable("enterprise");
        }

        civProfile = findViewById(R.id.civProfileCatalog);
        tvEnterpriseName = findViewById(R.id.tvEnterpriseNameCatalog);
        tvEnterpriseCategory = findViewById(R.id.tvEnterpriseCategoryCatalog);
        tvDeliveryTime = findViewById(R.id.tvEnterpriseDeliveryTimeCatalog);
        tvDeliveryRate = findViewById(R.id.tvEnterpriseDeliveryRateCatalog);
        tvQtt = findViewById(R.id.tvQtt);
        tvAmount = findViewById(R.id.tvAmount);
        Picasso.get().load(enterprise.getUrlImage()).into(civProfile);

        tvEnterpriseName.setText(enterprise.getEnterpriseName());
        tvEnterpriseCategory.setText(enterprise.getEnterpriseCategory());
        tvDeliveryTime.setText(enterprise.getDeliveryTime());
        tvDeliveryRate.setText(enterprise.getDeliveryRate());

        enterpriseId = enterprise.getEnterpriseId();
        loggedUserId = FirebaseUser.getUserId();
        reference = FirebaseConfig.getReference();

        adapter = new ProductsListAdapter(getApplicationContext(), productsList);

        rvProductsList = findViewById(R.id.rvCatalog);
        rvProductsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvProductsList.setHasFixedSize(true);
        rvProductsList.setAdapter(adapter);
        
        rvProductsList.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                rvProductsList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        confirmQuantity(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    private void recoverProductsList(){

        DatabaseReference enterpriseRef = reference
                .child("Products")
                .child(enterpriseId);

        enterpriseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productsList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    Product product = ds.getValue(Product.class);
                    productsList.add(product);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recoverUserData(){

        spotsDialog = new SpotsDialog(CatalogActivity.this, "Carregando Dados");
        spotsDialog.create();
        spotsDialog.show();

        DatabaseReference usersRef = reference
                .child("Users")
                .child(loggedUserId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue(User.class) != null){

                    user = snapshot.getValue(User.class);
                }

                recoverOrder();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recoverOrder() {


        DatabaseReference orderRef = reference
                .child("Orders_User")
                .child(enterpriseId)
                .child(loggedUserId);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qttItems = 0;
                amountOrder = 0.0;
                orderItemsList = new ArrayList<>();

                if (snapshot.getValue(Order.class) != null) {

                    recoveredOrder = snapshot.getValue(Order.class);

                    orderItemsList = recoveredOrder.getOrderItems();

                    for (OrderItems orderItems : orderItemsList) {

                        int qtt = orderItems.getProductQuantity();
                        Double price = orderItems.getProductPrice();
                        Toast.makeText(CatalogActivity.this, String.valueOf(price), Toast.LENGTH_SHORT).show();
                        amountOrder += (qtt * price);
                        qttItems += qtt;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                tvQtt.setText(String.valueOf(qttItems));
                tvAmount.setText(String.valueOf(amountOrder));

                spotsDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void confirmQuantity(int position){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        EditText etQtt = new EditText(this);
        etQtt.setText("1");

        builder.setView(etQtt);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String qtt = etQtt.getText().toString();
                Product product = productsList.get(position);

                OrderItems orderItems = new OrderItems();

                orderItems.setProductId(product.getProductId());
                orderItems.setProductName(product.getName());
                orderItems.setProductQuantity(Integer.parseInt(qtt));
                orderItems.setProductPrice(Double.parseDouble(product.getPrice()));

                orderItemsList.add(orderItems);

                if(recoveredOrder == null){

                    recoveredOrder = new Order(loggedUserId, enterpriseId);
                }

                String leadAddress = user.getStreet() + ", " + user.getDistrict() + " - " + user.getCity();
                recoveredOrder.setLeadName(user.getUserName());
                recoveredOrder.setAddress(leadAddress);
                recoveredOrder.setOrderItems(orderItemsList);
                recoveredOrder.save();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                paymentMethod = which;
            }
        });


        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmOrder(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento:");

        CharSequence[]items = new CharSequence[]{
                "Dinheiro", "Máquina de Cartão"
        };
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AppCompatEditText etObservation = new AppCompatEditText(this);
        etObservation.setHint("Precisa de troco? Quer alterar algo?");

        builder.setView(etObservation);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String observation = etObservation.getText().toString();
                recoveredOrder.setPaymentMethod(String.valueOf(paymentMethod));
                recoveredOrder.setObservation(observation);
                recoveredOrder.setStatus("Confirmado");
                recoveredOrder.confirm();
                recoveredOrder.remove();
                recoveredOrder = null;
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        dialog.show();
    }
}