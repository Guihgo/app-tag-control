package com.guihgo.inventorycontroll.ui.tags;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.guihgo.inventorycontroll.R;
import com.guihgo.inventorycontroll.database.DatabaseHelper;
import com.guihgo.inventorycontroll.database.TagContract;
import com.guihgo.inventorycontroll.databinding.ActivityTagAddEditBinding;
import com.guihgo.inventorycontroll.model.tag.TagEntity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TagAddEdit extends AppCompatActivity {

    public static final String KEY_ID = "id";

    private ActivityTagAddEditBinding binding;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    private boolean isAddMode;
    TagEntity tagEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTagAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(KEY_ID);
        this.isAddMode = id == null;

        getSupportActionBar().setTitle(((this.isAddMode) ? getString(R.string.action_add):getString(R.string.action_edit)) + " Tag");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        /* Load entity for edit mode */
        if(this.isAddMode) {
            tagEntity = new TagEntity("", "", "");
        } else {
            String[] projection = {
                    TagContract.TagEntry.COLUMN_NAME_ID,
                    TagContract.TagEntry.COLUMN_NAME_NAME,
                    TagContract.TagEntry.COLUMN_NAME_DESCRIPTION
            };

            String where = TagContract.TagEntry.COLUMN_NAME_ID + " = ?";
            String[] whereArgs = {id};

            Cursor cursor = db.query(TagContract.TagEntry.TABLE_NAME, projection, where,whereArgs, null, null, null);
            if(cursor.moveToFirst()) {
                tagEntity = new TagEntity(
                        cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TagContract.TagEntry.COLUMN_NAME_DESCRIPTION))
                );
            } else {
                onSupportNavigateUp();
            }
            cursor.close();
        }

        binding.tagId.getEditText().setText(tagEntity.id);
        binding.tagId.setOnClickListener(v -> {
            binding.tagId.setErrorEnabled(false);
        });
        binding.tagId.setErrorIconOnClickListener(v -> {
            binding.tagId.setErrorEnabled(false);
        });
        binding.tagId.setEndIconOnClickListener(v -> {
            Toast.makeText(this, "scan now", Toast.LENGTH_SHORT).show();
        });

        binding.tagName.getEditText().setText(tagEntity.name);
        binding.tagDescription.getEditText().setText(tagEntity.description);
    }

    void save() {
        if(binding.tagId.getEditText().getText().length() == 0 || binding.tagId.getEditText().getText().toString() == "") {
            binding.tagId.setError(getString(R.string.tag_id_required));
            binding.tagId.setErrorEnabled(true);
            return;
        }
        ContentValues cvTag = new ContentValues();
        cvTag.put(TagContract.TagEntry.COLUMN_NAME_ID, binding.tagId.getEditText().getText().toString());
        cvTag.put(TagContract.TagEntry.COLUMN_NAME_NAME, binding.tagName.getEditText().getText().toString());
        cvTag.put(TagContract.TagEntry.COLUMN_NAME_DESCRIPTION, binding.tagDescription.getEditText().getText().toString());

        if(this.isAddMode) {
            db.insert(TagContract.TagEntry.TABLE_NAME, null, cvTag);
        } else {
            String where = TagContract.TagEntry.COLUMN_NAME_ID + " = ?";
            String[] whereArgs = { tagEntity.id };
            db.update(TagContract.TagEntry.TABLE_NAME, cvTag, where, whereArgs);
        }
        this.setResult(Activity.RESULT_OK);
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_add_edit, menu);
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
}