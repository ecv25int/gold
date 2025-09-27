package com.goldapp.repository;

import com.goldapp.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByCedula(String cedula);
    
    List<Client> findByActiveTrue();
    
    Page<Client> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.active = true AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.cedula) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Client> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    List<Client> findByClientType(Client.ClientType clientType);
    
    @Query("SELECT c FROM Client c WHERE c.active = true AND c.city = :city")
    List<Client> findByCity(@Param("city") String city);
    
    @Query("SELECT c FROM Client c WHERE c.active = true AND c.province = :province")
    List<Client> findByProvince(@Param("province") String province);
    
    boolean existsByCedula(String cedula);
    
    boolean existsByEmail(String email);
}