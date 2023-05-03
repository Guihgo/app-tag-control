package com.guihgo.tagcontrol.ui.inventory;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guihgo.tagcontrol.R;
import com.guihgo.tagcontrol.model.inventory.InventoryEntity;
import com.guihgo.tagcontrol.ui.helper.OnItemInteractionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.ViewHolder> {

    public List<InventoryEntity> inventoryEntityList;

    public ItemTouchHelper itemTouchHelper;

    private OnItemInteractionListener onItemInteractionListener = null;

    public void setupRecycleView(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public InventoryListAdapter(@Nullable List<InventoryEntity> inventoryEntityList) {
        this.inventoryEntityList = (this.inventoryEntityList == null) ? new ArrayList<>() : inventoryEntityList;

        InventoryListAdapter adapter = this;

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (adapter.onItemInteractionListener == null) return;

                if (direction == ItemTouchHelper.END) {
                    adapter.onItemInteractionListener.onItemSwipedLeft(viewHolder.getAbsoluteAdapterPosition());
                }
            }
        });
    }

    public void setOnItemInteractionListener(OnItemInteractionListener onItemInteractionListener) {
        this.onItemInteractionListener = onItemInteractionListener;
    }

    @NonNull
    @Override
    public InventoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventories_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryListAdapter.ViewHolder holder, int position) {
        InventoryEntity inventoryEntity = inventoryEntityList.get(position);
        holder.quantityTextView.setText(""+inventoryEntity.quantity);
        holder.tagNameTextView.setText(inventoryEntity.tagName);

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(inventoryEntity.expirationDate);
        holder.expirationDateTextView.setText((new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())).format(expirationDate.getTime()));

        if(Calendar.getInstance().before(expirationDate)) {
            holder.expirationStatusImageView.setBackgroundResource(R.drawable.ic_done);
            holder.expirationStatusImageView.setColorFilter(com.google.android.material.R.attr.colorOnSurface);
        } else {
            holder.expirationStatusImageView.setBackgroundResource(R.drawable.ic_error_outline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemInteractionListener.onItemClick(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.inventoryEntityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quantityTextView;
        public TextView tagNameTextView;
        public TextView expirationDateTextView;
        public ImageView expirationStatusImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            quantityTextView = itemView.findViewById(R.id.inventory_quantity);
            tagNameTextView = itemView.findViewById(R.id.inventory_tag_name);
            expirationDateTextView = itemView.findViewById(R.id.inventory_expiration_date);
            expirationStatusImageView = itemView.findViewById(R.id.inventory_expiration_status);
        }
    }
}
