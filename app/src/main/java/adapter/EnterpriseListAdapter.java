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

import de.hdodenhof.circleimageview.CircleImageView;
import model.Enterprise;

public class EnterpriseListAdapter extends RecyclerView.Adapter<EnterpriseListAdapter.MyViewHolder> {

    private Context context;
    private List<Enterprise> enterpriseList;

    public EnterpriseListAdapter(Context c, List<Enterprise> enterpriseL) {
        this.context = c;
        this.enterpriseList = enterpriseL;
    }

    @NonNull
    @Override
    public EnterpriseListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.enterprise_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnterpriseListAdapter.MyViewHolder holder, int position) {

        Enterprise enterprise = enterpriseList.get(position);

        Picasso.get().load(enterprise.getUrlImage()).into(holder.civProfile);
        holder.tvName.setText(enterprise.getEnterpriseName());
        holder.tvDeliveryTime.setText(enterprise.getDeliveryTime());
        holder.tvDeliveryRate.setText(enterprise.getDeliveryRate());
    }

    @Override
    public int getItemCount() {
        return enterpriseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private AppCompatTextView tvName, tvDeliveryTime, tvDeliveryRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileAdapter);
            tvName = itemView.findViewById(R.id.tvEnterpriseNameAdapter);
            tvDeliveryTime = itemView.findViewById(R.id.tvEnterpriseDeliveryTimeAdapter);
            tvDeliveryRate = itemView.findViewById(R.id.tvEnterpriseDeliveryRateAdapter);
        }
    }
}
