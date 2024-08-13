package com.inu.killergy.Notification;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inu.killergy.DB.History;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.DB.Notification;
import com.inu.killergy.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {

    private List<Notification> mNotificationList;
    MainDB database;
    private Activity context;

    public notificationAdapter(Activity context, List<Notification> mDataset) {
        this.context = context;
        this.mNotificationList = mDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);
        database = MainDB.getInstance(context);
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        holder.message.setText(notification.text);
        holder.date.setText(dateFormat.format(notification.date));

        /* 삭제 클릭 */
        holder.delete_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Notification notification = mNotificationList.get(holder.getAdapterPosition());

                database.notificationDao().deleteNotification(notification)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                int position = holder.getAdapterPosition();
                mNotificationList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mNotificationList.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;
        ImageView delete_btn;
        public ViewHolder(@NonNull View view) {
            super(view);

            message = view.findViewById(R.id.notificationMessage);
            date = view.findViewById(R.id.dateText);
            delete_btn = view.findViewById(R.id.delete_btn);
        }

        void onBind(notificationItem notificationItem) {
            message.setText(notificationItem.getMessage());
            date.setText(notificationItem.getDate());
        }
    }
}
