package com.valuation.demo.repository;

import com.valuation.demo.model.Valuation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValuationRepository extends JpaRepository<Valuation, Long> {

}