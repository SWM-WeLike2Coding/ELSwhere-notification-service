package com.wl2c.elswherenotificationservice.domain.slack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wl2c.elswherenotificationservice.client.slack.api.SlackClient;
import com.wl2c.elswherenotificationservice.client.slack.dto.RequestMessageDto;
import com.wl2c.elswherenotificationservice.domain.slack.model.dto.NewIssuerMessage;
import com.wl2c.elswherenotificationservice.domain.slack.model.dto.NewTickerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewIssuerMessageReceiver {

    private final SlackClient slackClient;

    @KafkaListener(topics = "new-issuer-alert", groupId = "new-issuer-alert-consumer", containerFactory = "kafkaConsumerContainerFactory")
    public void receive(String stringMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        NewIssuerMessage newIssuerMessage = objectMapper.readValue(stringMessage, NewIssuerMessage.class);
        log.info("new-issuer-alert Message Consumed : " + stringMessage);

        ResponseEntity<String> response = slackClient.sendAlert(createMessage(newIssuerMessage));
        log.info("new-issuer-alert Response : " + response.getStatusCode());
    }

    private RequestMessageDto createMessage(NewIssuerMessage newIssuerMessage) {
        String stringBuilder = "새로운 발행회사 정보 반영 필요\n" +
                "상품명 : " +
                newIssuerMessage.getProductName() +
                "\n\n" +
                "발행회사 : " +
                newIssuerMessage.getIssuer();

        return new RequestMessageDto(stringBuilder);
    }
}
