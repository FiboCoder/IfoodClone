package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.ifood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.EnterpriseListAdapter;
import helper.FirebaseConfig;
import helper.RecyclerItemClickListener;
import model.Enterprise;

public class UserActivity extends AppCompatActivity {

    //List products
    private List<Enterprise> enterpriseList = new ArrayList<>();
    private RecyclerView rvEnterprisesList;
    private EnterpriseListAdapter adapter;

    //Firebase
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private ValueEventListener valueEventListenerEnterprises;

    private SearchView svMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initAndConfigComponents();
        recoverEnterprises();
    }

    @Override
    protected void onStart() {
        super.onStart();

        recoverEnterprises();
    }

    @Override
    protected void onStop() {
        super.onStop();

        reference.removeEventListener(valueEventListenerEnterprises);
    }

    private void initAndConfigComponents(){

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Ifood");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        reference = FirebaseConfig.getReference();
        auth = FirebaseConfig.getAuth();

        adapter = new EnterpriseListAdapter(getApplicationContext(), enterpriseList);

        rvEnterprisesList = findViewById(R.id.rvEnterprises);
        rvEnterprisesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvEnterprisesList.setHasFixedSize(true);
        rvEnterprisesList.setAdapter(adapter);

        rvEnterprisesList.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                rvEnterprisesList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(UserActivity.this, CatalogActivity.class);
                        intent.putExtra("enterprise", enterpriseList.get(position));
                        startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        MenuItem item = menu.findItem(R.id.menuSearchUser);
        svMain = (SearchView) item.getActionView();
        svMain.setQueryHint("Pesquisar restaurantes");
        svMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchEnterprises(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuConfigUser:
                doSelectedMenuOptions("menuConfigUser");
                break;

            case R.id.menuLogoutUser:
                doSelectedMenuOptions("menuLogoutUser");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void recoverEnterprises(){

        DatabaseReference enterprisesRef = reference
                .child("Enterprises");

        valueEventListenerEnterprises = enterprisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                enterpriseList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    Enterprise enterprise = ds.getValue(Enterprise.class);

                    enterpriseList.add(enterprise);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void doSelectedMenuOptions(String type){

        if(type.equals("menuConfigUser")){

            startActivity(new Intent(UserActivity.this, UserConfigActivity.class));

        }else if(type.equals("menuLogoutUser")){

            auth.signOut();
            startActivity(new Intent(UserActivity.this, AuthActivity.class));
            finish();
        }
    }

    private void searchEnterprises(String search){

        DatabaseReference enterprisesRef = reference
                .child("Enterprises");
        Query query = enterprisesRef.orderByChild("enterpriseName")
                .startAt(search)
                .endAt(search + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                enterpriseList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    Enterprise enterprise = ds.getValue(Enterprise.class);

                    enterpriseList.add(enterprise);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}