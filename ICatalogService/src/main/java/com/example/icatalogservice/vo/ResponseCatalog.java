package com.example.icatalogservice.vo;

import com.example.icatalogservice.entity.CatalogEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseCatalog {
	private String productId;
	private String productName;
	private Integer unitPrice;
	private Integer stock;
	private LocalDateTime createdAt;

	public static ResponseCatalog of(CatalogEntity entity) {
		ResponseCatalog dto = new ResponseCatalog();
		dto.setProductId(entity.getProductId());
		dto.setProductName(entity.getProductName());
		dto.setUnitPrice(entity.getUnitPrice());
		dto.setStock(entity.getStock());
		dto.setCreatedAt(entity.getCreatedAt());
		return dto;
	}
}
