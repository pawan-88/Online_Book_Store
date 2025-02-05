package com.bookstore.repository;

import com.bookstore.model.Role;
import com.bookstore.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT * FROM role WHERE role_name = :roleName", nativeQuery = true)
    Optional<Role> findByRoleName(@Param("roleName") String roleName);
}
