package com.guihgo.tagcontrol.ui.inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.guihgo.tagcontrol.Helper;
import com.guihgo.tagcontrol.R;
import com.guihgo.tagcontrol.database.DatabaseHelper;
import com.guihgo.tagcontrol.database.InventoryContract;
import com.guihgo.tagcontrol.database.TagContract;
import com.guihgo.tagcontrol.databinding.FragmentInventoriesBinding;
import com.guihgo.tagcontrol.model.inventory.InventoryEntity;
import com.guihgo.tagcontrol.ui.helper.OnItemInteractionListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InventoriesFragment extends Fragment {

    private FragmentInventoriesBinding binding;
    private RecyclerView recyclerView;
    protected InventoryListAdapter inventoryListAdapter;
    private BottomNavigationView bnvMenu;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                refreshData();
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InventoryViewModel inventoryViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);

        binding = FragmentInventoriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        inventoryListAdapter = new InventoryListAdapter(null);
        inventoryListAdapter.setupRecycleView(recyclerView);

        dbHelper = new DatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();

        this.refreshData();

        inventoryListAdapter.setOnItemInteractionListener(new OnItemInteractionListener() {
            @Override
            public void onItemSwipedLeft(int position) {
                InventoryEntity item = inventoryListAdapter.inventoryEntityList.get(position);

                MaterialAlertDialogBuilder madbConfirm = new MaterialAlertDialogBuilder(getContext());

                String title = getContext().getString(R.string.dialog_confirm_remove_title);

                String message = getContext().getString(R.string.dialog_confirm_remove_message)
                        .replace("{{entity_name}}", InventoryContract.InventoryEntry.TABLE_NAME)
                        .replace("{{entity_id}}", item.quantity + "x (" + item.tagName+")");

                madbConfirm.setTitle(title);
                madbConfirm.setMessage(message);

                madbConfirm.setNeutralButton(getResources().getString(R.string.dialog_confirm_remove_button_cancel), (dialog, which) -> {
                    inventoryListAdapter.notifyItemChanged(position);
                });

                madbConfirm.setPositiveButton(getResources().getString(R.string.dialog_confirm_remove_button_remove), (dialog, which) -> {

                    String where = InventoryContract.InventoryEntry.COLUMN_NAME_ID + " = ?";
                    String[] whereArgs = {"" + item.id};
                    db.delete(InventoryContract.InventoryEntry.TABLE_NAME, where, whereArgs);
                    inventoryListAdapter.inventoryEntityList.remove(position);
                    inventoryListAdapter.notifyItemRemoved(position);
                });

                madbConfirm.show();
            }

            @Override
            public void onItemSwipedRight(int position) {

            }

            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), InventoryAddEdit.class);
                intent.putExtra(InventoryContract.InventoryEntry.COLUMN_NAME_ID, inventoryListAdapter.inventoryEntityList.get(position).id);
                startForResult.launch(intent);
            }

            @Override
            public boolean onTemLongClick(int position, View v) {
                return false;
            }
        });

        bnvMenu = binding.bottomNavigation;

        bnvMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Helper.confirmTouch(getContext());
                switch (item.getItemId()) {
                    case R.id.inventories_scan:
                        Helper.scanTag(getActivity(), new Helper.OnScanTagListener() {
                            @Override
                            public void onStop() {
//                                Toast.makeText(getContext(), "stopped!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onScanned(Tag tag) {
                                if (Looper.myLooper() == null) {
                                    Looper.prepare();
                                }

                                String tagId = Helper.bytesToHex(tag.getId());

                                String[] projection = {"*"};

                                String whereTag = TagContract.TagEntry.COLUMN_NAME_ID + " = ?";
                                String[] whereArgsTag = {tagId};

                                Cursor cursorTag = db.query(TagContract.TagEntry.TABLE_NAME, projection, whereTag, whereArgsTag, null, null, null);
                                if (!cursorTag.moveToFirst()) {
                                    Toast.makeText(getContext(), "Tag #" + tagId + " " + getString(R.string.not_found), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                InventoriesFragment.goToInventory(tagId, getActivity(), startForResult, db);

                            }
                        });
                        return true;
                }
                return false;
            }
        });

        return root;
    }

    public static void goToInventory(String tagId, Activity activity, ActivityResultLauncher<Intent> startForResult, SQLiteDatabase db) {
        String[] projection = {"*"};
        String where = InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID + " = ?";
        String[] whereArgs = {tagId};

        Cursor cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, where, whereArgs, null, null, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_ID));
        } else {
            /* Insert one linked with tag id */
            ContentValues cvInventory = new ContentValues();
            cvInventory.put(InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID, tagId);
            cvInventory.put(InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION, (Calendar.getInstance().getTimeInMillis()));
            cvInventory.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, 0);
            id = (int) db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, cvInventory);
        }
        cursor.close();

        Intent intent = new Intent(activity, InventoryAddEdit.class);
        intent.putExtra(InventoryContract.InventoryEntry.COLUMN_NAME_ID, id);
        startForResult.launch(intent);
    }

    void refreshData() {
        inventoryListAdapter.inventoryEntityList = new ArrayList<>();

        String tableWithJoin = InventoryContract.InventoryEntry.TABLE_NAME +
                " INNER JOIN " + TagContract.TagEntry.TABLE_NAME +
                " ON " + InventoryContract.InventoryEntry.TABLE_NAME + "." + InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID +
                " = " + TagContract.TagEntry.TABLE_NAME + "." + TagContract.TagEntry.COLUMN_NAME_ID;

        String[] projection = {"*"};

        String sortOrder = TagContract.TagEntry.TABLE_NAME + "." + TagContract.TagEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(tableWithJoin, projection, null, null, null, null, sortOrder);

        for (String s : cursor.getColumnNames()) {
            Log.d("INV_DB", s);
        }

        while (cursor.moveToNext()) {
            inventoryListAdapter.inventoryEntityList.add(new InventoryEntity(
                    cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_NAME)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY))
            ));
        }
        cursor.close();

        inventoryListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}