package com.ibm.codey.loyalty.util;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AppIDServiceGetUserProfileResponse {

    @JsonbProperty("identities")
    private Identity[] identities;

    @Getter @Setter
    public static class Identity {
        private String provider;
        private String id;
    }
}