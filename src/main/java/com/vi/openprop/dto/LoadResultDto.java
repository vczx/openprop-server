package com.vi.openprop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoadResultDto {

    public LoadResultDto(Integer batchNum) {
        this.batchNum = batchNum;
    }

    Integer batchNum;
    boolean loadFromUra;
    boolean persistToDb;
    boolean persistToFile;
}
