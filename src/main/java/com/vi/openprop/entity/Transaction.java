package com.vi.openprop.entity;

import com.vi.openprop.dto.TransactionDto;
import com.vi.openprop.helpers.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Transaction Entity, each transaction would require a MD5 hash with its creation date,update date
 * Free data set do not specify the exact floor. There are duplicates in the data set
 *
 * Any duplicate could happen between HDB landed and private landed in the same street
 *
 * Therefore we need a custom object model with ID Generator
 */
@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;
    @Column(name = "contract_date")
    private LocalDate contractDate;
    private Double area;
    @Column(name = "area_sf")
    private Integer squarefeetArea;
    private Integer price;
    @Column(name = "property_type")
    private String propertyType;
    @Column(name = "type_of_area")
    private String typeOfArea;
    private String tenure;
    @Column(name = "floor_range")
    private String floorRange;
    @Column(name = "type_of_sale")
    private String typeOfSale;
    private String district;
    @Column(name = "no_of_units")
    private Integer noOfUnits;
    @Column(name = "psf_price")
    private Integer psfPrice;
    @Column(name = "update_date")
    private LocalDate updateDate;
    @Column(name = "create_date")
    private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public Transaction(TransactionDto t) {
        final String mth = t.getContractDate().substring(0, 2);
        final String year = t.getContractDate().substring(2, 4);
        this.contractDate = LocalDate.of(Integer.parseInt("20" + year), Integer.parseInt(mth), 1);
        this.area = Double.parseDouble(t.getArea());
        this.price = Double.valueOf(t.getPrice()).intValue();
        this.squarefeetArea = sqmTosf(this.area);
        this.psfPrice = getPsf(this.price, this.squarefeetArea);
        this.propertyType = t.getPropertyType();
        this.typeOfArea = t.getTypeOfArea();
        this.tenure = t.getTenure();
        this.floorRange = t.getFloorRange();
        this.typeOfSale = t.getTypeOfSale();
        this.district = t.getDistrict();
        this.noOfUnits = Integer.valueOf(t.getNoOfUnits());
        this.updateDate = LocalDate.now();
        this.createDate = LocalDate.now();
        this.id = IdGenerator.generateId(this.toString()).orElseGet(() -> String.valueOf(hashCode()));
    }

    Integer sqmTosf(Double sqm) {
        return (int) Math.round(sqm * 10.7639);
    }

    Integer getPsf(Integer price, Integer sqf) {
        return Math.round(price / sqf);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "contractDate=" + contractDate +
                ", area=" + area +
                ", squarefeetArea=" + squarefeetArea +
                ", price=" + price +
                ", propertyType='" + propertyType + '\'' +
                ", typeOfArea='" + typeOfArea + '\'' +
                ", tenure='" + tenure + '\'' +
                ", floorRange='" + floorRange + '\'' +
                ", typeOfSale='" + typeOfSale + '\'' +
                ", district='" + district + '\'' +
                ", noOfUnits=" + noOfUnits +
                ", psfPrice=" + psfPrice +
                ", updateDate=" + updateDate +
                ", createDate=" + createDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(contractDate, that.contractDate) && Objects.equals(area, that.area) && Objects.equals(squarefeetArea, that.squarefeetArea) && Objects.equals(price, that.price) && Objects.equals(propertyType, that.propertyType) && Objects.equals(typeOfArea, that.typeOfArea) && Objects.equals(tenure, that.tenure) && Objects.equals(floorRange, that.floorRange) && Objects.equals(typeOfSale, that.typeOfSale) && Objects.equals(district, that.district) && Objects.equals(noOfUnits, that.noOfUnits) && Objects.equals(psfPrice, that.psfPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractDate, area, squarefeetArea, price, propertyType, typeOfArea, tenure, floorRange, typeOfSale, district, noOfUnits, psfPrice);
    }
}
