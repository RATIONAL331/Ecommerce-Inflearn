package com.example.icatalogservice.service.impl;

import com.example.icatalogservice.entity.CatalogEntity;
import com.example.icatalogservice.repository.CatalogRepository;
import com.example.icatalogservice.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
	private final CatalogRepository catalogRepository;

	@Override
	public List<CatalogEntity> getAllCatalogs() {
		return catalogRepository.findAll();
	}
}
