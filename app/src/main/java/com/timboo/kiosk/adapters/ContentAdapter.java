package com.timboo.kiosk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.timboo.kiosk.R;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private List<String> cartoonList;
    private Context context;

    public ContentAdapter(List<String> cartoonList, Context context) {
        this.cartoonList = cartoonList;
        this.context = context;
    }

    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContentAdapter.ViewHolder holder, int position) {
        String cartoonName = cartoonList.get(position);
        holder.titleTextView.setText(cartoonName);
        // İstersen burada resim ekleme, tıklama gibi işlemler de yapılabilir
    }

    @Override
    public int getItemCount() {
        return cartoonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.cartoon_title);
        }
    }
}