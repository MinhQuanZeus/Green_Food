package zeus.quantm.greenfood.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.models.Notification;
import zeus.quantm.greenfood.network.models.order.Order;

/**
 * Created by QuanT on 6/25/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> musicTypeModels;
    private Context context;

    private View.OnClickListener onClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onClickListener = onItemClickListener;
    }

    public NotificationAdapter(List<Notification> musicTypeModels, Context context) {
        this.musicTypeModels = musicTypeModels;
        this.context = context;
    }

    //2
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_notification, parent, false);
        itemView.setOnClickListener(onClickListener);
        return new NotificationViewHolder(itemView);
    }

    //3
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.setData(musicTypeModels.get(position));
    }

    //1
    @Override
    public int getItemCount() {
        return musicTypeModels.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImageNotifi;
        private TextView tvTitleNotifi;
        private TextView tvDescription;
        View view;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            ivImageNotifi = (ImageView)itemView.findViewById(R.id.iv_notification);
            tvTitleNotifi = (TextView) itemView.findViewById(R.id.tv_title);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            view = itemView;
        }

        public void setData(Notification notification) {
            Picasso.with(context).load(notification.getImage()).into(ivImageNotifi);
            tvTitleNotifi.setText(notification.getTitle());
            view.setTag(notification);
        }
    }
}

