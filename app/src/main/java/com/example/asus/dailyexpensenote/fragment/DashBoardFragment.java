package com.example.asus.dailyexpensenote.fragment;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.dailyexpensenote.R;
import com.example.asus.dailyexpensenote.database.MyDBHelper;
import com.example.asus.dailyexpensenote.model_class.Expense;

import java.util.ArrayList;
import java.util.Calendar;

public class DashBoardFragment extends Fragment {

    private MyDBHelper myDBHelper;

    private Spinner spinner;
    private String[] spinnerList;
    private ArrayAdapter<String> arrayAdapter;

    private TextView fromDateTV,toDateTV,totalExpenseTV;
    private ImageView fromDateIV,toDateIV;

    private DatePickerDialog.OnDateSetListener mFromDateSetListener;
    private DatePickerDialog.OnDateSetListener mToDateSetListener;

    private String fromDate;

    public DashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        init(view);

        getFromDate();

        getToDate();

        //show total expense based on spinner selected item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }else if(position == 1){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Electricity Bill'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
                else if(position == 2){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Transport Cost'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
                else if(position == 3){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Medical Cost'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
                else if(position == 4){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Lunch'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
                else if(position == 5){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Dinner'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
                else if(position == 6){
                    Cursor cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_type = 'Others'");
                    if (cursor.moveToFirst()) {
                        int total = cursor.getInt(cursor.getColumnIndex("total"));
                        totalExpenseTV.setText(String.valueOf(total));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //set date to fromDate TextView by clicking from date icon
    private void getFromDate() {

        fromDateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mFromDateSetListener,
                        year, month, day
                );
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.setTitle("Please select date");
                datePickerDialog.show();
            }
        });

        mFromDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                fromDate = dayOfMonth + "/" + month + "/" + year;
                fromDateTV.setText(fromDate);
            }
        };
    }

    //set date to toDate TextView by clicking to date icon
    private void getToDate() {

            toDateIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(fromDate != null){
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                mToDateSetListener,
                                year, month, day
                        );
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.setTitle("Please select date");
                        datePickerDialog.show();
                    }else {
                        Toast.makeText(getActivity(), "Select From Date First", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mToDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    String toDate = dayOfMonth + "/" + month + "/" + year;
                    toDateTV.setText(toDate);
                    setDataAccordingToDate(toDate);
                }
            };

    }

    //show data according to date
    private void setDataAccordingToDate(String toDate) {
        String selectedItem = spinner.getSelectedItem().toString();
        Cursor cursor;

        switch (selectedItem) {
            case "Select Expense Type":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Electric Bill":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Transport Cost":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Medical Cost":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Lunch":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Dinner":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;

            case "Others":
                cursor = myDBHelper.getData("SELECT SUM (expense_amount) AS total FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("total"));
                    totalExpenseTV.setText(String.valueOf(total));
                }
                break;
        }
    }


    //initialize all components
    private void init(View view) {

        myDBHelper = new MyDBHelper(getActivity());

        spinner = view.findViewById(R.id.selectExpenseTypeSpinnerId);
        spinnerList = getResources().getStringArray(R.array.spinner_list);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,spinnerList);
        spinner.setAdapter(arrayAdapter);

        fromDateTV = view.findViewById(R.id.fromDateTVId);
        toDateTV = view.findViewById(R.id.toDateTVId);
        fromDateIV = view.findViewById(R.id.fromDateIVId);
        toDateIV = view.findViewById(R.id.toDateIVId);
        totalExpenseTV = view.findViewById(R.id.totalExpenseTVId);

    }

}
