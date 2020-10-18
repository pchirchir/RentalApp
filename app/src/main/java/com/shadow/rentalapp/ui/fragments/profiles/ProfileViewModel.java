package com.shadow.rentalapp.ui.fragments.profiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shadow.rentalapp.data.models.Profile;
import com.shadow.rentalapp.data.repositories.ProfilesRepository;
import com.shadow.rentalapp.internal.ProfileTaskListener;

public class ProfileViewModel extends ViewModel implements ProfileTaskListener {

    private MutableLiveData<Profile> _profile = new MutableLiveData<>();
    private MutableLiveData<Exception> _exception = new MutableLiveData<>();

    private ProfilesRepository profilesRepository = new ProfilesRepository(this);

    public void initiateProfileRetrieval(String uid) {
        profilesRepository.getProfile(uid);
    }

    @Override
    public void onProfileRetrieved(Profile profile) {
        _profile.setValue(profile);
    }

    @Override
    public void onException(Exception exception) {
        _exception.setValue(exception);
    }

    public LiveData<Profile> getProfile() {
        return _profile;
    }

    public LiveData<Exception> getException() {
        return _exception;
    }
}
