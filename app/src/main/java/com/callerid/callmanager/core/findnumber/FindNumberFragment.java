package com.callerid.callmanager.core.findnumber;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.callerid.callmanager.R;


public class FindNumberFragment extends Fragment {

    LinearLayout llToolbarSearch, llToolbar, llSearchNotFound;
    AppCompatImageView imgSearch, imgBack,imgClose;
    AppCompatEditText edSearch;
    AppCompatTextView txtSearchNumber;

    public FindNumberFragment() {
        // Required empty public constructor
    }


    public static FindNumberFragment newInstance() {
        FindNumberFragment fragment = new FindNumberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_number, container, false);

        llToolbarSearch = view.findViewById(R.id.llToolbarSearch);
        llToolbar = view.findViewById(R.id.llToolbar);
        llSearchNotFound = view.findViewById(R.id.llSearchNotFound);
        llSearchNotFound.setVisibility(View.GONE);
        imgSearch = view.findViewById(R.id.imgSearch);
        imgClose = view.findViewById(R.id.imgClose);
        imgBack = view.findViewById(R.id.imgBack);
        edSearch = view.findViewById(R.id.edSearch);
        txtSearchNumber = view.findViewById(R.id.txtSearchNumber);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && edSearch != null) {
                    imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
                    edSearch.clearFocus(); // Remove focus from EditText
                }

                llToolbarSearch.setVisibility(View.GONE);
                llToolbar.setVisibility(View.VISIBLE);
                llSearchNotFound.setVisibility(View.GONE);
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llToolbarSearch.setVisibility(View.VISIBLE);
                llToolbar.setVisibility(View.GONE);
            }
        });

       /* edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                edSearch.requestFocus();
                edSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(edSearch, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });
            }
        });*/

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edSearch.setText("");
            }
        });
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty())
                    llSearchNotFound.setVisibility(View.GONE);
                else {
                    llSearchNotFound.setVisibility(View.VISIBLE);
                    txtSearchNumber.setText("Searched “" + editable.toString() + "” is not find on Caller ID");
                }
            }
        });

        return view;
    }
}