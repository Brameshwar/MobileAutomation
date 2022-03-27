package com.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleTestData {

    String searchKey;
    String numberOfOutput;

}
