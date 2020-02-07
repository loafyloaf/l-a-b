package com.ibm.codey.loyalty.util;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IAMTokenServiceResponse {

    @JsonbProperty("access_token")
    public String accessToken;

    @JsonbProperty
    public long expiration;

}