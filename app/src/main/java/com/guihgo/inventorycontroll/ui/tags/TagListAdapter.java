package com.guihgo.inventorycontroll.ui.tags;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guihgo.inventorycontroll.R;
import com.guihgo.inventorycontroll.model.tag.TagEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    public List<TagEntity> tagEntityList;

    public ItemTouchHelper itemTouchHelper;

    public interface OnItemSlideListener {
        public void onItemSlideLeft(int position);
        public void onItemSlideRight(int position);
    }

    private OnItemSlideListener onItemSlideListener = null;

    public void setupRecycleView(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public TagListAdapter(List<TagEntity> tagEntityList) {
        this.tagEntityList = tagEntityList;

        TagListAdapter adapter = this;

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
                if(adapter.onItemSlideListener == null) return;

                if(direction == ItemTouchHelper.END) {
                    adapter.onItemSlideListener.onItemSlideLeft(viewHolder.getAbsoluteAdapterPosition());
                }
            }
        });
    }

    public void setOnItemSlideListener(OnItemSlideListener onItemSlideListener) {
        this.onItemSlideListener = onItemSlideListener;
    }

    @NonNull
    @Override
    public TagListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListAdapter.ViewHolder holder, int position) {
        TagEntity tagEntity = tagEntityList.get(position);
        holder.idTextView.setText("#" + tagEntity.id);
        holder.nameTextView.setText(tagEntity.name);
        holder.descriptionTextView.setText(tagEntity.description);
    }

    @Override
    public int getItemCount() {
        return this.tagEntityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView nameTextView;
        public TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.tag_id);
            nameTextView = itemView.findViewById(R.id.tag_name);
            descriptionTextView = itemView.findViewById(R.id.tag_description);
        }
    }
}
