package com.example.asus.dailyexpensenote.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.asus.dailyexpensenote.database.MyDBHelper;
import com.example.asus.dailyexpensenote.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddDailyExpenseActivity extends AppCompatActivity {

    private MyDBHelper myDBHelper;

    private Spinner spinner;
    private String[] spinnerList;
    private ArrayAdapter<String> arrayAdapter;

    private EditText amountET,dateET,timeET;
    private Button addDocumentBtn,addExpenseBtn;
    private ImageView dateIV,timeIV,documentIV,documentCancelIV;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private String expenseType,expenseAmount,expenseDate,expenseTime;
    private String idIntent;
    private Bitmap bitmapImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_expense);

        init();

        getDate();

        getTime();

        getUpdateIntent();

        //document cancel image view click action
        documentCancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapImage = null;
                Toast.makeText(AddDailyExpenseActivity.this, "No Document Selected", Toast.LENGTH_SHORT).show();
                documentIV.setImageResource(R.drawable.ic_assignment_black_24dp);
                documentCancelIV.setVisibility(View.INVISIBLE);
            }
        });

        //add document button action
        addDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDailyExpenseActivity.this);
                String[] items = {"From Camera","From Gallery"};
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                });

                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        //add Expense button action
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getDataFromUser();
                getDataFromUser();

                if(idIntent != null){

                    if(!validate()){
                        return;
                    }else {
                        //update data to database
                        long resultId = myDBHelper.updateDataToDatabase(idIntent,expenseType,expenseAmount,expenseDate,expenseTime,bitmapToString(bitmapImage));

                        if(resultId > 0){
                            Toast.makeText(AddDailyExpenseActivity.this, "Row "+resultId+" Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(AddDailyExpenseActivity.this, "Data are not Updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    if(!validate()){
                        return;
                    }else {
                        //insert data to database
                        insertData();
                    }
                }
            }
        });
    }

    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String stringImage=Base64.encodeToString(b, Base64.DEFAULT);
        return stringImage;
    }

    private Bitmap stringToBitmap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,99);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,88);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 88){
                Bundle bundle = data.getExtras();
                bitmapImage = (Bitmap) bundle.get("data");
                documentIV.setImageBitmap(bitmapImage);
                documentCancelIV.setVisibility(View.VISIBLE);
            }
            else if(requestCode == 99){
               //Uri uri = data.getData();
               // bitmapImage = (Bitmap) Uri.parse(uri);
               // documentIV.setImageBitmap(bitmapImage);
            }
        }
    }

    private void getUpdateIntent() {

        idIntent = getIntent().getStringExtra("EXPENSE_ID");
        Bitmap bitmapImageIntent = stringToBitmap(getIntent().getStringExtra("EXPENSE_IMAGE"));

        if(idIntent != null){

            int spinnerItemPosition = arrayAdapter.getPosition(getIntent().getStringExtra("EXPENSE_TYPE"));
            spinner.setSelection(spinnerItemPosition);
            amountET.setText(getIntent().getStringExtra("EXPENSE_AMOUNT"));
            dateET.setText(getIntent().getStringExtra("EXPENSE_DATE"));
            timeET.setText(getIntent().getStringExtra("EXPENSE_TIME"));

            if(bitmapImageIntent != null){
                bitmapImage = bitmapImageIntent;
                documentIV.setImageBitmap(bitmapImage);
                documentCancelIV.setVisibility(View.VISIBLE);
            }else {
                documentIV.setImageResource(R.drawable.ic_assignment_black_24dp);
            }
            setTitle("Update "+getIntent().getStringExtra("EXPENSE_TYPE"));
            addExpenseBtn.setText("Update Expense");
        }
    }

    //check all data empty validation
    private boolean validate() {

        boolean valid = true;

        if(expenseType.equals("Select Expense Type")){
            Toast.makeText(this, "Please Select Expense Type !", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(expenseAmount.isEmpty()){
            amountET.setError("Enter Amount");
            valid = false;
        }else {
            amountET.setError(null);
        }
        if(expenseDate.isEmpty()){
            dateET.setError("Select Date");
            valid = false;
        }else {
            dateET.setError(null);
        }

        return valid;
    }

    //set time to edittextView by clicking date icon
    private void getTime() {

        timeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddDailyExpenseActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hour, minute, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time time = new Time(hourOfDay,minute,00);
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss aa");
                timeET.setText(timeFormatter.format(time).toString());
            }
        };
    }

    //set date to editTextView by clicking date icon
    private void getDate() {

        dateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddDailyExpenseActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                dateET.setText(date);
            }
        };
    }

    // insert data to database
    private void insertData() {

        if(bitmapImage != null){
            long resultId = myDBHelper.insertDataToDatabase(expenseType,expenseAmount,expenseDate,expenseTime,bitmapToString(bitmapImage));

            if(resultId > 0){
                setResult(RESULT_OK);
                Toast.makeText(AddDailyExpenseActivity.this, "Row "+resultId+" inserted Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(AddDailyExpenseActivity.this, "Data are not inserted", Toast.LENGTH_SHORT).show();
            }
        }else {
            long resultId = myDBHelper.insertDataToDatabase(expenseType,expenseAmount,expenseDate,expenseTime,null);

            if(resultId > 0){
                setResult(RESULT_OK);
                Toast.makeText(AddDailyExpenseActivity.this, "Row "+resultId+" inserted Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(AddDailyExpenseActivity.this, "Data are not inserted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //get all data from user input
    private void getDataFromUser() {
        expenseType = spinner.getSelectedItem().toString();
        expenseAmount = amountET.getText().toString().trim();
        expenseDate = dateET.getText().toString().trim();
        expenseTime = timeET.getText().toString().trim();

    }

    //initialize all components
    private void init() {

        myDBHelper = new MyDBHelper(AddDailyExpenseActivity.this);

        spinner = findViewById(R.id.selectExpenseTypeSpinnerId);
        spinnerList = getResources().getStringArray(R.array.spinner_list);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,spinnerList);
        spinner.setAdapter(arrayAdapter);

        amountET = findViewById(R.id.expenseAmountETId);
        dateET = findViewById(R.id.expenseDateETId);
        timeET = findViewById(R.id.expenseTimeETId);

        dateIV = findViewById(R.id.dateIVId);
        timeIV = findViewById(R.id.timeIVId);
        documentIV = findViewById(R.id.documentIVId);
        documentCancelIV = findViewById(R.id.cancelIVId);

        addDocumentBtn = findViewById(R.id.addDocumentBtnId);
        addExpenseBtn = findViewById(R.id.addExpenseBtnId);
    }
}
