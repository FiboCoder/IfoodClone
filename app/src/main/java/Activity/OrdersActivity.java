package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.ifood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterOrder;
import dmax.dialog.SpotsDialog;
import helper.FirebaseConfig;
import helper.FirebaseUser;
import helper.RecyclerItemClickListener;
import model.Order;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private AdapterOrder adapter;
    private List<Order> orderList = new ArrayList<>();

    private DatabaseReference reference;

    private SpotsDialog spotsDialog;
    private String loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        initAndConfigComponents();
        recoverOrders();
    }

    private void initAndConfigComponents() {

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Pedidos");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseConfig.getReference();

        loggedUserId = FirebaseUser.getUserId();

        adapter = new AdapterOrder(orderList);
        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvOrders.setHasFixedSize(true);
        rvOrders.setAdapter(adapter);

        rvOrders.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvOrders,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        Order order = orderList.get(position);
                        order.setStatus("Finalizado");
                        order.updateStatus();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));


    }

    private void recoverOrders(){

        spotsDialog = new SpotsDialog(OrdersActivity.this, "Carregando Dados");
        spotsDialog.create();
        spotsDialog.show();

        DatabaseReference ordersRef = reference
                .child("Orders")
                .child(loggedUserId);

        Query searchOrder = ordersRef.orderByChild("status")
                .equalTo("Confirmado");

        searchOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                if(snapshot.getValue() != null){

                    for(DataSnapshot ds: snapshot.getChildren()){

                        Order order = ds.getValue(Order.class);
                        orderList.add(order);
                    }

                    adapter.notifyDataSetChanged();
                    spotsDialog.dismiss();
                }else{

                    spotsDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}