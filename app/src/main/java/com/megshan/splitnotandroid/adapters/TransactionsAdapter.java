package com.megshan.splitnotandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.megshan.splitnotandroid.AddTransactionToSplitwiseActivity;
import com.megshan.splitnotandroid.dto.Transaction;
import com.plaid.splitnotandroid.R;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private static String LOGGER = "TransactionsAdapter";
    private static String EXTRA_TRANSACTION_NAME = "EXTRA_TRANSACTION_NAME";
    private static String EXTRA_TRANSACTION_AMOUNT = "EXTRA_TRANSACTION_AMOUNT";

    private Context context;
    private List<Transaction> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionsAdapter(List<Transaction> myDataset, Context mContext) {
        mDataset = myDataset;
        context = mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOGGER, "clicked view with id=" + mDataset.get(position).getName());
                Intent intent = new Intent(context, AddTransactionToSplitwiseActivity.class);
                intent.putExtra(EXTRA_TRANSACTION_NAME, mDataset.get(position).getName());
                intent.putExtra(EXTRA_TRANSACTION_AMOUNT, mDataset.get(position).getAmount());
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}