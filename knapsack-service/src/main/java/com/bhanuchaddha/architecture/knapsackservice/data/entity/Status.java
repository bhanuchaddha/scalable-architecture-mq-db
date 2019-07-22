package com.bhanuchaddha.architecture.knapsackservice.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {

    @JsonProperty("submitted") SUBMITTED,
    @JsonProperty("started") STARTED,
    @JsonProperty("completed") COMPLETED
    ;


}
