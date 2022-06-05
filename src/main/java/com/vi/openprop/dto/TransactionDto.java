package com.vi.openprop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto implements Serializable {
    private String contractDate;
    private String area;
    private String price;
    private String propertyType;
    private String typeOfArea;
    private String tenure;
    private String floorRange;
    private String typeOfSale;
    private String district;
    private String noOfUnits;
    private String nettPrice;
}
