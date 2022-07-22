package com.example.exchange.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchange.Transaction;
import com.example.exchange.databinding.FragmentTransactionBinding;
import com.example.exchange.transactionAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TransactionFragment extends Fragment {

    private FragmentTransactionBinding binding;
    private ImageView profileView;
    private TextView name,id,funds;
    private DocumentReference customerDatabase;
    transactionAdapter adapter;
    SearchView search;
    private RecyclerView recyclerView;
    Query query;
    Query searchQuery;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TransactionViewModel homeViewModel =
                new ViewModelProvider(this).get(TransactionViewModel.class);

        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.transactionRecylcler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search = binding.searchBar;

        query = FirebaseFirestore.getInstance()
                .collection("transaction")
                .whereEqualTo("user", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Transaction> options = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query, Transaction.class)
                .build();

        adapter = new transactionAdapter(options);
        recyclerView.setAdapter(adapter);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 1){
                    startSearch(newText);
                }else{
                    loadData();
                }
                return true;
            }
        });

        return root;
    }

    public void startSearch(String SearchText){
        searchQuery = FirebaseFirestore.getInstance()
                .collection("transaction")
                .whereEqualTo("user", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("reference")
                .startAt(SearchText)
                .endAt(SearchText + '~')
                .limit(50);

        FirestoreRecyclerOptions<Transaction> options
                = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(searchQuery, Transaction.class)
                .build();

        adapter = new transactionAdapter(options);
        recyclerView.setAdapter(adapter);
        onStart();
    }

    public void loadData(){
        FirestoreRecyclerOptions<Transaction> options
                = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query, Transaction.class)
                .build();

        adapter = new transactionAdapter(options);
        recyclerView.setAdapter(adapter);
        onStart();
    }

    @Override public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}