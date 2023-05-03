package com.guihgo.tagcontrol.ui.tag;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guihgo.tagcontrol.R;
import com.guihgo.tagcontrol.model.tag.TagEntity;
import com.guihgo.tagcontrol.ui.helper.OnItemInteractionListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    public List<TagEntity> tagEntityList;

    public ItemTouchHelper itemTouchHelper;

    private OnItemInteractionListener onItemInteractionListener = null;

    public void setupRecycleView(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public TagListAdapter(@Nullable List<TagEntity> tagEntityList) {
        this.tagEntityList = (tagEntityList == null) ? new ArrayList<>() : tagEntityList;

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
                if(adapter.onItemInteractionListener == null) return;

                if(direction == ItemTouchHelper.END) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemInteractionListener.onItemClick(holder.getAbsoluteAdapterPosition());
            }
        });
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
