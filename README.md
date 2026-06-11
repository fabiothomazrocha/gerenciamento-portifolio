# 🗂️ Sistema de Gerenciamento de Portfólio de Projetos
Sistema desenvolvido em Spring Boot 3 para gerenciamento completo do ciclo de vida de projetos, com controle de membros, orçamento, risco e status.
---

---

## ⚙️ Como executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Configurar banco de dados
```sql
CREATE DATABASE portfolio_db;
```

### 2. Configurar `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/portfolio_db
    username: seu_usuario
    password: sua_senha
```

### 3. Executar a aplicação
```bash
./mvnw spring-boot:run
```

A aplicação iniciará em: http://localhost:8080

---

## 📖 Documentação da API

Acesse o **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 🔐 Segurança

A API usa **HTTP Basic Authentication** com usuários em memória:

| Usuário | Senha | Permissões |
|---|---|---|
| `user` | `user123` | GET (leitura) |
| `manager` | `manager123` | GET + POST + PUT + PATCH |
| `admin` | `admin123` | Acesso total (inclui DELETE) |

---

## 📋 Endpoints

### Projetos `/api/v1/projetos`

| Método | Endpoint | Descrição | Papel mínimo |
|---|---|---|---|
| POST | `/` | Criar projeto | MANAGER |
| GET | `/` | Listar (paginado + filtros) | USER |
| GET | `/{id}` | Buscar por ID | USER |
| PUT | `/{id}` | Atualizar projeto | MANAGER |
| DELETE | `/{id}` | Excluir projeto | ADMIN |
| PATCH | `/{id}/status` | Transicionar status | MANAGER |
| POST | `/{id}/membros` | Associar membro | MANAGER |
| DELETE | `/{id}/membros/{membroId}` | Desassociar membro | MANAGER |
| GET | `/relatorio` | Relatório do portfólio | USER |

### Filtros na listagem
```
GET /api/v1/projetos?nome=sistema&status=EM_ANDAMENTO&gerenteId=1&page=0&size=10&sort=nome,asc
```

### Membros `/api/v1/membros`

| Método | Endpoint | Descrição | Papel mínimo |
|---|---|---|---|
| POST | `/` | Criar membro (via API ext.) | MANAGER |
| GET | `/` | Listar todos | USER |
| GET | `/{id}` | Buscar por ID | USER |

---

## 🛠️ Princípios aplicados

- **SOLID**: SRP (cada classe tem uma responsabilidade), OCP (extensível via interfaces), DIP (injeção por interfaces)
- **Clean Code**: nomes expressivos, funções curtas, sem magic numbers
- **MVC**: separação clara entre Controller → Service → Repository
- **DTOs**: nunca expõe entidades diretamente
- **Tratamento global de exceções**: `GlobalExceptionHandler` com `ProblemDetail` (RFC 7807)
- **Paginação**: `Pageable` com filtros JPQL dinâmicos

