package com.valuation.demo.service;

import com.valuation.demo.model.Valuation;
import com.valuation.demo.repository.ValuationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ValuationServiceImpl implements ValuationService {
	private final ValuationRepository repo;

	public ValuationServiceImpl(ValuationRepository repo) {
		this.repo = repo;
	}

	@Override
	public Valuation create(Valuation valuation) {
		valuation.setCreatedAt(LocalDateTime.now());
		return repo.save(valuation);
	}

	@Override
	public List<Valuation> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<Valuation> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Valuation update(Long id, Valuation valuation) {
		Valuation existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Valuation not found"));
		existing.setOwnerName(valuation.getOwnerName());
		existing.setOwnerMobile(valuation.getOwnerMobile());
		existing.setCarpetArea(valuation.getCarpetArea());
		existing.setPossession(valuation.getPossession());
		existing.setAddress(valuation.getAddress());
		return repo.save(existing);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
}