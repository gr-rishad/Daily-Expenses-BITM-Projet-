package com.example.asus.dailyexpensenote.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.asus.dailyexpensenote.model_class.Expense;
import com.example.asus.dailyexpensenote.adapter.ExpenseAdapter;
import com.example.asus.dailyexpensenote.database.MyDBHelper;
import com.example.asus.dailyexpensenote.R;
import com.example.asus.dailyexpensenote.activity.AddDailyExpenseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment {

    public static MyDBHelper myDBHelper;
    private ExpenseAdapter expenseAdapter;

    private Spinner spinner;
    private String[] spinnerList;
    private ArrayAdapter<String> arrayAdapter;

    private TextView fromDateTV,toDateTV;
    private ImageView fromDateIV,toDateIV;

    private DatePickerDialog.OnDateSetListener mFromDateSetListener;
    private DatePickerDialog.OnDateSetListener mToDateSetListener;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private List<Expense> expenseList;

    private String fromDate;

    public ExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_expenses, container, false);

        init(view);

        getFromDate();

        getToDate();

        getData();

        populateDataToRecyclerView();

        //show expenses based on spinner selected item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    getData();
                    populateDataToRecyclerView();
                }else if(position == 1){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Electricity Bill'");
                    setData(cursor);
                }
                else if(position == 2){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Transport Cost'");
                    setData(cursor);
                }
                else if(position == 3){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Medical Cost'");
                    setData(cursor);
                }
                else if(position == 4){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Lunch'");
                    setData(cursor);
                }
                else if(position == 5){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Dinner'");
                    setData(cursor);
                }
                else if(position == 6){
                    Cursor cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Others'");
                    setData(cursor);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //floating action button actions here to add new expense
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddDailyExpenseActivity.class);
                startActivityForResult(intent,100);
            }
        });

        //hide fab icon on scroll up
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return view;
    }

    private void setData(Cursor cursor) {
        expenseList.clear();
        while (cursor.moveToNext()) {
            String expenseId = cursor.getString(0);
            String expenseType = cursor.getString(1);
            String expenseAmount = cursor.getString(2);
            String expenseDate = cursor.getString(3);
            String expenseTime = cursor.getString(4);
            String expenseImage = cursor.getString(5);
            expenseList.add(new Expense(expenseId,expenseType,expenseAmount,expenseDate,expenseTime,expenseImage));
        }
        populateDataToRecyclerView();
    }

    //refresh data after adding
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 100 && resultCode == getActivity().RESULT_OK){
            getData();
            populateDataToRecyclerView();
            Toast.makeText(getActivity(), "Data saved Successfully", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //set all data to recycler view
    public void populateDataToRecyclerView() {
        expenseAdapter = new ExpenseAdapter(expenseList,getActivity());
        expenseAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(expenseAdapter);
    }

    //getDataFromDatabase
    public void getData() {
        expenseList.clear();
        Cursor cursor = myDBHelper.getDataFromDatabase();

        while (cursor.moveToNext()) {

            String expenseId = cursor.getString(0);
            String expenseType = cursor.getString(1);
            String expenseAmount = cursor.getString(2);
            String expenseDate = cursor.getString(3);
            String expenseTime = cursor.getString(4);
            String expenseImage = cursor.getString(5);

            expenseList.add(new Expense(expenseId,expenseType,expenseAmount,expenseDate,expenseTime,expenseImage));
        }
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

    private void setDataAccordingToDate(String toDate) {

        String selectedItem = spinner.getSelectedItem().toString();
        Cursor cursor;

        switch (selectedItem){
            case "Select Expense Type":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Electric Bill":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Electricity Bill' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Transport Cost":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Transport Cost' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Medical Cost":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Medical Cost' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Lunch":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Lunch' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Dinner":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Dinner' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
                break;

            case "Others":
                cursor = myDBHelper.getData("SELECT * FROM expense WHERE expense_type = 'Others' AND expense_date BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
                setData(cursor);
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

        recyclerView = view.findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab = view.findViewById(R.id.fabId);
        expenseList = new ArrayList<Expense>();
    }

}
