package com.valuation.demo.controller;

import com.valuation.demo.model.Valuation;
import com.valuation.demo.service.ValuationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/valuations")
public class ValuationController {

	private static final Logger log = LoggerFactory.getLogger(ValuationController.class);

	private final ValuationService service;

	public ValuationController(ValuationService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Valuation> create(@Validated @RequestBody Valuation valuation) {
		Valuation created = service.create(valuation);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public List<Valuation> list() {
		return service.findAll();
	}

	/**
	 * Get a valuation by ID.
	 * @param id The ID of the valuation to retrieve (from path variable)
	 * @return The valuation or 404 if not found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Valuation> get(@PathVariable("id") Long id) {
		if (id == null) {
			return ResponseEntity.badRequest().build();
		}
		return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Update a valuation by ID.
	 * @param id The ID of the valuation to update (from path variable)
	 * @param valuation The valuation data to update with
	 * @return The updated valuation or 404 if not found
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Valuation> update(@PathVariable("id") Long id, @RequestBody Valuation valuation) {
		if (id == null) {
			return ResponseEntity.badRequest().build();
		}
		try {
			Valuation updated = service.update(id, valuation);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Delete a valuation by ID.
	 * @param id The ID of the valuation to delete (from path variable)
	 * @return No content if successful
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (id == null) {
			return ResponseEntity.badRequest().build();
		}
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
