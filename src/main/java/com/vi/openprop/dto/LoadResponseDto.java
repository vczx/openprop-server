package com.vi.openprop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoadResponseDto {

    private List<LoadResultDto> loadResultDto;
    private boolean success;
    private String message;
}
