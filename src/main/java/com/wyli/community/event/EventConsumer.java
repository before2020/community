package com.wyli.community.event;

import com.alibaba.fastjson.JSONObject;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.Event;
import com.wyli.community.entity.Message;
import com.wyli.community.service.DiscussPostService;
import com.wyli.community.service.ElasticsearchService;
import com.wyli.community.service.MessageService;
import com.wyli.community.util.CommunityConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstants {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private ElasticsearchService elasticService;
    @Autowired
    private DiscussPostService discussPostService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息不能为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 三个信息被存入message.content
        Map<String, Object> map = new HashMap<>();
        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());

        if (!event.getOthers().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getOthers().entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(map));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息不能为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticService.saveDiscussPost(post);
    }
}
