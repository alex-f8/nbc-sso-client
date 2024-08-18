package com.example.nbc_sso_client;

import com.example.nbc_sso_client.dto.TokenDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Profile("loc2")
@EnableScheduling
@RestController
@RequestMapping
@SpringBootApplication
public class NbcSsoClientApplication {
    @Value("${keycloak-client.issue-uri}")
    private String issueUri;

    @Value("${keycloak-client.realm}")
    private String realm;

    @Value("${keycloak-client.client-id}")
    private String clientId;

    @Value("${keycloak-client.client-secret}")
    private String clientSecret;


    private RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(NbcSsoClientApplication.class, args);
    }

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("get-token/{client}")
    @ResponseStatus(HttpStatus.OK)
    public String getToken(@PathVariable(value = "client") String client) {

        String username = "office-admin";
        String password = "123";

        String usernameService = "service";
        String passwordService = "123";

        TokenDTO tokenDTO;

        if (client.equals("service")) {
            tokenDTO = getTokenDTO(usernameService, passwordService);
        } else {
            tokenDTO = getTokenDTO(username, password);
        }
        return tokenDTO.getAccessToken();
    }


    private TokenDTO getTokenDTO(String username, String password) {
        var uriBuilder = UriComponentsBuilder
                .fromHttpUrl(this.issueUri)
                .build();

        String clientId = this.clientId;
        String clientSecret = this.clientSecret;
        String grantTypePassword = "password";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("username", username);
        body.set("password", password);
        body.set("client_id", clientId);
        body.set("client_secret", clientSecret);
        body.set("grant_type", grantTypePassword);
//        body.set("scope", "email");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TokenDTO> response2 = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, TokenDTO.class);

        System.out.println(response2.getStatusCode());
        System.out.println();
        System.out.println(response2.getHeaders());
        System.out.println();
        System.out.println(response2.getBody());
        System.out.println();
        System.out.println(response2.getBody().getAccessToken());

        return response2.getBody();
    }


    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 20)
    public void getTokensForTestUsers() {
        System.out.printf("%s %s %s %n", issueUri, clientId, clientSecret);

        String nbcAdminUsername = "nbc-admin";
        String nbcAdminPass = "123";

        String nbcBOUsername = "nbc-bo";
        String nbcBOPass = "123";

        Map<String, String> admin = Map.of("username", nbcAdminUsername, "password", nbcAdminPass);
        Map<String, String> branchOperator = Map.of("username", nbcBOUsername, "password", nbcBOPass);

        var uriBuilder = UriComponentsBuilder
                .fromHttpUrl(this.issueUri)
                .build();

        String clientId = this.clientId;
        String clientSecret = this.clientSecret;
        String grantTypePassword = "password";

        System.out.println("\n\n\n\n");
        for (Map<String, String> user : List.of(admin, branchOperator)) {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.set("username", user.get("username"));
            body.set("password", user.get("password"));
            body.set("client_id", clientId);
            body.set("client_secret", clientSecret);
            body.set("grant_type", grantTypePassword);
            body.set("scope", "openid email");


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<TokenDTO> response2 = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, TokenDTO.class);


            System.out.println("user : " + user.get("username"));
            System.out.println(response2.getStatusCode());
            System.out.println(response2.getBody().getAccessToken());
            System.out.println("---------------");
        }
    }

}
