package com.keti.aiot.groupmgmt_proxy.consumer.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInfoKafkaDto {
    private String type;
    private String devId;
    private String ncId;

    //  단말 상세 정보
    private String devEui;
    private String description;
    private String channelPlan;
    private String activationType;
    private Boolean hasMobillity;
    private Integer priorityNum;
    private LocalDateTime lastSeen;
    private String appId;
    private String aapEui;
}
