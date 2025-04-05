package com.keti.aiot.groupmgmt_proxy.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformRestClient {

    private final WebClient webClient;

    private static final String ORIGIN = "SgroupProxy";
    private static final String BASE_PATH = "/Mobius";

    public void deleteResource(String resourcePath) {
        try {
            log.info("[MobiusRestClient] 그룹 리소스 삭제 요청: {}", resourcePath);

            webClient
                    .delete()
                    .uri(BASE_PATH + "/" + resourcePath) // 예: /Mobius/sensorAE01/group001
                    .header("X-M2M-Origin", ORIGIN)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("[MobiusRestClient] 삭제 완료 - {}", resourcePath);
        } catch (Exception e) {
            log.error("[MobiusRestClient] 리소스 삭제 실패: {}", resourcePath, e);
        }
    }

    public void createGroupResource(String aeId, Map<String, Object> groupBody) {
        try {
            String targetPath = "/Mobius/" + aeId;

            log.info("[MobiusRestClient] 그룹 리소스 생성 요청 - path: {}", targetPath);
            log.debug("[MobiusRestClient] 전송 바디: {}", groupBody);

            webClient
                    .post()
                    .uri(targetPath)
                    .header("X-M2M-Origin", ORIGIN)
                    .header("Content-Type", "application/json;ty=9") // ty=9 → <grp> 타입
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[MobiusRestClient] 그룹 리소스 생성 완료 - {}", targetPath);
        } catch (Exception e) {
            log.error("[MobiusRestClient] 그룹 생성 실패 - aeId: {}", aeId, e);
        }
    }

}
