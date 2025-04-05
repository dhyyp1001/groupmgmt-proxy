package com.keti.aiot.groupmgmt_proxy.mapper;

import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceGroupKafkaMessage;
import com.keti.aiot.groupmgmt_proxy.consumer.dto.DeviceInfoKafkaDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeviceGroupMapper {

    public Map<String, Object> toPlatformGroupBody(DeviceGroupKafkaMessage message) {
        List<DeviceInfoKafkaDto> members = message.getMembers();

        // 모든 단말 → "/Mobius/{appId}/{devId}" 형식의 mid 생성
        List<String> memberRiList = members.stream()
                .map(member -> resolveDeviceResourceId(member.getAppId(), member.getDevId()))
                .distinct()
                .collect(Collectors.toList());

        return Map.of(
                "m2m:grp", Map.of(
                        "rn", message.getGroupId(),
                        "mid", memberRiList,
                        "mnm", memberRiList.size(),
                        "mt", 3,
                        "lbl", List.of(
                                "name:" + message.getGroupName(),
                                "description:" + message.getDescription()
                        )
                )
        );
    }

    private String resolveDeviceResourceId(String appId, String devId) {
        return "/Mobius/" + appId + "/" + devId;
    }
}
