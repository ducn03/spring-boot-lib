package com.springboot.lib.service.redis;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class RedisAnnotationConfig {
    @Autowired
    private RedisMessageListenerContainer listenerContainer;

    @Autowired
    private Map<String, Object> beans;

    // Cache để lưu thông tin listener
    private final Map<String, MessageListenerAdapter> listenerCache = new HashMap<>();

    @PostConstruct
    public void registerListeners() {
        // Quét một lần và lưu vào cache
        beans.forEach((beanName, bean) -> {
            ReflectionUtils.doWithMethods(bean.getClass(), method -> {
                RedisSubscriber annotation = method.getAnnotation(RedisSubscriber.class);
                if (annotation != null) {
                    String topic = annotation.value();
                    MessageListenerAdapter listener = new MessageListenerAdapter(bean, method.getName());
                    listenerCache.put(topic, listener);
                }
            }, method -> method.getAnnotation(RedisSubscriber.class) != null);
        });

        // Đăng ký tất cả listener từ cache
        listenerCache.forEach((topic, listener) -> {
            listenerContainer.addMessageListener(listener, new org.springframework.data.redis.listener.ChannelTopic(topic));
        });
    }
}
