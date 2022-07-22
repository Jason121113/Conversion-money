package com.example.exchange.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.exchange.R;
import com.example.exchange.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private ImageView profileImage;
    private TextInputLayout name,email,cellphone,password;
    private TextView nameLabel, emailLabel;
    private DocumentReference customerDatabase;
    private Uri resultUri;
    private RelativeLayout exchange;
    Button confirm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel galleryViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.fullNameProfile;
        email = binding.emailProfile;
        cellphone = binding.phoneNoProfile;
        password = binding.passwordProfile;
        profileImage = binding.profileImage;

        nameLabel = binding.fullNameLabelProfile;
        emailLabel = binding.emailLabel;

        confirm = binding.updateProfileBtn;

        customerDatabase = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        name.getEditText().setText(document.get("name").toString());
                        cellphone.getEditText().setText(document.get("cellphone").toString());
                        email.getEditText().setText(document.get("email").toString());
                        emailLabel.setText(document.get("email").toString());
                        nameLabel.setText(document.get("name").toString());
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

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        confirm.setOnClickListener(v -> saveUserInformation());

        return root;
    }

    private void saveUserInformation() {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("name", name.getEditText().getText().toString());
            userInfo.put("email", email.getEditText().getText().toString());
            userInfo.put("cellphone", cellphone.getEditText().getText().toString());

            customerDatabase.update(userInfo);

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            UploadTask uploadTask = filePath.putFile(resultUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profile", uri.toString());
                customerDatabase.update(newImage);

            }).addOnFailureListener(exception -> {

            }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                Glide.with(getActivity())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}