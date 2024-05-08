package com.standader.hundredmilliondollars.Functions;

import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;

import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroups;
import org.springframework.data.redis.core.RedisTemplate;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void setKeyValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //Stream Generate and add
    public void setStreamKeyValue(String streamKey, String message) {
        ObjectRecord<String, String> record = StreamRecords.newRecord()
                    .ofObject(message)
                    .withStreamKey(streamKey);
        redisTemplate.opsForStream().add(record);
    }
    /*Map Record 구조
     * 1.StreamKey
     * 2.MessageID
     * 3.Key-Value Map
    */
    public List<MapRecord<String, Object, Object>> getAllMessages(String streamKey) {
        try {
            List<MapRecord<String, Object, Object>> messageStream = redisTemplate.opsForStream()
                .range(streamKey, Range.unbounded());
            return messageStream;
        } catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            List<MapRecord<String, Object, Object>> messageStream = new ArrayList<>();
            return messageStream;
        }
        
    }
    public void createConsumerGroup(String streamKey, String groupName) {
        try {
            XInfoGroups groups = redisTemplate.opsForStream().groups(streamKey);

            boolean groupExists = groups.stream()
                .anyMatch(group -> groupName.equals(group.groupName()));


            if (!groupExists) {
                redisTemplate.opsForStream().createGroup(streamKey, groupName);
                System.out.println("Consumer group created: " + groupName);
            } else {
                System.out.println("Consumer group already exists: " + groupName);
            }
        } catch (Exception e) {
            System.err.println("Failed to create consumer group: " + e.getMessage());
        }
    }
}
