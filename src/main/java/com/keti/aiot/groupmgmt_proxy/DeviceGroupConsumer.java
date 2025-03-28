package com.keti.aiot.groupmgmt_proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceGroupConsumer {

    @KafkaListener(topics = "device-group-events", groupId = "device-group-consumer")
    public void listen(String message) {
        log.info("✅ Kafka 메시지 수신됨: {}", message);
    }
}
