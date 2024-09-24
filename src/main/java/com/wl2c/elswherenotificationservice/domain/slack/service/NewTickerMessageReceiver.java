package com.wl2c.elswherenotificationservice.domain.slack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wl2c.elswherenotificationservice.client.slack.api.SlackClient;
import com.wl2c.elswherenotificationservice.client.slack.dto.RequestMessageDto;
import com.wl2c.elswherenotificationservice.domain.slack.model.dto.NewTickerMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewTickerMessageReceiver {

    private final SlackClient slackClient;

    @KafkaListener(topics = "new-ticker-alert", groupId = "new-ticker-alert-consumer", containerFactory = "kafkaConsumerContainerFactory")
    public void receive(String stringMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        NewTickerMessage newTickerMessage = objectMapper.readValue(stringMessage, NewTickerMessage.class);
        log.info("new-ticker-alert Message Consumed : " + stringMessage);

        ResponseEntity<String> response = slackClient.sendAlert(createMessage(newTickerMessage));
        log.info("new-ticker-alert Response : " + response.getStatusCode());
    }

    private RequestMessageDto createMessage(NewTickerMessage newTickerMessage) {
        String stringBuilder = "새로운 티커 정보 반영 필요\n" +
                "상품 ID : " +
                newTickerMessage.getProductId() +
                "\n\n" +
                "상품명 : " +
                newTickerMessage.getProductName() +
                "\n\n" +
                "기초자산 : " +
                newTickerMessage.getEquity();

        return new RequestMessageDto(stringBuilder);
    }
}
