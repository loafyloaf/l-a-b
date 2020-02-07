package com.ibm.codey.loyalty.catalog.json;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class EventDefinition {

    @JsonbProperty
    private String eventName;

    @JsonbProperty
    private int pointValue;

}