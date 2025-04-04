package com.keti.aiot.groupmgmt_proxy.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceGroupKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceGroupConsumer {

    @KafkaListener(topics = "aiot.network.management.device-group", groupId = "group-proxy")
    public void handleDeviceGroupEvent(ConsumerRecord<String, Object> record) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        try {
            // 헤더 추출
            String eventType = record.headers().lastHeader("eventType") != null
                    ? new String(record.headers().lastHeader("eventType").value())
                    : "UNKNOWN";

            // JSON 역직렬화
            String json = record.value().toString(); // value는 LinkedHashMap or String
            DeviceGroupKafkaMessage message = objectMapper.readValue(json, DeviceGroupKafkaMessage.class);


            log.info("[Consumer] Received Kafka Message eventType={}, groupId={}", eventType, message.getGroupId());

            // 이벤트 처리
           /* switch (eventType) {
                case "CREATED" -> processCreate(message);
                case "UPDATED" -> processUpdate(message);
                case "DELETED" -> processDelete(message.getGroupId());
                default -> log.warn("알 수 없는 이벤트 타입: {}", eventType);
            }*/

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}
