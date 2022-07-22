package com.example.exchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class transactionAdapter extends FirestoreRecyclerAdapter<
        Transaction, transactionAdapter.transactionViewholder> {

    public transactionAdapter(@NonNull FirestoreRecyclerOptions<Transaction> options) {
        super(options);
    }

    @Override
    public transactionViewholder onCreateViewHolder(ViewGroup group, int i) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.transaction, group, false);
        return new transactionViewholder(view);
    }


    public class transactionViewholder extends RecyclerView.ViewHolder {
        TextView Reference, Amount, Type, Remark, Date;
        public transactionViewholder(@NonNull View itemView) {
            super(itemView);

            Remark = itemView.findViewById(R.id.remark);
            Date = itemView.findViewById(R.id.Date);
            Amount = itemView.findViewById(R.id.amount);
            Reference = itemView.findViewById(R.id.reference);
            Type = itemView.findViewById(R.id.type);
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull transactionViewholder holder, int position, @NonNull Transaction model) {
        holder.Reference.setText(model.getReference());

        holder.Date.setText(model.getDate());
        holder.Remark.setText(model.getRemark());
        holder.Type.setText(model.getType());
        holder.Amount.setText(model.getAmount().toString());
    }
}
