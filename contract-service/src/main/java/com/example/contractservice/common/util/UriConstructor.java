package com.example.contractservice.common.util;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriConstructor {

    @Value("${module.member.application.name}")
    private String memberServiceName;

    @Value("${module.member.application.path.member-info}")
    private String memberInfoUrl;

    private UriConstructor() {}

    public URI createMemberInfoUrl(List<String> memberCodes) {
        return UriComponentsBuilder.newInstance()
                .scheme("lb")
                .host(memberServiceName)
                .path(memberInfoUrl)
                .queryParam("member-code", memberCodes)
                .build()
                .toUri();
    }
}
