package com.keti.aiot.groupmgmt_proxy.service;

import com.keti.aiot.groupmgmt_proxy.client.PlatformRestClient;
import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceGroupKafkaMessage;
import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceInfoKafkaDto;
import com.keti.aiot.groupmgmt_proxy.mapper.DeviceGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformSyncService {

    private final PlatformRestClient platformRestClient;
    private final DeviceGroupMapper deviceGroupMapper;

    public void createGroup(DeviceGroupKafkaMessage message) {
        log.info("[PlatformSync] 그룹 생성 시작 - groupId: {}", message.getGroupId());

        List<DeviceInfoKafkaDto> members = message.getMembers();
        if (members == null || members.isEmpty()) {
            log.warn("[PlatformSync] 멤버가 없습니다. groupId={}", message.getGroupId());
            return;
        }

        String aeId = extractAeId(members);

        // [1] groupBody 생성 책임을 mapper로 위임
        Map<String, Object> groupBody = deviceGroupMapper.toPlatformGroupBody(message);

        // Mobius 리소스 생성 요청
        platformRestClient.createGroupResource(aeId, groupBody);

        log.info("[PlatformSync] 그룹 생성 완료 - AE: {}, groupId: {}", aeId, message.getGroupId());
    }

    public void updateGroup(DeviceGroupKafkaMessage message) {
        log.info("[PlatformSync] 그룹 수정 시작 - groupId: {}", message.getGroupId());

        List<DeviceInfoKafkaDto> members = message.getMembers();
        if (members == null || members.isEmpty()) {
            log.warn("[PlatformSync] 멤버가 없습니다. groupId={}", message.getGroupId());
            return;
        }

        // appId 또는 appEui 기준으로 aeId 판단
        String aeId = extractAeId(members);

        // 기존 그룹 삭제
        deleteGroup(message);

        // 새로운 정보로 재생성
        createGroup(message);

        log.info("[PlatformSync] 그룹 수정 완료 - groupId: {}", message.getGroupId());
    }

    public void deleteGroup(DeviceGroupKafkaMessage message) {
        log.info("[PlatformSync] 그룹 삭제 시작 - groupId: {}", message.getGroupId());

        List<DeviceInfoKafkaDto> members = message.getMembers();
        if (members == null || members.isEmpty()) {
            log.warn("[PlatformSync] 그룹 삭제를 위해 단말 정보가 필요합니다. groupId={}", message.getGroupId());
            return;
        }

        String aeId = extractAeId(members);
        String resourcePath = aeId + "/" + message.getGroupId();

        platformRestClient.deleteResource(resourcePath);

        log.info("[PlatformSync] 그룹 삭제 완료 - groupId: {}, aeId: {}", message.getGroupId(), aeId);
    }

    private String extractAeId(List<DeviceInfoKafkaDto> members) {
        // 우선 appId 기준으로 묶기
        boolean sameAppId = members.stream()
                .map(DeviceInfoKafkaDto::getAppId)
                .distinct()
                .count() == 1;

        if (sameAppId) {
            return members.get(0).getAppId();
        }

        // appId가 다르다면 appEui 기준으로 판단
        boolean sameAppEui = members.stream()
                .map(DeviceInfoKafkaDto::getAapEui)
                .distinct()
                .count() == 1;

        if (sameAppEui) {
            return members.get(0).getAapEui();
        }

        throw new IllegalArgumentException("[PlatformSync] 단말 그룹의 AE 기준(appId/appEui)이 일치하지 않습니다.");
    }
}