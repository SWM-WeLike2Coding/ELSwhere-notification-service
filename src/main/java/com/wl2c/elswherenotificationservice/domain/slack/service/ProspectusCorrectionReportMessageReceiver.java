package com.wl2c.elswherenotificationservice.domain.slack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wl2c.elswherenotificationservice.client.slack.api.SlackClient;
import com.wl2c.elswherenotificationservice.client.slack.dto.RequestMessageDto;
import com.wl2c.elswherenotificationservice.domain.slack.model.dto.NewIssuerMessage;
import com.wl2c.elswherenotificationservice.domain.slack.model.dto.ProspectusCorrectionReportMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProspectusCorrectionReportMessageReceiver {

    private final SlackClient slackClient;

    @KafkaListener(topics = "prospectus-correction-report-alert", groupId = "prospectus-correction-report-alert-consumer", containerFactory = "kafkaConsumerContainerFactory")
    public void receive(String stringMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
       ProspectusCorrectionReportMessage prospectusCorrectionReportMessage = objectMapper.readValue(stringMessage, ProspectusCorrectionReportMessage.class);
        log.info("prospectus-correction-report-alert Message Consumed : " + stringMessage);

        ResponseEntity<String> response = slackClient.sendAlert(createMessage(prospectusCorrectionReportMessage));
        log.info("prospectus-correction-report-alert Response : " + response.getStatusCode());
    }

    private RequestMessageDto createMessage(ProspectusCorrectionReportMessage prospectusCorrectionReportMessage) {
        String stringBuilder = "투자설명서 정정신고한 상품\n" +
                "상품명 : " +
                prospectusCorrectionReportMessage.getProductName() +
                "\n\n" +
                "투자설명서 링크 : " +
                prospectusCorrectionReportMessage.getProspectusLink();

        return new RequestMessageDto(stringBuilder);
    }
}
