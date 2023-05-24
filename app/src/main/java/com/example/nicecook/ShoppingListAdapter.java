package com.example.nicecook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder> {
    Context context;
    ArrayList<ShoppingItem> list;
    private OnCheckedChangeListener listener;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingItem> list, OnCheckedChangeListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_item, parent, false);
        return new ShoppingListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListHolder holder, int position) {
        ShoppingItem shoppingItem = list.get(position);
        holder.txtItem.setText(shoppingItem.getItemName());
        holder.txtQuantity.setText(shoppingItem.getQuantity());
        holder.txtPrice.setText("₱".concat(Double.toString(shoppingItem.getPrice())));

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onItemCheckedChanged(b, shoppingItem.getPrice(), shoppingItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ShoppingListHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView txtItem, txtQuantity, txtPrice;


        public ShoppingListHolder(@NonNull View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.checkbox);
            txtItem = itemView.findViewById(R.id.txtItem);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }

    public interface OnCheckedChangeListener {
        void onItemCheckedChanged(boolean isIncrement, double amount, ShoppingItem shoppingItem);
    }
}
