package com.example.exchange.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.exchange.Login;
import com.example.exchange.R;
import com.example.exchange.SignUp;
import com.example.exchange.User;
import com.example.exchange.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ImageView profileImage;
    private TextView nameLabel, emailLabel, accountLabel, pesoFund, dollarFund, yuanFund, euroFund;
    private DocumentReference customerDatabase;
    private Uri resultUri;
    private RelativeLayout exchange, dollar, peso, yuan, euro;
    private Integer dollarBalance, pesoBalance,yuanBalance, euroBalance;
    Button confirm;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameLabel = binding.fullNameLabelProfile;
        emailLabel = binding.emailLabel;
        accountLabel = binding.accountnumber;

        profileImage = binding.profileImage;

        pesoFund = binding.fundpesoLabel;
        dollarFund = binding.funddollarLabel;
        yuanFund = binding.fundYuanLabel;
        euroFund = binding.fundEuroLabel;

        peso = binding.peso;
        dollar = binding.dollar;
        yuan = binding.yuan;
        euro = binding.euro;


        customerDatabase = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        nameLabel.setText(document.get("name").toString());
                        accountLabel.setText("Acct no: "+document.get("id").toString());
                        emailLabel.setText(document.get("email").toString());
                        nameLabel.setText(document.get("name").toString());
                        pesoFund.setText("₱"+document.get("fundsPeso").toString());
                        euroFund.setText("€"+document.get("fundsEuro").toString());
                        dollarFund.setText("$"+document.get("fundsDollar").toString());
                        yuanFund.setText("¥"+document.get("fundsYuan").toString());

                        euroBalance = Integer.parseInt(document.get("fundsEuro").toString());
                        dollarBalance = Integer.parseInt(document.get("fundsDollar").toString());
                        pesoBalance = Integer.parseInt(document.get("fundsPeso").toString());
                        yuanBalance = Integer.parseInt(document.get("fundsYuan").toString());

                        if(!document.get("profile").toString().equals("default")) {
                            Glide.with(getActivity()).load(document.get("profile").toString()).apply(RequestOptions.circleCropTransform()).into(profileImage);
                        }
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        dollar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Dollar Funds Add");
            // Set up the input
            final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    Integer newBalance = dollarBalance + Integer.parseInt(m_Text);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("fundsDollar", newBalance);

                    customerDatabase.update(userInfo);
                    dollarFund.setText("$"+newBalance.toString());

                    CreateTransaction(Integer.parseInt(m_Text), "Dollar");
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        peso.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Peso Funds Add");
            // Set up the input
            final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    Integer newBalance = pesoBalance + Integer.parseInt(m_Text);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("fundsPeso", newBalance);

                    customerDatabase.update(userInfo);
                    pesoFund.setText("₱"+newBalance.toString());

                    CreateTransaction(Integer.parseInt(m_Text), "Peso");
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        yuan.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Yuan Funds Add");
            // Set up the input
            final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    Integer newBalance = yuanBalance + Integer.parseInt(m_Text);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("fundsYuan", newBalance);

                    customerDatabase.update(userInfo);
                    yuanFund.setText("¥"+newBalance.toString());

                    CreateTransaction(Integer.parseInt(m_Text), "Yuan");
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        euro.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Euro Funds Add");
            // Set up the input
            final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    Integer newBalance = euroBalance + Integer.parseInt(m_Text);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("fundsEuro", newBalance);

                    customerDatabase.update(userInfo);
                    euroFund.setText("€"+newBalance.toString());

                    CreateTransaction(Integer.parseInt(m_Text), "Euro");
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void CreateTransaction(Integer Amount, String type){

        String transactionID = UUID.randomUUID().toString();
        transactionID = transactionID.replaceAll("[^\\d.]", "");
        transactionID = transactionID.replaceAll("0", "");
        transactionID = transactionID.substring(0,8);

        Long timestamp = System.currentTimeMillis()/1000;

        String DateCreated = new java.sql.Date(System.currentTimeMillis()).toString();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("reference", transactionID);
        transaction.put("type", "Add Fund");
        transaction.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        transaction.put("remark", "Fund "+ type);
        transaction.put("amount", Amount.toString());
        transaction.put("date", DateCreated);
        transaction.put("timestamp", timestamp.toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("transaction")
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Add funds Success", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("test", "Error adding document", e);
                    }
                });
    }
}