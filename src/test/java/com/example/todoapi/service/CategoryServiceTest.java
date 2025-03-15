package com.example.todoapi.service;

import com.example.todoapi.dto.CategoryDTO;
import com.example.todoapi.model.Category;
import com.example.todoapi.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Description");
    }

    @Test
    void findAll_ShouldReturnListOfCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        List<CategoryDTO> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category.getName(), result.get(0).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void findById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void findById_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(1L));
        verify(categoryRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnCreatedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.create(categoryDTO);

        assertNotNull(result);
        assertEquals(categoryDTO.getName(), result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_WhenCategoryExists_ShouldReturnUpdatedCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.update(1L, categoryDTO);

        assertNotNull(result);
        assertEquals(categoryDTO.getName(), result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(1L, categoryDTO));
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void delete_WhenCategoryExists_ShouldDeleteCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.delete(1L);

        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(1L));
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository, never()).deleteById(any());
    }
} 