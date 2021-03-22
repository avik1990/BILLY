package com.tpcodl.billingreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tpcodl.billingreading.R;

import java.util.List;

public class NonSBMBilledDataAdapter extends RecyclerView.Adapter<NonSBMBilledDataAdapter.ViewHolder> {

    private List<String>listData;
    private Context mContext;


    public NonSBMBilledDataAdapter(Context context,List<String>list) {
        this.listData = list;
        mContext=context;
    }

    @NonNull
    @Override
    public NonSBMBilledDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NonSBMBilledDataAdapter.ViewHolder holder, int position) {
        holder.tv_inst.setText(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_inst;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_inst=itemView.findViewById(R.id.tv_inst);

        }
    }
}
