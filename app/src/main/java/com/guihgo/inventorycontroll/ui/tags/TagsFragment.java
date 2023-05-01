package com.guihgo.inventorycontroll.ui.tags;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.guihgo.inventorycontroll.Helper;
import com.guihgo.inventorycontroll.database.DatabaseHelper;
import com.guihgo.inventorycontroll.database.TagContract;
import com.guihgo.inventorycontroll.model.tag.TagEntity;
import com.guihgo.inventorycontroll.databinding.FragmentTagsBinding;

import java.util.ArrayList;
import java.util.List;

public class TagsFragment extends Fragment {

    private FragmentTagsBinding binding;

    private RecyclerView recyclerView;
    protected TagListAdapter tagListAdapter;

    public List<TagEntity> tagEntities;
    private BottomNavigationView bnvMenu;

    DatabaseHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TagsViewModel tagsViewModel =
                new ViewModelProvider(this).get(TagsViewModel.class);

        binding = FragmentTagsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(getContext());

        /* Load from database */
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        tagEntities = new ArrayList<>();
        String[] projection = {
                TagContract.TagEntry.COLUMN_NAME_ID,
                TagContract.TagEntry.COLUMN_NAME_NAME,
                TagContract.TagEntry.COLUMN_NAME_DESCRIPTION
        };

        String sortOrder = TagContract.TagEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(TagContract.TagEntry.TABLE_NAME, projection, null,null,null,null, sortOrder);

        while(cursor.moveToNext()) {
            tagEntities.add(new TagEntity(
                    cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_DESCRIPTION))
            ));
        }
        cursor.close();


        tagListAdapter = new TagListAdapter(tagEntities);

        recyclerView = binding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        tagListAdapter.setupRecycleView(recyclerView);

        tagListAdapter.setOnItemSlideListener(new TagListAdapter.OnItemSlideListener() {
            @Override
            public void onItemSlideLeft(int position) {
                Toast.makeText(getContext(), "Item " + tagListAdapter.tagEntityList.get(position).name + " was removed", Toast.LENGTH_SHORT).show();
                tagListAdapter.tagEntityList.remove(position);
                tagListAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onItemSlideRight(int position) {

            }
        });

        bnvMenu = binding.bottomNavigation;

        bnvMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Helper.confirmTouch(getContext());
                Toast.makeText(getContext(), item.getItemId() +" Cliecked on " + item.toString(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        return super.onGetLayoutInflater(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}