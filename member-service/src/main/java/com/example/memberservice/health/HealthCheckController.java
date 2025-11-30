package com.example.memberservice.health;


import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Server 상태 확인 API")
public class HealthCheckController {


    private final RestTemplate restTemplate;

    public HealthCheckController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/healthCheck")
    public String healthCheck(@RequestHeader(name = "X-CODE", required = false) String memberCode) {
        if(memberCode==null){
            memberCode=" Internal API ";
        }

        return "Member"+memberCode+" OK";
    }

    @GetMapping("/connectCheck")
    public String connectCheck() {
        String cartpostUrl = "lb://CARTPOST-SERVICE/api/commissions/healthCheck";
        String communicationUrl = "lb://COMMUNICATION-SERVICE/api/chats/healthCheck";
        String contractUrl = "lb://CONTRACT-SERVICE/api/contracts/healthCheck";
        String memberUrl = "lb://MEMBER-SERVICE/api/members/healthCheck";
        String paymentUrl = "lb://PAYMENT-SERVICE/api/payments/healthCheck";
        String profileUrl = "lb://PROFILE-SERVICE/api/profiles/healthCheck";
        String searchUrl = "lb://SEARCH-SERVICE/api/search/healthCheck";
        StringBuilder sb = new StringBuilder();

        sb.append("cartpost: ").append(checkHealth(cartpostUrl)).append("\n");
        sb.append("communication: ").append(checkHealth(communicationUrl)).append("\n");
        sb.append("contract: ").append(checkHealth(contractUrl)).append("\n");
        sb.append("member: ").append(checkHealth(memberUrl)).append("\n");
        sb.append("payment: ").append(checkHealth(paymentUrl)).append("\n");
        sb.append("profile: ").append(checkHealth(profileUrl)).append("\n");
        sb.append("search: ").append(checkHealth(searchUrl)).append("\n");

        return sb.toString();

    }

    @GetMapping("/emptyTest")
    public ResponseDto<Empty> checkEmpty(){
        return ResponseDto.success();
    }

    private String checkHealth(String url) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "ERROR";
        }
    }

}
