package com.keti.aiot.groupmgmt_proxy.consumer.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroupKafkaMessage {
    private Long dgpId;
    private String groupId;
    private String groupName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeviceInfoKafkaDto> members;
}
