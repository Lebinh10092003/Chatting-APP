package vn.edu.tlu.cse470_team8.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.tlu.cse470_team8.R;

public class MessageFragment extends Fragment {

        public MessageFragment() {
            // Required empty public constructor
        }

        public static MessageFragment newInstance() {
            MessageFragment fragment = new MessageFragment();
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_message, container, false);
        }

}