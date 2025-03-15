package com.example.todoapi.service;

import com.example.todoapi.dto.TodoDTO;
import com.example.todoapi.model.Category;
import com.example.todoapi.model.Todo;
import com.example.todoapi.repository.CategoryRepository;
import com.example.todoapi.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<TodoDTO> findAll(Boolean completed, String priority, String sortBy, String sortDirection) {
        log.info("Buscando todos com filtros - completed: {}, priority: {}, sortBy: {}, sortDirection: {}", 
                 completed, priority, sortBy, sortDirection);
        
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
        
        List<TodoDTO> todos = todoRepository.findAll(spec, sort).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        log.info("Encontrados {} todos", todos.size());
        return todos;
    }

    @Transactional(readOnly = true)
    public TodoDTO findById(Long id) {
        log.info("Buscando todo por ID: {}", id);
        return todoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> {
                    log.error("Todo não encontrado com ID: {}", id);
                    return new EntityNotFoundException("Todo not found with id: " + id);
                });
    }

    @Transactional
    public TodoDTO create(TodoDTO todoDTO) {
        log.info("Criando novo todo: {}", todoDTO.getTitle());
        Todo todo = new Todo();
        updateTodoFromDTO(todo, todoDTO);
        Todo savedTodo = todoRepository.save(todo);
        log.info("Todo criado com ID: {}", savedTodo.getId());
        return convertToDTO(savedTodo);
    }

    @Transactional
    public TodoDTO update(Long id, TodoDTO todoDTO) {
        log.info("Atualizando todo ID: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Todo não encontrado para atualização. ID: {}", id);
                    return new EntityNotFoundException("Todo not found with id: " + id);
                });
        
        updateTodoFromDTO(todo, todoDTO);
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo atualizado com sucesso. ID: {}", id);
        return convertToDTO(updatedTodo);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando todo ID: {}", id);
        if (!todoRepository.existsById(id)) {
            log.error("Todo não encontrado para deleção. ID: {}", id);
            throw new EntityNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
        log.info("Todo deletado com sucesso. ID: {}", id);
    }

    private void updateTodoFromDTO(Todo todo, TodoDTO todoDTO) {
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setCompleted(todoDTO.isCompleted());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setPriority(todoDTO.getPriority());
        
        if (todoDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(todoDTO.getCategoryId())
                    .orElseThrow(() -> {
                        log.error("Categoria não encontrada. ID: {}", todoDTO.getCategoryId());
                        return new EntityNotFoundException("Category not found with id: " + todoDTO.getCategoryId());
                    });
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