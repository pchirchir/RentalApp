package com.shadow.rentalapp.internal;

import com.shadow.rentalapp.data.models.Profile;

public interface ProfileTaskListener {

    void onProfileRetrieved(Profile profile);

    void onException(Exception exception);
}
