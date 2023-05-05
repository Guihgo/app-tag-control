package com.guihgo.tagcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.guihgo.tagcontrol.database.DatabaseHelper;
import com.guihgo.tagcontrol.database.TagContract;
import com.guihgo.tagcontrol.databinding.ActivityMainBinding;
import com.guihgo.tagcontrol.databinding.AppBarMainBinding;
import com.guihgo.tagcontrol.model.tag.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    NavController navController;

    private MaterialSwitch switchTheme;
    private boolean isDarkTheme;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper.restoreApplicationLocale(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        this.dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* Initial database population */
        List<TagEntity> tagEntities = new ArrayList<>();
//        tagEntities.add(new TagEntity("AABBCCDD", "Tire Good Year 177/09", "A good tire for your car"));
//        tagEntities.add(new TagEntity("FFDDCCBB", "Tire Used 1998", "A good tire for your bike"));
//        tagEntities.add(new TagEntity("AAAAAAAA", "Tire A ", "A good tire A"));
//        tagEntities.add(new TagEntity("BBBBBBBB", "Tire B ", "A good tire B"));

        ContentValues cvTags = new ContentValues();
        for (int i = 0; i < tagEntities.size(); i++) {
            cvTags.put(TagContract.TagEntry.COLUMN_NAME_ID, tagEntities.get(i).id);
            cvTags.put(TagContract.TagEntry.COLUMN_NAME_NAME, tagEntities.get(i).name);
            cvTags.put(TagContract.TagEntry.COLUMN_NAME_DESCRIPTION, tagEntities.get(i).description);

            db.insert(TagContract.TagEntry.TABLE_NAME, null, cvTags);
        }

//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tags, R.id.nav_inventory, R.id.nav_settings)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        switchTheme = (MaterialSwitch) navigationView.getHeaderView(0).findViewById(R.id.switch_theme);
        switchTheme.setChecked(this.isDarkTheme);
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String themeStyle = (isChecked) ? "dark" : "light";
                Helper.saveTheme(getApplicationContext(), themeStyle);
                recreate();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(getIntent().hasCategory(CATEGORY_GO_TO_SETTINGS)) {
////            Toast.makeText(this, "Must go to settings", Toast.LENGTH_SHORT).show();
////            this.navController.naviga
//            this.navController.getGraph().setStartDestination(R.id.nav_settings);
//        }
    }

    @Override
    public void setTheme(int resId) {
        this.isDarkTheme = Helper.restoreTheme(this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        Helper.stopReadNFC(this);
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//
//            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//            Toast.makeText(this, "Scanned tag with id #"+tag.getId().toString(), Toast.LENGTH_SHORT).show();

//            Parcelable[] rawMessages =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMessages != null) {
//                NdefMessage[] messages = new NdefMessage[rawMessages.length];
//                for (int i = 0; i < rawMessages.length; i++) {
//                    messages[i] = (NdefMessage) rawMessages[i];
//                }
//                // Process the messages array.
//            }
//        }

    }
}