package com.guihgo.tagcontrol.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.guihgo.tagcontrol.Helper;
import com.guihgo.tagcontrol.R;
import com.guihgo.tagcontrol.database.InventoryContract;
import com.guihgo.tagcontrol.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    MaterialAutoCompleteTextView language;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        language = (MaterialAutoCompleteTextView) binding.language.getEditText();
        language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "item clicked : " + language.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();

                Locale locale = Helper.getLocaleByPosition(getContext(), position);
                Helper.setApplicationLocale(getContext(), locale);
                Helper.saveApplicationLocale(getContext(), locale);

                getActivity().recreate();
            }
        });

        language.setHint(Helper.getApplicationLanguage(getContext(), Helper.getApplicationLocale(getContext())));
        language.requestFocus();

        binding.about.setOnClickListener((v)->{

            MaterialAlertDialogBuilder madbConfirm = new MaterialAlertDialogBuilder(getContext());

            String title = getContext().getString(R.string.app_name) + " - "+ getContext().getString(R.string.app_version);

            String message = getContext().getString(R.string.settings_about_message);
            madbConfirm.setTitle(title);
            madbConfirm.setMessage(message);
            madbConfirm.setIcon(R.drawable.vector);

            madbConfirm.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}