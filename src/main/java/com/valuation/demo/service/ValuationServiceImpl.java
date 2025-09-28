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

	    // Only overwrite if incoming field is non-null (preserve existing values otherwise)
	    if (valuation.getOwnerName() != null) {
	        // Optional: trim and treat empty string as "no change" — uncomment if you want that behavior
	        // String v = valuation.getOwnerName().trim();
	        // if (!v.isEmpty()) existing.setOwnerName(v);
	        existing.setOwnerName(valuation.getOwnerName());
	    }
	    if (valuation.getOwnerMobile() != null) {
	        existing.setOwnerMobile(valuation.getOwnerMobile());
	    }
	    if (valuation.getCarpetArea() != null) {
	        existing.setCarpetArea(valuation.getCarpetArea());
	    }
	    if (valuation.getPossession() != null) {
	        existing.setPossession(valuation.getPossession());
	    }
	    if (valuation.getAddress() != null) {
	        existing.setAddress(valuation.getAddress());
	    }

	    // Do NOT update createdAt here — preserve original creation time
	    return repo.save(existing);
	}


	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
}