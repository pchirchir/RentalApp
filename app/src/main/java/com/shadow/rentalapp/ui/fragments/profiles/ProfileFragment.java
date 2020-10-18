package com.shadow.rentalapp.ui.fragments.profiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shadow.rentalapp.R;
import com.shadow.rentalapp.data.models.Profile;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, phoneTextView, emailTextView;
    private CircleImageView profileImageCiv;

    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameTextView = view.findViewById(R.id.name_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        phoneTextView = view.findViewById(R.id.phone_text_view);

        profileImageCiv = view.findViewById(R.id.profile_image_civ);

        //Register the necessary views
        Button editProfileBtn = view.findViewById(R.id.edit_profile_button);
        editProfileBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.editProfileFragment));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        assert currentUser != null;

        viewModel.initiateProfileRetrieval(FirebaseAuth.getInstance().getCurrentUser().getUid());

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> updateUI(profile));
    }

    private void updateUI(Profile profile) {
        emailTextView.setText(currentUser.getEmail());

        nameTextView.setText(profile.getFullName());

        if (profile.getPhone() == null) phoneTextView.setText("Not Set");
        else phoneTextView.setText(profile.getPhone());
    }


}
