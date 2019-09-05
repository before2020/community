package com.wyli.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    // kafka里的topic,如点赞评论
    private String topic;
    // 哪个用户触发的操作
    private int userId;
    // 作用于哪个实体
    private int entityType;
    private int entityId;
    // 实体作者
    private int entityUserId;
    // 其他
    private Map<String, Object> others = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        // 这样可以连续调用 event.setTopic(topic).setUserId(id)...
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getOthers() {
        return others;
    }

    public Event setOthers(String key, Object value) {
        this.others.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", others=" + others +
                '}';
    }
}
