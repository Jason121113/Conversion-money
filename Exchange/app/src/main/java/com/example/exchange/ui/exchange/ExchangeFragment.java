package com.example.exchange.ui.exchange;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.exchange.SignUp;
import com.example.exchange.currencyspinner;
import com.example.exchange.databinding.FragmentExchangeBinding;
import com.example.exchange.fundspinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExchangeFragment extends Fragment {

    private FragmentExchangeBinding binding;
    private ImageView profileImage;
    private Button exchange;
    private EditText amount;
    private TextView nameLabel, emailLabel, accountLabel;
    private Spinner currencySpinner, fundSpinner;
    private String curValPeso, curValDollar, curValEuro, curValYuan;
    private String pesoFund, dollarFund, yuanFund, euroFund;
    private DocumentReference customerDatabase;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExchangeViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ExchangeViewModel.class);

        binding = FragmentExchangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        exchange = binding.confirm;

        profileImage = binding.profileImage;

        nameLabel = binding.fullNameLabelProfile;
        emailLabel = binding.emailLabel;
        accountLabel = binding.accountnumber;

        currencySpinner = binding.currency;
        fundSpinner = binding.accountFund;
        amount = binding.amount;

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

                        pesoFund = document.get("fundsPeso").toString();
                        dollarFund = document.get("fundsDollar").toString();
                        yuanFund = document.get("fundsYuan").toString();
                        euroFund = document.get("fundsEuro").toString();

                        if(!document.get("profile").toString().equals("default")) {
                            Glide.with(getActivity()).load(document.get("profile").toString()).apply(RequestOptions.circleCropTransform()).into(profileImage);
                        }

                        loadSpinner();
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currencyspinner selectedUser =(currencyspinner) currencySpinner.getSelectedItem();
                String currencyName = selectedUser.getName();
                String currencyValue = selectedUser.getValueAmount();

                fundspinner selectedFund =(fundspinner) fundSpinner.getSelectedItem();
                String fundName = selectedFund.getFundName();
                String fundValue = selectedFund.getValueAmount();

                Integer amountTrasfer = Integer.parseInt(amount.getText().toString().trim());

                Log.d("myTag", "Currency Value"+currencyValue);
                Log.d("myTag", "Amount Value"+amountTrasfer);


                if (fundName.equals(currencyName)){
                    Toast.makeText(getContext(), "You cannot convert same currency", Toast.LENGTH_LONG).show();
                    fundSpinner.requestFocus();
                    return;
                }

                if (amount.getText().toString().trim().isEmpty()){
                    amount.setError("Amount is required!");
                    amount.requestFocus();
                    return;
                }

                Map newUserFund = new HashMap();
                Integer convertedAmount = 0;

