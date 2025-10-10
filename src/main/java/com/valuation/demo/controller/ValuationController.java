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

	@GetMapping("/{id}")
	public ResponseEntity<Valuation> get(@PathVariable Long id) {
		return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Valuation> update(@PathVariable Long id, @RequestBody Valuation valuation) {
		try {
			Valuation updated = service.update(id, valuation);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
