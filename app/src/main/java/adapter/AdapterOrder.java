package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifood.R;

import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.OrderItems;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.MyViewHolder> {

    private List<Order> ordersList;

    public AdapterOrder(List<Order> ordersL) {

        this.ordersList = ordersL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Order order = ordersList.get(i);
        holder.nome.setText( order.getLeadName() );
        holder.endereco.setText( "Endereço: "+ order.getAddress());
        holder.observacao.setText( "Obs: "+ order.getObservation() );

        List<OrderItems> items = new ArrayList<>();
        items = order.getOrderItems();
        String description = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( OrderItems orderItems : items ){

            int qtt = orderItems.getProductQuantity();
            Double price = orderItems.getProductPrice();
            total += (qtt * price);

            String name = orderItems.getProductName();
            description += numeroItem + ") " + name + " / (" + qtt + " x R$ " + price + ") \n";
            numeroItem++;
        }
        description += "Total: R$ " + total;
        holder.itens.setText(description);

        int paymentMethod = Integer.parseInt(order.getPaymentMethod());
        String payment = paymentMethod == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.pgto.setText( "pgto: " + payment );

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
