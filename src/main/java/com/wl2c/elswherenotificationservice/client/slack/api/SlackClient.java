package com.wl2c.elswherenotificationservice.client.slack.api;

import com.wl2c.elswherenotificationservice.client.slack.dto.RequestMessageDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-client", url = "${slack.api.url}")
public interface SlackClient {

    @PostMapping
    ResponseEntity<String> sendAlert(@Valid @RequestBody RequestMessageDto requestMessageDto);

}
