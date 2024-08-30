package com.wl2c.elswherenotificationservice.client.slack.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RequestMessageDto {

    @Schema(description = "전달할 메시지 내용")
    private final String text;

    @JsonCreator
    public RequestMessageDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}
