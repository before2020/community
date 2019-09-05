package com.wyli.community.util;

public interface CommunityConstants {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    long DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    long REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // 评论类型
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;

    // Topic类型
    String TOPIC_FOLLOW = "follow";
    String TOPIC_LIKE = "like";
    String TOPIC_COMMENT = "comment";
    String TOPIC_PUBLISH = "publish";

    int SYSTEM_USER_ID = 1;
}
