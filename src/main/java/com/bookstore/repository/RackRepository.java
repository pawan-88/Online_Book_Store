package com.bookstore.repository;

import com.bookstore.model.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    @Query("SELECT r FROM Rack r WHERE r.rackNumber = :rackNumber AND r.block.id = :block_id")
    Rack findByRackNumberAndBlockId(@Param("rackNumber") String rackNumber, @Param("block_id") Long block_id);


    @Query("SELECT COUNT(b) FROM Book b WHERE b.rack.id = :rackId")
    int countBooksInRack(@Param("rackId") Long rackId);

}
