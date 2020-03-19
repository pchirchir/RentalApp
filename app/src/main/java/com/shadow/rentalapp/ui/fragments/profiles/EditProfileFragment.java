package com.shadow.rentalapp.ui.fragments.profiles;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shadow.rentalapp.R;
import com.shadow.rentalapp.data.models.Profile;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    private static final int SELECT_IMAGE_RC = 22;
    private CircleImageView avatarCiv;
    private Uri mAvatarImageUri = null;
    private TextInputEditText fullNameTxt;
    private TextInputEditText phoneTxt;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mDatabase;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Initialize firebase variables
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        //Register the views
        avatarCiv = view.findViewById(R.id.avatar_civ);
        avatarCiv.setOnClickListener(v -> {
            mAvatarImageUri = null;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            assert getActivity() != null;
            ComponentName componentName = intent.resolveActivity(getActivity().getPackageManager());

            if (componentName != null) {
                startActivityForResult(intent, SELECT_IMAGE_RC);
            }
        });


        fullNameTxt = view.findViewById(R.id.full_name_txt);
        phoneTxt = view.findViewById(R.id.phone_txt);
        //RadioGroup genderRg = view.findViewById(R.id.gender_rg);
        RadioButton maleRb = view.findViewById(R.id.male_rb);
        RadioButton femaleRb = view.findViewById(R.id.female_rb);

        Button updateProfileBtn = view.findViewById(R.id.update_profile_btn);

        updateProfileBtn.setOnClickListener(v -> {
            String fullName = String.valueOf(fullNameTxt.getText());
            String phone = String.valueOf(phoneTxt.getText());
            String gender = "";

            if (maleRb.isChecked()) {
                gender = "male";
            } else if (femaleRb.isChecked()) {
                gender = "female";
            }

            if (!isValidName(fullName)) return;
            if (!isValidPhone(phone)) return;

            Profile profile = new Profile(fullName, phone, gender, null);

            if (mAvatarImageUri == null) {
                updateProfile(profile);
            } else {
                uploadImageFirst(profile);
            }
        });
    }

    private void uploadImageFirst(Profile profile) {
        //Get the authenticated user uid
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String uid = currentUser.getUid();

        StorageReference userAvatarStorageReference = mStorage.getReference(uid);

        UploadTask uploadTask = userAvatarStorageReference.putFile(mAvatarImageUri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                userAvatarStorageReference.getDownloadUrl().addOnCompleteListener(downLoadUrlTask -> {
                    if (downLoadUrlTask.isSuccessful()) {

                        assert downLoadUrlTask.getResult() != null;
                        String imageUrl = downLoadUrlTask.getResult().toString();
                        profile.setAvatarUrl(imageUrl);

                        updateProfile(profile);

                    }
                });
            } else {
                Log.e(TAG, "uploadImageFirst: failed: ", task.getException());
                Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateProfile(Profile profile) {
        Map<String, Object> profileUpdatesMap = new HashMap<>();

        profileUpdatesMap.put("fullName", profile.getFullName());
        profileUpdatesMap.put("phone", profile.getPhone());
        profileUpdatesMap.put("gender", profile.getGender());
        profileUpdatesMap.put("avatarUrl", profile.getAvatarUrl());

        assert mAuth.getCurrentUser() != null;
        mDatabase.collection("Profiles").document(mAuth.getCurrentUser().getUid())
                .update(profileUpdatesMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Woo hoo!! profile updated", Toast.LENGTH_SHORT).show();
                        assert getActivity() != null;
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(), "Update operation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidPhone(String phone) {

        if (TextUtils.isEmpty(phone)) {
            phoneTxt.setError("Phone is required");
            phoneTxt.requestFocus();
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneTxt.setError("Invalid Phone");
            phoneTxt.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidName(String fullName) {

        if (TextUtils.isEmpty(fullName)) {
            fullNameTxt.setError("Full name is required");
            fullNameTxt.requestFocus();
            return false;
        }

        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE_RC) {
                assert data != null;
                mAvatarImageUri = data.getData();
                avatarCiv.setImageURI(mAvatarImageUri);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            Log.d(TAG, "onActivityResult: result not OK");
        }
    }
}
