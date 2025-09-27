package com.goldapp.repository;

import com.goldapp.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    
    Optional<Provider> findByCedula(String cedula);
    
    List<Provider> findByActiveTrue();
    
    Page<Provider> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Provider p WHERE p.active = true AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.cedula) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Provider> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    List<Provider> findByProviderType(Provider.ProviderType providerType);
    
    @Query("SELECT p FROM Provider p WHERE p.active = true AND p.city = :city")
    List<Provider> findByCity(@Param("city") String city);
    
    @Query("SELECT p FROM Provider p WHERE p.active = true AND p.province = :province")
    List<Provider> findByProvince(@Param("province") String province);
    
    boolean existsByCedula(String cedula);
    
    boolean existsByEmail(String email);
}