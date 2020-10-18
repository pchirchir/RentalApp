package com.shadow.rentalapp.data.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.rentalapp.data.models.Profile;
import com.shadow.rentalapp.internal.ProfileTaskListener;

public class ProfilesRepository {

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private ProfileTaskListener listener;

    public ProfilesRepository(ProfileTaskListener listener) {
        this.listener = listener;
    }

    public void getProfile(String uid) {

        mDb.collection("Profiles")
                .document(uid)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        if (listener != null) listener.onException(e);
                    }

                    if (documentSnapshot.exists()) {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        assert profile != null;
                        if (listener != null) listener.onProfileRetrieved(profile);
                    }
                });
    }
}
