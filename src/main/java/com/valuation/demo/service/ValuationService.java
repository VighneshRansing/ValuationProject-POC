package com.valuation.demo.service;

import com.valuation.demo.model.Valuation;

import java.util.List;
import java.util.Optional;

public interface ValuationService {
	Valuation create(Valuation valuation);

	List<Valuation> findAll();

	Optional<Valuation> findById(Long id);

	Valuation update(Long id, Valuation valuation);

	void delete(Long id);
}