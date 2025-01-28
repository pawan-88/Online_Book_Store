package com.bookstore.dto;

import lombok.Data;

@Data
public class RackDTO {

    private Long id;
    private String rackNumber;
    private Long blockId;

    public RackDTO(Long id, String rackNumber) {
    }

    public RackDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }
}
