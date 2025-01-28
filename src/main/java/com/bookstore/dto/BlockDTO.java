package com.bookstore.dto;

import lombok.Data;

@Data
public class BlockDTO {

    private Long id;
    private String name;
    private Long warehouseId;

    public BlockDTO(Long id, String name) {
    }

    public BlockDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