//                Peso

                if (currencyName.equals("Peso") && fundName.equals("Dollar")){
                    convertedAmount = amountTrasfer * Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(dollarFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(pesoFund) + convertedAmount;
                    newUserFund.put("fundsDollar", UserNewFund.toString());
                    newUserFund.put("fundsPeso", UserNewFundCon.toString());
                }else if (currencyName.equals("Dollar") && fundName.equals("Peso")){
                    convertedAmount = amountTrasfer / Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(pesoFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(dollarFund) + convertedAmount;
                    newUserFund.put("fundsDollar", UserNewFundCon.toString());
                    newUserFund.put("fundsPeso", UserNewFund.toString());

                }

                if (currencyName.equals("Peso") && fundName.equals("Yuan")){
                    convertedAmount = amountTrasfer * Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(yuanFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(pesoFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFund.toString());
                    newUserFund.put("fundsPeso", UserNewFundCon.toString());
                }else if (currencyName.equals("Yuan") && fundName.equals("Peso")){
                    convertedAmount = amountTrasfer / Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(pesoFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(yuanFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFundCon.toString());
                    newUserFund.put("fundsPeso", UserNewFund.toString());
                }

                if (currencyName.equals("Peso") && fundName.equals("Euro")){
                    convertedAmount = amountTrasfer * Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(euroFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(pesoFund) + convertedAmount;
                    newUserFund.put("fundsEuro", UserNewFund.toString());
                    newUserFund.put("fundsPeso", UserNewFundCon.toString());
                }else if (currencyName.equals("Euro") && fundName.equals("Peso")){
                    convertedAmount = amountTrasfer / Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(pesoFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(euroFund) + convertedAmount;
                    newUserFund.put("fundsEuro", UserNewFundCon.toString());
                    newUserFund.put("fundsPeso", UserNewFund.toString());
                }

//                Dollar
                if (currencyName.equals("Dollar") && fundName.equals("Yuan")){
                    convertedAmount = amountTrasfer * Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(yuanFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(dollarFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFund.toString());
                    newUserFund.put("fundsDollar", UserNewFundCon.toString());
                }else if (currencyName.equals("Yuan") && fundName.equals("Dollar")){
                    convertedAmount = amountTrasfer / Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(dollarFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(yuanFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFundCon.toString());
                    newUserFund.put("fundsDollar", UserNewFund.toString());
                }

                if (currencyName.equals("Dollar") && fundName.equals("Euro")){
                    convertedAmount = amountTrasfer / Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(euroFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(dollarFund) + convertedAmount;
                    newUserFund.put("fundsEuro", UserNewFund.toString());
                    newUserFund.put("fundsDollar", UserNewFundCon.toString());
                }else if (currencyName.equals("Euro") && fundName.equals("Dollar")){
                    convertedAmount = amountTrasfer * Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(dollarFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(euroFund) + convertedAmount;
                    newUserFund.put("fundsEuro", UserNewFundCon.toString());
                    newUserFund.put("fundsDollar", UserNewFund.toString());
                }

//                EURO

                if (currencyName.equals("Euro") && fundName.equals("Yuan")){
                    convertedAmount = amountTrasfer * Integer.parseInt(selectedFund.getCurrencyValue());
                    Integer UserNewFund = Integer.parseInt(yuanFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(euroFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFund.toString());
                    newUserFund.put("fundsEuro", UserNewFundCon.toString());
                }else if (currencyName.equals("Yuan") && fundName.equals("Euro")){
                    convertedAmount = amountTrasfer / Integer.parseInt(currencyValue);
                    Integer UserNewFund = Integer.parseInt(euroFund) - amountTrasfer;
                    Integer UserNewFundCon = Integer.parseInt(yuanFund) + convertedAmount;
                    newUserFund.put("fundsYuan", UserNewFundCon.toString());
                    newUserFund.put("fundsEuro", UserNewFund.toString());
                }


                Log.d("myTag", "Converted Value"+convertedAmount);

                customerDatabase.update(newUserFund);

                String transactionID = UUID.randomUUID().toString();
                transactionID = transactionID.replaceAll("[^\\d.]", "");
                transactionID = transactionID.replaceAll("0", "");
                transactionID = transactionID.substring(0,8);

                String DateCreated = new java.sql.Date(System.currentTimeMillis()).toString();

                Long timestamp = System.currentTimeMillis()/1000;

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("reference", transactionID);
                transaction.put("type", "Convert Fund");
                transaction.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                transaction.put("remark", "Convert fund from "+fundName+" to "+currencyName);
                transaction.put("amount", amount.getText().toString().trim());
                transaction.put("date", DateCreated);
                transaction.put("timestamp", timestamp.toString());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("transaction")
                        .add(transaction)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Conversion Success", Toast.LENGTH_LONG).show();
                                loadFunds();
                                Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("test", "Error adding document", e);
                            }
                        });
            }
        });

        return root;
    }

    private void loadFunds(){
        amount.setText("");

        customerDatabase = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        pesoFund = document.get("fundsPeso").toString();
                        dollarFund = document.get("fundsDollar").toString();
                        yuanFund = document.get("fundsYuan").toString();
                        euroFund = document.get("fundsEuro").toString();
                        loadSpinner();
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    private void loadSpinner() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference subjectsRef = rootRef.collection("currency");
        currencySpinner = binding.currency;
        List<currencyspinner> currencyArr = new ArrayList<>();
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        currencyspinner currencyVal = new currencyspinner();
                        currencyVal.setName(document.getString("name"));
                        currencyVal.setValueAmount(document.getString("value"));

                        if (document.getString("name").equals("Yuan")){
                            curValYuan = document.getString("value");
                        }else if(document.getString("name").equals("Dollar")){
                            curValDollar = document.getString("value");
                        }else if(document.getString("name").equals("Peso")){
                            curValPeso = document.getString("value");
                        }else if(document.getString("name").equals("Euro")){
                            curValEuro = document.getString("value");
                        }
                        currencyArr.add(currencyVal);
                    }

                    ArrayAdapter<currencyspinner> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, currencyArr);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    currencySpinner.setAdapter(adapter);

                    loadFundSpinner();
                }
            }
        });
    }

    private void loadFundSpinner() {
        FirebaseFirestore fundRef = FirebaseFirestore.getInstance();
        CollectionReference fundsRef = fundRef.collection("fund");
        fundSpinner = binding.accountFund;
        List<fundspinner> fundArr = new ArrayList<>();
        fundsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        fundspinner fundVal = new fundspinner();
                        fundVal.setFundName(document.getString("name"));
                        if (document.getString("name").equals("Yuan")){
                            fundVal.setValueAmount(yuanFund);
                            fundVal.setCurrencyValue(curValYuan);
                            fundVal.setName(document.getString("name")+" - "+ yuanFund);
                        }else if(document.getString("name").equals("Dollar")){
                            fundVal.setValueAmount(dollarFund);
                            fundVal.setCurrencyValue(curValDollar);
                            fundVal.setName(document.getString("name")+" - "+ dollarFund);
                        }else if(document.getString("name").equals("Peso")){
                            fundVal.setValueAmount(pesoFund);
                            fundVal.setCurrencyValue(curValPeso);
                            fundVal.setName(document.getString("name")+" - "+ pesoFund);
                        }else if(document.getString("name").equals("Euro")){
                            fundVal.setValueAmount(euroFund);
                            fundVal.setCurrencyValue(curValEuro);
                            fundVal.setName(document.getString("name")+" - "+ euroFund);
                        }

                        fundArr.add(fundVal);
                    }

                    ArrayAdapter<fundspinner> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fundArr);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fundSpinner.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}