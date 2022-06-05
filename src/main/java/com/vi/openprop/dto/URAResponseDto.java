package com.vi.openprop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class URAResponseDto {
    @JsonProperty("Result")
    private List<ProjectDto> result;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Message")
    private String message;
}
