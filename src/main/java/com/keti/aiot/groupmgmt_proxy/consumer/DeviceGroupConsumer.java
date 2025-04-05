package com.keti.aiot.groupmgmt_proxy.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceGroupKafkaMessage;
import com.keti.aiot.groupmgmt_proxy.service.GroupEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceGroupConsumer {

    private final GroupEventProcessor groupEventProcessor;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(
            topics = "${spring.kafka.topic.device-group}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleDeviceGroupEvent(ConsumerRecord<String, Object> record) {
        try {
            // Kafka 헤더에서 eventType 추출
            String eventType = record.headers().lastHeader("eventType") != null
                    ? new String(record.headers().lastHeader("eventType").value())
                    : "UNKNOWN";

            // 메시지 본문 역직렬화
            String json = record.value().toString();
            DeviceGroupKafkaMessage message = objectMapper.readValue(json, DeviceGroupKafkaMessage.class);

            log.info("[Consumer] Kafka 메시지 수신 - eventType={}, groupId={}", eventType, message.getGroupId());

            // 이벤트 위임 처리
            groupEventProcessor.processEvent(eventType, message);

        } catch (Exception e) {
            log.error("[Consumer] Kafka 메시지 처리 실패", e);
        }
    }
}
