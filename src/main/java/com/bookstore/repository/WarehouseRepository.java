package com.bookstore.repository;

import com.bookstore.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query(value = "SELECT * FROM warehouse WHERE name = :name", nativeQuery = true)
    Warehouse findByName(@Param("name") String name);

}
