# TOTVS Commercial AI (TCA)

Plataforma que analisa reuniões comerciais com inteligência artificial, calcula a
saúde do relacionamento com cada cliente e o desempenho de cada vendedor, e
oferece um assistente de chat e previsões para apoiar decisões comerciais.

Projeto desenvolvido para o Challenge FIAP em parceria com a TOTVS.

---

## Sobre o projeto

Toda reunião comercial registrada (via transcrição) passa por uma dupla análise
feita por IA:

- **Estratégica** — avalia o cliente: potencial de crescimento, risco de
  cancelamento, flexibilidade, impacto financeiro.
- **De desempenho** — avalia a condução da reunião pelo time comercial:
  engajamento, comunicação, tratamento de objeções, erros cometidos.

A partir dessas análises, um motor de regras (determinístico, não a IA) calcula
scores considerando o histórico de cada cliente e de cada vendedor ao longo do
tempo. O sistema também conta com:

- **Chat com IA** para perguntas em linguagem natural sobre reuniões e
  clientes, com busca semântica sobre o histórico (RAG).
- **Previsões** — leitura prospectiva de uma reunião futura específica, ou da
  trajetória geral de um cliente com a empresa.
- **Auditoria automática** de alterações em dados sensíveis, via trigger no
  banco.

---

## Tecnologias utilizadas

| Camada | Tecnologia |
|---|---|
| Frontend | Next.js + TypeScript |
| Backend principal | Java 21 + Spring Boot |
| Microserviço de IA | Python + FastAPI |
| Banco de dados | PostgreSQL 16 + pgvector (hospedado no Supabase) |
| IA generativa | Google Gemini (Interactions API) |
| Embeddings | SentenceTransformer, local (paraphrase-multilingual-MiniLM-L12-v2) |
| Autenticação | JWT |
| Deploy | Railway (Docker) |

---

## Arquitetura, em linhas gerais

```
Frontend (Next.js)
        │
        ▼
API Java (Spring Boot) ──────► Banco (Supabase / Postgres + pgvector)
        │                              ▲
        ▼                              │
Microserviço Python (FastAPI) ─────────┘
        │
        ▼
   Google Gemini
```

- O **Java** é dono de praticamente todo o CRUD, das regras de negócio, da
  autenticação e do cálculo de score. É ele quem o frontend acessa.
- O **Python** é responsável só pela parte de IA: enviar transcrições e
  contexto para o Gemini, gerar embeddings localmente, e montar as respostas
  do chat e das previsões. Não tem acesso público — só o Java conversa com
  ele, por chamada HTTP autenticada com uma chave compartilhada.
- O **banco** guarda tudo, incluindo os vetores de embedding (via extensão
  `pgvector`) usados na busca semântica do chat.

---

## Funcionalidades Disponíveis

- Cadastro e autenticação de usuários (JWT)
- CRUD de clientes, reuniões e squads
- Upload de transcrição e análise dupla por IA (estratégica + desempenho)
- Motor de cálculo de score com histórico e multiplicadores de tendência
- Chat com IA com busca semântica sobre reuniões passadas
- Geração de título automático de conversa
- Previsão de reunião futura (com base no histórico do cliente)
- Previsão de trajetória de cliente (com base no estado agregado + histórico)
- Auditoria automática de alterações via trigger no banco
- Limpeza automática de transcrições/previsões que falharam no processamento

---

## Como executar o projeto

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose
- Uma conta no [Supabase](https://supabase.com) (gratuita) com a extensão
  `pgvector` habilitada — ou qualquer Postgres 16+ com `pgvector`
- Uma chave de API do [Google Gemini](https://ai.google.dev/)

### 1. Preparar o banco de dados

No SQL Editor do Supabase (ou via `psql`), rode os scripts na seguinte ordem,
a partir do repositório [`tca-database`](https://github.com/Scacchetti-Lab/tca-database):

```
1. CREATE EXTENSION IF NOT EXISTS vector;
2. schema/ (schema completo das tabelas)
3. functions/ (funções auxiliares e de auditoria)
4. Triggers/ (triggers de auditoria e limpeza automática)
```

### 2. Clonar os repositórios

```bash
git clone https://github.com/Scacchetti-Lab/tca-database.git
git clone https://github.com/Scacchetti-Lab/tca-api
git clone https://github.com/Scacchetti-Lab/tca-processing
git clone <repo-do-frontend>  
```

### 3. Configurar as variáveis de ambiente

Copie o `.env.example` de cada serviço para `.env` e preencha:

**API Java:**
```
PG_CONNECTIONSTRING_MODEL=jdbc:postgresql://<host-do-pooler>:5432/postgres?sslmode=require
PG_USERNAME=postgres.<ref-do-projeto-supabase>
PG_PASSWORD=<senha do Supabase>
PY_HOST_URL=http://api-python:8000
PY_SECRET_KEY=<uma chave qualquer, compartilhada com o Python>
JWT_SECRET_KEY=<gere um valor aleatório>
```

**Microserviço Python:**
```
DB_HOST=postgresql+psycopg://postgres.<ref-do-projeto-supabase>:<senha>@<host-do-pooler>:5432/postgres
GEMINI_API_KEY=<sua chave do Gemini>
GEMINI_MODEL_NAME=<modelo do Gemini a ser usado>
X_ACCESS_KEY=<a mesma chave usada no AI_SERVICE_API_KEY do Java>
```

> Use sempre a connection string do tipo **Session pooler** do Supabase
> (porta 5432, usuário no formato `postgres.<ref>`), não a "Direct
> connection" — a direta só funciona em rede com suporte a IPv6.

### 4. Subir os serviços

Na raiz do projeto, com o `docker-compose.yml`:

```bash
docker compose up --build
```

Isso sobe a API Java e o microserviço Python juntos, na mesma rede interna.
O banco continua no Supabase — não é subido em container.

### 5. Acessar

- API Java: `http://localhost:7057`
- O microserviço Python não é exposto publicamente por padrão (só a API Java
  conversa com ele, pela rede interna do Docker)

---

## Deploy

O deploy de referência é feito no [Railway](https://railway.com), com dois
serviços (Java e Python) no mesmo projeto, e o banco continuando externo, no
Supabase. Consulte os `Dockerfile` de cada serviço para o processo de build
usado em produção.

---

## Equipe GAPC
| Nome Completo | RM |
| --- | --- |
| Camila Martins  | 561492 |
| Gabriel Amara  | 561403 |
| Guilherme Goody | 564417 |
| Luís Felipe Scacchetti Mariano  | 562241 |
| Pedro Lucas Almeida Cunha  | 566256 |

## Licença

[MIT LICENSE](./LICENSE)