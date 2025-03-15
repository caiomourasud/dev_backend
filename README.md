# Todo List API

API REST em Spring Boot para gerenciamento de tarefas e categorias.

## Tecnologias
- Java 17
- Spring Boot 3.2.3
- H2 Database
- Maven

## Como Executar
```bash
mvn spring-boot:run
```
Acesse: http://localhost:8080

## Endpoints

### Categorias
```
GET    /api/categories      - Lista todas as categorias
GET    /api/categories/{id} - Busca categoria por ID
POST   /api/categories      - Cria categoria
PUT    /api/categories/{id} - Atualiza categoria
DELETE /api/categories/{id} - Remove categoria

Exemplo POST/PUT:
{
    "name": "Trabalho",
    "description": "Tarefas do trabalho"
}
```

### Tarefas (Todos)
```
GET    /api/todos          - Lista todas as tarefas
GET    /api/todos/{id}     - Busca tarefa por ID
POST   /api/todos          - Cria tarefa
PUT    /api/todos/{id}     - Atualiza tarefa
DELETE /api/todos/{id}     - Remove tarefa

Exemplo POST/PUT:
{
    "title": "Reunião",
    "description": "Reunião de planejamento",
    "priority": "HIGH",
    "categoryId": 1,
    "dueDate": "2024-03-20T14:00:00"
}
```

### Filtros e Ordenação
```
GET /api/todos?completed=false          - Tarefas não concluídas
GET /api/todos?priority=HIGH            - Filtra por prioridade
GET /api/todos?sortBy=createdAt        - Ordena por data de criação
GET /api/todos?sortDirection=DESC      - Ordem decrescente
GET /api/todos?categoryId=1            - Tarefas de uma categoria
```

## Autor
Caio Moura

[Link para vídeo demonstração]