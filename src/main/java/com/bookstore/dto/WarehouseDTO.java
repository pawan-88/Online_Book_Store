package com.bookstore.dto;

import lombok.Data;

@Data
public class WarehouseDTO{

    private Long id;
    private String name;
    private String location;

    public WarehouseDTO(Long id, String name) {
    }

    public WarehouseDTO() {

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
