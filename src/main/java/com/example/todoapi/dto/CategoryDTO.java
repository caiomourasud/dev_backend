package com.example.todoapi.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryDTO {
    private Long id;
    
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
} 