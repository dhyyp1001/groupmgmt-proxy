package com.keti.aiot.groupmgmt_proxy.service;

import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceGroupKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupEventProcessor {

    private final PlatformSyncService platformSyncService;

    public void processEvent(String eventType, DeviceGroupKafkaMessage message) {
        switch (eventType.toUpperCase()) {
            case "CREATE" -> {
                log.info("[Processor] 그룹 생성 처리 시작 - groupId: {}", message.getGroupId());
                platformSyncService.createGroup(message);
            }
            case "UPDATE" -> {
                log.info("[Processor] 그룹 수정 처리 시작 - groupId: {}", message.getGroupId());
                platformSyncService.updateGroup(message);
            }
            case "DELETE" -> {
                log.info("[Processor] 그룹 삭제 처리 시작 - groupId: {}", message.getGroupId());
                platformSyncService.deleteGroup(message);
            }
            default -> log.warn("[Processor] 알 수 없는 이벤트 타입: {}", eventType);
        }
    }
}
