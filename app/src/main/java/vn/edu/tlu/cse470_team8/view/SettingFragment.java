package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import vn.edu.tlu.cse470_team8.R;

public class SettingFragment extends Fragment {
    private TextView st_txt_translated;
    private Switch st_switch_language;
    private SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        st_txt_translated = root.findViewById(R.id.st_txt_translate);
        st_switch_language = root.findViewById(R.id.st_sw_translate);
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean translated = sharedPreferences.getBoolean("translated", false);
        st_switch_language.setChecked(translated);
        if (translated) {
            st_txt_translated.setText("Bật");
        } else {
            st_txt_translated.setText("Tắt");
        }
        st_switch_language.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                st_txt_translated.setText("Bật");
                sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("translated", true);
                editor.apply();
            } else {
                st_txt_translated.setText("Tắt");
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("translated", false);
                editor.apply();
            }
        }

        );

        return root;
    }



}
