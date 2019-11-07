package com.example.asus.dailyexpensenote.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.dailyexpensenote.R;
import com.example.asus.dailyexpensenote.activity.AddDailyExpenseActivity;
import com.example.asus.dailyexpensenote.activity.MainActivity;
import com.example.asus.dailyexpensenote.database.MyDBHelper;
import com.example.asus.dailyexpensenote.fragment.BottomSheetFragment;
import com.example.asus.dailyexpensenote.fragment.ExpensesFragment;
import com.example.asus.dailyexpensenote.model_class.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private Context context;
    private TextView expenseType,expenseAmount,expenseDate,expenseTime;
    private Button showDocumentBtn;

    public ExpenseAdapter() {

    }

    public ExpenseAdapter(List<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_design,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Expense expense = expenseList.get(position);

        viewHolder.expenseTypeTV.setText(expense.getExpenseType());
        viewHolder.expenseDateTV.setText(expense.getExpenseDate());
        viewHolder.expenseAmountTV.setText(expense.getExpenseAmount());

        //recycler view item click event to show details
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet, null);
                expenseType = view.findViewById(R.id.expenseTypeTVId);
                expenseAmount = view.findViewById(R.id.expenseAmountTVId);
                expenseDate = view.findViewById(R.id.expenseDateTVId);
                expenseTime = view.findViewById(R.id.expenseTimeTVId);
                showDocumentBtn = view.findViewById(R.id.showDocumentBtnId);

                expenseType.setText(expense.getExpenseType());
                expenseAmount.setText(expense.getExpenseAmount()+" BDT");
                expenseDate.setText(expense.getExpenseDate());

                //time empty checking
                if(expense.getExpenseTime() == null || expense.getExpenseTime().isEmpty()){
                    expenseTime.setText("No time Added");
                }else {
                    expenseTime.setText(expense.getExpenseTime());
                }

                showDocumentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog builder = new Dialog(context);
                        View  view = LayoutInflater.from(context).inflate(R.layout.image_view_layout_design,null);
                        builder.setTitle("Document of "+expense.getExpenseType());
                        builder.setContentView(view);

                        ImageView imageView = view.findViewById(R.id.imageViewLayoutDesignId);
                        imageView.setImageBitmap(stringToBitmap(expense.getExpenseImage()));
                        builder.show();
                    }
                });

                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(view);
                dialog.show();
            }
        });

        //recycler view more button click action
        viewHolder.moreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,viewHolder.moreIV);
                popupMenu.inflate(R.menu.edit_menu_item);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.updateOptionId:
                                //update option click action
                                update(expense);

                                return true;

                            case R.id.deleteOptionId:
                                //delete option click action
                                Cursor cursor = ExpensesFragment.myDBHelper.getData("SELECT id FROM expense");
                                List<Integer> id = new ArrayList<>();

                                while (cursor.moveToNext()){
                                    id.add(cursor.getInt(0));
                                }

                                showDeleteDialog(id.get(position),position);

                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
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

    private void update(Expense expense) {

        Intent intent = new Intent(context,AddDailyExpenseActivity.class);

        intent.putExtra("EXPENSE_ID",expense.getId());
        intent.putExtra("EXPENSE_TYPE",expense.getExpenseType());
        intent.putExtra("EXPENSE_AMOUNT",expense.getExpenseAmount());
        intent.putExtra("EXPENSE_DATE",expense.getExpenseDate());
        intent.putExtra("EXPENSE_TIME",expense.getExpenseTime());
        intent.putExtra("EXPENSE_IMAGE",expense.getExpenseImage());

        context.startActivity(intent);
    }

    //show delete dialog to delete
    private void showDeleteDialog(final int rowId, final int position) {

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("Warning!");
        deleteDialog.setMessage("Are you sure to delete?");

        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ExpensesFragment.myDBHelper.deleteDataFromDatabase(rowId);
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                    expenseList.remove(position);
                    notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(context, "Exception: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView expenseTypeTV,expenseDateTV,expenseAmountTV;
        private ImageView moreIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            expenseTypeTV = itemView.findViewById(R.id.expenseTypeTVId);
            expenseDateTV = itemView.findViewById(R.id.expenseDateTVId);
            expenseAmountTV = itemView.findViewById(R.id.expenseAmountTVId);
            moreIV = itemView.findViewById(R.id.moreIVId);
        }
    }
}
