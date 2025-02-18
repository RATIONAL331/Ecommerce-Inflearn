package com.example.icatalogservice.controller;

import com.example.icatalogservice.service.CatalogService;
import com.example.icatalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {
	private final CatalogService catalogService;

	@GetMapping("/health_check")
	public String status() {
		return "I'm running!";
	}

	@GetMapping("/catalogs")
	public ResponseEntity<List<ResponseCatalog>> getCatalogs() {
		List<ResponseCatalog> result = catalogService.getAllCatalogs()
		                                             .stream()
		                                             .map(ResponseCatalog::of)
		                                             .toList();
		return ResponseEntity.ok(result);
	}
}
