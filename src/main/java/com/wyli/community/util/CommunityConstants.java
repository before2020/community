package com.wyli.community.util;

public interface CommunityConstants {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    long DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    long REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
