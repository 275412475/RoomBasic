package com.example.mywords;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private WordViewMudel viewMudel;
    private Button buttonSubmit;
    private EditText editTextEnglishi,editTextChinese;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonSubmit =requireActivity().findViewById(R.id.button2);
        editTextChinese=requireActivity().findViewById(R.id.editTextChinese);
        editTextEnglishi=requireActivity().findViewById(R.id.editTextEnglishi);
        viewMudel = ViewModelProviders.of(requireActivity()).get(WordViewMudel.class);
        buttonSubmit.setEnabled(false);
        editTextEnglishi.requestFocus();
        InputMethodManager  imm= (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null){
            imm.showSoftInput(editTextEnglishi,0);
        }

        TextWatcher watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String english=editTextEnglishi.getText().toString().trim();
                String  chinese=editTextChinese.getText().toString().trim();
                buttonSubmit.setEnabled(!english.isEmpty()&&!chinese.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextEnglishi.addTextChangedListener(watcher);
        editTextChinese.addTextChangedListener(watcher);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english=editTextEnglishi.getText().toString().trim();
                String  chinese=editTextChinese.getText().toString().trim();
                Word word=new Word(english,chinese,0);
                viewMudel.insetWords(word);
                NavController controller= Navigation.findNavController(v);
                controller.navigateUp();
                InputMethodManager  imm= (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm!=null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        });
    }
}
