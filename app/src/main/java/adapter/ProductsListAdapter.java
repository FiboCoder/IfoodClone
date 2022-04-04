package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifood.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Product;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.MyViewHolder> {

    private Context context;
    private List<Product> productsList;

    public ProductsListAdapter(Context c, List<Product> productsL) {
        this.context = c;
        this.productsList = productsL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product product = productsList.get(position);

        if(!product.getUrlImage().isEmpty()){

            Picasso.get().load(product.getUrlImage()).into(holder.ivProduct);

        }

        holder.tvProductName.setText(product.getName());
        holder.tvProductDescription.setText(product.getDescription());
        holder.tvProductPrice.setText(product.getPrice());
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private AppCompatImageView ivProduct;
        private AppCompatTextView tvProductName, tvProductDescription, tvProductPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProduct = itemView.findViewById(R.id.ivProductAdapter);
            tvProductName = itemView.findViewById(R.id.tvProductNameAdapter);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescriptionAdapter);
            tvProductPrice = itemView.findViewById(R.id.tvProductPriceAdapter);
        }
    }
}
