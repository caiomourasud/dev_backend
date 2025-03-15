package com.example.todoapi.service;

import com.example.todoapi.dto.TodoDTO;
import com.example.todoapi.model.Category;
import com.example.todoapi.model.Todo;
import com.example.todoapi.repository.CategoryRepository;
import com.example.todoapi.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<TodoDTO> findAll(Boolean completed, String priority, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        
        Specification<Todo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (completed != null) {
                predicates.add(cb.equal(root.get("completed"), completed));
            }
            
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return todoRepository.findAll(spec, sort).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TodoDTO findById(Long id) {
        return todoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + id));
    }

    @Transactional
    public TodoDTO create(TodoDTO todoDTO) {
        Todo todo = new Todo();
        updateTodoFromDTO(todo, todoDTO);
        return convertToDTO(todoRepository.save(todo));
    }

    @Transactional
    public TodoDTO update(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + id));
        
        updateTodoFromDTO(todo, todoDTO);
        return convertToDTO(todoRepository.save(todo));
    }

    @Transactional
    public void delete(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new EntityNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }

    private void updateTodoFromDTO(Todo todo, TodoDTO todoDTO) {
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setCompleted(todoDTO.isCompleted());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setPriority(todoDTO.getPriority());
        
        if (todoDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(todoDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + todoDTO.getCategoryId()));
            todo.setCategory(category);
        }
    }

    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.isCompleted());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setDueDate(todo.getDueDate());
        dto.setPriority(todo.getPriority());
        if (todo.getCategory() != null) {
            dto.setCategoryId(todo.getCategory().getId());
        }
        return dto;
    }
} 