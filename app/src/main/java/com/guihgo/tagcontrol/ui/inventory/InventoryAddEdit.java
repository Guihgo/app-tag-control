package com.guihgo.tagcontrol.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.guihgo.tagcontrol.Helper;
import com.guihgo.tagcontrol.R;
import com.guihgo.tagcontrol.database.DatabaseHelper;
import com.guihgo.tagcontrol.database.InventoryContract;
import com.guihgo.tagcontrol.database.TagContract;
import com.guihgo.tagcontrol.databinding.ActivityInventoryAddEditBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InventoryAddEdit extends AppCompatActivity {

    private ActivityInventoryAddEditBinding binding;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    Calendar expirationDate;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInventoryAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.inventory);

        id = getIntent().getIntExtra(InventoryContract.InventoryEntry.COLUMN_NAME_ID, -1);

        if (id == -1) {
            this.onSupportNavigateUp();
            return;
        }

        expirationDate = Calendar.getInstance();

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        this.updateUI(id);

        binding.inventoryQuantityPlus.setOnClickListener((v) -> {
            this.plusMinusQuantity(true);
        });
        binding.inventoryQuantityMinus.setOnClickListener((v) -> {
            this.plusMinusQuantity(false);
        });

        binding.inventoryExpirationDate.setEndIconOnClickListener((v) -> {
            this.showExpirationDateDialog();
        });
        binding.inventoryExpirationDate.getEditText().setOnClickListener((v) -> {
            this.showExpirationDateDialog();
        });
        binding.inventoryExpirationDate.setOnClickListener((v) -> {
            this.showExpirationDateDialog();
        });

        binding.inventoryQuantity.setOnClickListener(v -> {
            binding.inventoryQuantity.setErrorEnabled(false);
        });
        binding.inventoryQuantity.setErrorIconOnClickListener(v -> {
            binding.inventoryQuantity.setErrorEnabled(false);
        });
    }

    void showExpirationDateDialog() {
        Helper.confirmTouch(this);
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(expirationDate.getTimeInMillis())
                .setTitleText(R.string.inventory_expiration_date)
                .build();

        datePicker.addOnPositiveButtonClickListener((s) -> {
            expirationDate.setTimeInMillis(Long.parseLong(datePicker.getSelection().toString()));
            expirationDate.add(Calendar.DAY_OF_YEAR, 1);
            Calendar now = Calendar.getInstance();
            expirationDate.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            expirationDate.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            expirationDate.set(Calendar.SECOND, now.get(Calendar.SECOND));
            this.updateExpirationDateUI();
        });

        datePicker.show(getSupportFragmentManager(), InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION);
    }

    void updateExpirationDateUI() {
        binding.inventoryExpirationDate.getEditText().setText((new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())).format(expirationDate.getTime()));
    }

    void plusMinusQuantity(boolean toPlus) {
        Helper.confirmTouch(this);
        int quantity = Integer.parseInt(binding.inventoryQuantity.getEditText().getText().toString());
        if (toPlus) {
            quantity++;
        } else {
            quantity--;
            if (quantity < 0) quantity = 0;
        }
        binding.inventoryQuantity.getEditText().setText("" + quantity);
    }

    void updateUI(int id) {
        /* Load entity */
        String tableWithJoin = InventoryContract.InventoryEntry.TABLE_NAME +
                " INNER JOIN " + TagContract.TagEntry.TABLE_NAME +
                " ON " + InventoryContract.InventoryEntry.TABLE_NAME + "." + InventoryContract.InventoryEntry.COLUMN_NAME_TAG_ID +
                " = " + TagContract.TagEntry.TABLE_NAME + "." + TagContract.TagEntry.COLUMN_NAME_ID;

        String[] projection = {"*"};

        String where = InventoryContract.InventoryEntry.COLUMN_NAME_ID + " = ?";
        String[] whereArgs = {"" + id};

        Cursor cursor = db.query(tableWithJoin, projection, where, whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            binding.inventoryTagId.getEditText().setText(cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_ID)));

            binding.inventoryTagName.getEditText().setText(cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_NAME)));

            expirationDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION)));
            this.updateExpirationDateUI();

            binding.inventoryQuantity.getEditText().setText("" + cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY)));
        } else {
            cursor.close();
            Toast.makeText(this, "Inventory #" + id + " " + getString(R.string.not_found), Toast.LENGTH_SHORT).show();
            this.onSupportNavigateUp();
            return;
        }
        cursor.close();

    }

    void save() {
        if (binding.inventoryQuantity.getEditText().getText().length() == 0 || binding.inventoryQuantity.getEditText().getText().toString() == "") {
            binding.inventoryQuantity.setError(getString(R.string.inventory_quantity_required));
            binding.inventoryQuantity.setErrorEnabled(true);
            return;
        }
        int quantity = Integer.parseInt(binding.inventoryQuantity.getEditText().getText().toString());
        if (quantity < 0) {
            binding.inventoryQuantity.setError(getString(R.string.inventory_quantity_too_low));
            binding.inventoryQuantity.setErrorEnabled(true);
            return;
        }

        ContentValues cvInventory = new ContentValues();
        cvInventory.put(InventoryContract.InventoryEntry.COLUMN_NAME_EXPIRATION, expirationDate.getTimeInMillis());
        cvInventory.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, quantity);

        String where = InventoryContract.InventoryEntry.COLUMN_NAME_ID + " = ?";
        String[] whereArgs = {"" + id};
        db.update(InventoryContract.InventoryEntry.TABLE_NAME, cvInventory, where, whereArgs);

        this.setResult(Activity.RESULT_OK);
        onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                this.save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}