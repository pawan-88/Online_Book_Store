package com.bookstore.repository;

import com.bookstore.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    @Query("SELECT b FROM Block b WHERE b.name = :name AND b.warehouse.id = :warehouse_id")
    Block findByNameAndWarehouseId(@Param("name") String name, @Param("warehouse_id") Long warehouse_id);
}

