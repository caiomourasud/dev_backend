package com.example.todoapi.service;

import com.example.todoapi.dto.TodoDTO;
import com.example.todoapi.model.Category;
import com.example.todoapi.model.Todo;
import com.example.todoapi.repository.CategoryRepository;
import com.example.todoapi.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo;
    private TodoDTO todoDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("Test Description");
        todo.setCompleted(false);
        todo.setCategory(category);
        todo.setPriority("HIGH");
        todo.setCreatedAt(LocalDateTime.now());

        todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Todo");
        todoDTO.setDescription("Test Description");
        todoDTO.setCompleted(false);
        todoDTO.setCategoryId(1L);
        todoDTO.setPriority("HIGH");
    }

    @Test
    void findAll_ShouldReturnListOfTodos() {
        when(todoRepository.findAll(any(Specification.class), any(Sort.class)))
            .thenReturn(Arrays.asList(todo));

        List<TodoDTO> result = todoService.findAll(false, "HIGH", "createdAt", "DESC");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(todo.getTitle(), result.get(0).getTitle());
        verify(todoRepository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void findById_WhenTodoExists_ShouldReturnTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        TodoDTO result = todoService.findById(1L);

        assertNotNull(result);
        assertEquals(todo.getTitle(), result.getTitle());
        verify(todoRepository).findById(1L);
    }

    @Test
    void findById_WhenTodoDoesNotExist_ShouldThrowException() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.findById(1L));
        verify(todoRepository).findById(1L);
    }

    @Test
    void create_WithValidCategory_ShouldReturnCreatedTodo() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoDTO result = todoService.create(todoDTO);

        assertNotNull(result);
        assertEquals(todoDTO.getTitle(), result.getTitle());
        verify(categoryRepository).findById(1L);
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void create_WithInvalidCategory_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.create(todoDTO));
        verify(categoryRepository).findById(1L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void update_WhenTodoExists_ShouldReturnUpdatedTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoDTO result = todoService.update(1L, todoDTO);

        assertNotNull(result);
        assertEquals(todoDTO.getTitle(), result.getTitle());
        verify(todoRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void update_WhenTodoDoesNotExist_ShouldThrowException() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.update(1L, todoDTO));
        verify(todoRepository).findById(1L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void delete_WhenTodoExists_ShouldDeleteTodo() {
        when(todoRepository.existsById(1L)).thenReturn(true);

        todoService.delete(1L);

        verify(todoRepository).existsById(1L);
        verify(todoRepository).deleteById(1L);
    }

    @Test
    void delete_WhenTodoDoesNotExist_ShouldThrowException() {
        when(todoRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> todoService.delete(1L));
        verify(todoRepository).existsById(1L);
        verify(todoRepository, never()).deleteById(any());
    }
} 