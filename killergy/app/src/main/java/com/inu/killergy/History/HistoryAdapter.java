package com.inu.killergy.History;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inu.killergy.DB.History;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.R;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
{
    private List<History> dataList;
    private Activity context;
    private MainDB database;



    public HistoryAdapter(Activity context, List<History> dataList)
    {
        this.context = context;
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryAdapter.ViewHolder holder, int position)
    {
        final History data = dataList.get(position);
        database = MainDB.getInstance(context);
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        holder.txtProductName.setText(data.productName);
        holder.txtDate.setText(dateFormat.format(data.searchDate));
        holder.img_product.setImageBitmap(BitmapFactory.decodeByteArray(data.productImage, 0, data.productImage.length));
        holder.btDetail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                History history = dataList.get(holder.getAdapterPosition());
                List<History> historyList;

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_history_detail);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width, height);

                dialog.show();
                ImageView imgShowProduct = dialog.findViewById(R.id.imgShowProduct);
                final TextView txtShowProductName = dialog.findViewById(R.id.txtShowProductName);
                final TextView txtShowProductDate = dialog.findViewById(R.id.txtShowProductDate);
                final TextView txtShowProductAllergy = dialog.findViewById(R.id.txtShowProductAllergy);
                final TextView txtShowProductComponent = dialog.findViewById(R.id.txtShowProductComponent);
                Button bt_confirm = dialog.findViewById(R.id.bt_confirm);
                txtShowProductName.setText(data.productName);
                txtShowProductDate.setText(dateFormat.format(data.searchDate));
                txtShowProductAllergy.setText(data.allergies);
                txtShowProductComponent.setText(data.Componentstr());
                imgShowProduct.setImageBitmap(BitmapFactory.decodeByteArray(data.productImage, 0, data.productImage.length));

                bt_confirm.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();

                    }
                });
            }
        });

        /* 삭제 클릭 */
        holder.btDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                History history = dataList.get(holder.getAdapterPosition());

                database.historyDao().deleteHistory(history)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                int position = holder.getAdapterPosition();
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, dataList.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtProductName, txtDate;
        ImageView img_product, btDetail, btDelete;

        public ViewHolder(@NonNull View view)
        {
            super(view);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtDate = view.findViewById(R.id.txtDate);
            img_product = view.findViewById(R.id.img_product);
            btDetail = view.findViewById(R.id.bt_detail);
            btDelete = view.findViewById(R.id.bt_delete);
        }
    }

}