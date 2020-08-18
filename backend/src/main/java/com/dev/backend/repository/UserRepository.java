package com.dev.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.dev.backend.model.*;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select c from User c where c.email like %:keyword%")
    public User findByEmail2(@Param("keyword") String keyword);

    @Query(value = "select c from User c where c.email like %:keyword%")
    public List<User> findByEmails(@Param("keyword") String keyword);

    @Query(value = "select c from User c where c.firstName like %:keyword%")
    public List<User> findByFName(@Param("keyword") String keyword);

    @Query(value = "select c from User c where c.lastName like %:keyword%")
    public List<User> findByLName(@Param("keyword") String keyword);

    @Query(value = "select c from User c inner join c.roles r on r.role like %:keyword%")
    public List<User> findByRoles(@Param("keyword") String keyword);

    Optional<User> findByEmail(String email);

    @Query(value = "select r.role from User c inner join c.roles r on r.role like 'SITE_USER' or r.role like 'ADMIN_USER'"
            + "where c.email=:keyword")
    public String findRoleByUser(@Param("keyword") String keyword);

}
