package com.example.nbc_sso_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenDTO {
    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("expires_in")
    String expiresIn;

    @JsonProperty("refresh_expires_in")
    String refreshExpiresIn;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("not-before-policy")
    String notBeforePolicy;

    @JsonProperty("session_state")
    String sessionState;

    @JsonProperty("scope")
    String scope;
}
