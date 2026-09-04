-- =========================================================
-- TOTVS Commercial AI — schema completo
-- =========================================================

CREATE EXTENSION IF NOT EXISTS vector;

-- =========================================================
-- USERS
-- =========================================================
CREATE TABLE "users" (
                         "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                         "full_name" VARCHAR(255) NOT NULL,
                         "username" VARCHAR(100) UNIQUE NOT NULL,
                         "cpf" CHAR(11) UNIQUE NOT NULL,
                         "mobile_phone" VARCHAR(50),
                         "birth_date" DATE NOT NULL,
                         "email" VARCHAR(255) NOT NULL,
                         "password" VARCHAR(255) NOT NULL,
                         "profile_photo_url" VARCHAR(255),
                         "address_id" UUID NOT NULL,
                         "score" INTEGER DEFAULT 0,
                         "ai_token_used" INTEGER DEFAULT 0,
                         "status" VARCHAR(50) NOT NULL CHECK ("status" IN ('FORCE_CHANGE_PASSWORD', 'ACTIVE', 'INACTIVE', 'BANNED', 'FIRST_LOGIN', 'CHECKING_MFA')),
                         "is_deleted" BOOLEAN NOT NULL DEFAULT false,
                         "use_mfa" BOOLEAN NOT NULL DEFAULT false,
                         "mfa_token" VARCHAR(255),
                         "modified_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                         "created_by" UUID,
                         "created_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                         PRIMARY KEY ("id")
);

-- =========================================================
-- ADDRESSES
-- =========================================================
CREATE TABLE "addresses" (
                             "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                             "postalcode" CHAR(8) NOT NULL,
                             "number" VARCHAR(20) NOT NULL,
                             "name" VARCHAR(255) NOT NULL,
                             "complement" VARCHAR(255),
                             "state" VARCHAR(255) NOT NULL,
                             "uf" CHAR(2) NOT NULL,
                             "neighborhood" VARCHAR(255) NOT NULL,
                             PRIMARY KEY ("id")
);

-- =========================================================
-- PROFILES
-- CORRIGIDO: colunas ainda estavam com prefixo pf_ enquanto a PK
-- referenciava "id".
-- =========================================================
CREATE TABLE "profiles" (
                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                            "name" VARCHAR(255) NOT NULL,
                            "is_admin" BOOLEAN NOT NULL DEFAULT false,
                            PRIMARY KEY ("id")
);

-- =========================================================
-- USER_PROFILES
-- PK composta funciona agora que meeting_employees passou a referenciar
-- users diretamente, não mais esta tabela.
-- =========================================================
CREATE TABLE "user_profiles" (
                                 "user_id" UUID NOT NULL,
                                 "profile_id" UUID NOT NULL,
                                 PRIMARY KEY ("user_id", "profile_id")
);

-- =========================================================
-- SALESPERSONS
-- Campos derivados (_pp, total_leads) removidos: são calculáveis a partir
-- de meeting_performance_scores e ficariam desatualizados como cache.
-- =========================================================
CREATE TABLE "salespersons" (
                                "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                "user_id" UUID UNIQUE NOT NULL,
                                "total_meetings" INTEGER NOT NULL DEFAULT 0,
                                "need_training" BOOLEAN NOT NULL DEFAULT false,
                                "squad_id" UUID NOT NULL,
                                PRIMARY KEY ("id")
);

-- =========================================================
-- SQUADS
-- =========================================================
CREATE TABLE "squads" (
                          "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                          "name" VARCHAR(255) NOT NULL,
                          "manager_id" UUID NOT NULL,
                          "description" VARCHAR(255) NOT NULL,
                          "performance_avg" SMALLINT NOT NULL DEFAULT 0,
                          PRIMARY KEY ("id")
);

-- =========================================================
-- MANAGERS
-- =========================================================
CREATE TABLE "managers" (
                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                            "user_id" UUID UNIQUE NOT NULL,
                            "total_meetings" INTEGER NOT NULL DEFAULT 0,
                            "total_squads" SMALLINT NOT NULL DEFAULT 0,
                            "unit" VARCHAR(255),
                            "score_goal" SMALLINT,
                            "created_by" UUID NOT NULL,
                            "created_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                            "modified_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                            PRIMARY KEY ("id")
);

-- =========================================================
-- MEETINGS
-- =========================================================
CREATE TABLE "meetings" (
                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                            "totvs_id" INTEGER UNIQUE,
                            "transcript_id" UUID UNIQUE NOT NULL,
                            "title" VARCHAR(255) NOT NULL,
                            "summary" TEXT NOT NULL,
                            "scheduled" TIMESTAMPTZ NOT NULL,
                            "duration_min" INTEGER,
                            "status" VARCHAR(50) NOT NULL CHECK ("status" IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                            "performance_avg" SMALLINT,
                            "feedback" TEXT NOT NULL,
                            "priority" VARCHAR(50) CHECK ("priority" IN ('HIGH', 'MEDIUM', 'LOW')),
                            "client_id" UUID NOT NULL,
                            "is_deleted" BOOLEAN NOT NULL DEFAULT false,
                            "embeddings" VECTOR(384),
                            PRIMARY KEY ("id")
);

-- =========================================================
-- MEETING_EMPLOYEES
-- Agora referencia users diretamente (antes era user_profiles).
-- feedback_tip nullable: só é preenchido quando a transcrição permitir
-- identificar quem fez cada fala (diarização por locutor).
-- =========================================================
CREATE TABLE "meeting_employees" (
                                     "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                     "meeting_id" UUID NOT NULL,
                                     "user_id" UUID NOT NULL,
                                     "feedback_tip" TEXT,
                                     PRIMARY KEY ("id")
);

-- =========================================================
-- CLIENTS
-- squad_id: vínculo com a carteira, necessário para calcular o percentual
-- de impacto financeiro sobre a receita total da carteira.
-- =========================================================
CREATE TABLE "clients" (
                           "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                           "name" VARCHAR(255) NOT NULL,
                           "fantasy_name" VARCHAR(255) NOT NULL,
                           "cnpj" CHAR(14) UNIQUE NOT NULL,
                           "address_id" UUID NOT NULL,
                           "squad_id" UUID,
                           "revenue" DECIMAL(15,2) NOT NULL DEFAULT 0,
                           "status" VARCHAR(255) NOT NULL CHECK ("status" IN ('CRITIC', 'BAD', 'OK', 'GOOD')),
                           "embeddings" VECTOR(384),
                           PRIMARY KEY ("id")
);

-- =========================================================
-- CLIENT_ANALYSES
-- Cache 1:1 do estado atual do cliente. O histórico vive em
-- meeting_strategic_analyses / meeting_strategic_scores.
-- =========================================================
CREATE TABLE "client_analyses" (
                                   "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                   "client_id" UUID UNIQUE NOT NULL,
                                   "performance" SMALLINT NOT NULL,
                                   "closing_probability" SMALLINT NOT NULL,
                                   "flexibility" SMALLINT NOT NULL,
                                   "risk" SMALLINT NOT NULL,
                                   "financial_impact" DECIMAL(8,2) NOT NULL,
                                   "financial_status" VARCHAR(255) NOT NULL CHECK ("financial_status" IN ('MINIMAL', 'LOW', 'MEDIUM', 'HIGH', 'SEVERAL')),
                                   PRIMARY KEY ("id")
);

-- =========================================================
-- TRANSCRIPTS
-- =========================================================
CREATE TABLE "transcripts" (
                               "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                               "raw_transcript" TEXT NOT NULL,
                               "resume" TEXT NOT NULL,
                               "status" VARCHAR(255) NOT NULL CHECK ("status" IN ('NOT_STARTED', 'IN_PROGRESS', 'DONE', 'ERROR', 'CANCELLED')),
                               PRIMARY KEY ("id")
);

-- =========================================================
-- SESSIONS
-- =========================================================
CREATE TABLE "sessions" (
                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                            "title" VARCHAR(255) NOT NULL,
                            "last_activity" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                            "created_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                            "user_id" UUID NOT NULL,
                            "model_used" VARCHAR(255) NOT NULL CHECK ("model_used" IN ('FAST', 'PRO')),
                            "last_interaction_id" VARCHAR(255),
                            PRIMARY KEY ("id")
);

-- =========================================================
-- MESSAGES
-- =========================================================
CREATE TABLE "messages" (
                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                            "session_id" UUID NOT NULL,
                            "role" VARCHAR(255) NOT NULL CHECK ("role" IN ('SALESPERSON', 'MANAGER', 'TOTVS_AI')),
                            "content" TEXT NOT NULL,
                            "created_on" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                            PRIMARY KEY ("id")
);

-- =========================================================
-- CONFIGURATIONS
-- =========================================================
CREATE TABLE "configurations" (
                                  "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                  "key" VARCHAR(255) UNIQUE NOT NULL,
                                  "type" VARCHAR(255) NOT NULL CHECK ("type" IN ('STRING', 'INTEGER', 'DOUBLE', 'BOOLEAN')),
                                  "content" TEXT NOT NULL,
                                  PRIMARY KEY ("id")
);

-- =========================================================
-- TRANSCRIPT_CHUNKS
-- =========================================================
CREATE TABLE "transcript_chunks" (
                                     "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                     "transcript_id" UUID NOT NULL,
                                     "chunk_text" TEXT NOT NULL,
                                     "embedding" VECTOR(384) NOT NULL,
                                     "created_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                                     PRIMARY KEY ("id")
);

-- =========================================================
-- AUDITS
-- =========================================================
CREATE TABLE "audits" (
                          "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                          "entity_name" VARCHAR(255) NOT NULL,
                          "user_id" UUID,
                          "created_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                          "previous" JSONB NOT NULL,
                          "new" JSONB NOT NULL,
                          PRIMARY KEY ("id")
);

-- =========================================================
-- MEETING_STRATEGIC_ANALYSES — notas brutas da IA (type=DIRECTOR)
-- =========================================================
CREATE TABLE "meeting_strategic_analyses" (
                                              "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                              "meeting_id" UUID UNIQUE NOT NULL,
                                              "client_id" UUID NOT NULL,
                                              "company_status" VARCHAR(50) NOT NULL CHECK ("company_status" IN ('GOOD', 'OK', 'BAD', 'CRITIC')),
                                              "company_performance" NUMERIC(4,2) NOT NULL,
                                              "closing_probability" NUMERIC(4,2) NOT NULL,
                                              "flexibility" NUMERIC(4,2) NOT NULL,
                                              "risk" NUMERIC(4,2) NOT NULL,
                                              "financial_impact" NUMERIC(4,2) NOT NULL,
                                              "financial_impact_value" DECIMAL(15,2),
                                              "financial_impact_status" VARCHAR(50) NOT NULL CHECK ("financial_impact_status" IN ('MINIMAL', 'LOW', 'MEDIUM', 'HIGH', 'SEVERAL')),
                                              "created_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                                              PRIMARY KEY ("id")
);

-- =========================================================
-- MEETING_PERFORMANCE_ANALYSES — notas brutas da IA (type=SALESPERSON)
-- Nota coletiva: uma linha por reunião, conforme a documentação
-- ("vale igualmente para todos os presentes").
-- =========================================================
CREATE TABLE "meeting_performance_analyses" (
                                                "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                                "meeting_id" UUID UNIQUE NOT NULL,
                                                "engagement" NUMERIC(4,2) NOT NULL,
                                                "communication_quality" NUMERIC(4,2) NOT NULL,
                                                "opportunities_seized" NUMERIC(4,2) NOT NULL,
                                                "objection_handling" NUMERIC(4,2) NOT NULL,
                                                "missed_opportunities" NUMERIC(4,2) NOT NULL DEFAULT 0,
                                                "leads_converted" INTEGER NOT NULL,
                                                "tips" TEXT NOT NULL,
                                                "created_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                                                PRIMARY KEY ("id")
);

-- =========================================================
-- MEETING_STRATEGIC_SCORES — resultado calculado pelo Java (1:1 com reunião)
-- =========================================================
CREATE TABLE "meeting_strategic_scores" (
                                            "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                            "meeting_id" UUID UNIQUE NOT NULL,
                                            "client_id" UUID NOT NULL,
                                            "base_score" NUMERIC(6,2) NOT NULL,
                                            "multiplier" NUMERIC(3,2) NOT NULL,
                                            "wallet_percentage" NUMERIC(6,3),
                                            "financial_impact_level" VARCHAR(20) CHECK ("financial_impact_level" IN ('ALTO', 'MEDIO', 'BAIXO')),
                                            "financial_factor" NUMERIC(3,2) NOT NULL DEFAULT 1.00,
                                            "final_score" NUMERIC(8,2) NOT NULL,
                                            "calculated_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                                            PRIMARY KEY ("id")
);

-- =========================================================
-- MEETING_PERFORMANCE_SCORES — resultado calculado pelo Java
-- UMA LINHA POR VENDEDOR POR REUNIÃO: a nota base é coletiva, mas o
-- multiplicador é individual (últimas 5 reuniões daquele vendedor),
-- então o score final difere entre participantes da mesma reunião.
-- =========================================================
CREATE TABLE "meeting_performance_scores" (
                                              "id" UUID NOT NULL DEFAULT (gen_random_uuid()),
                                              "meeting_id" UUID NOT NULL,
                                              "salesperson_id" UUID NOT NULL,
                                              "base_score" NUMERIC(6,2) NOT NULL,
                                              "multiplier" NUMERIC(3,2) NOT NULL,
                                              "streak_type" VARCHAR(20) CHECK ("streak_type" IN ('ALTA', 'QUEDA', 'ESTAVEL')),
                                              "streak_count" SMALLINT NOT NULL DEFAULT 0,
                                              "final_score" NUMERIC(8,2) NOT NULL,
                                              "calculated_at" TIMESTAMPTZ NOT NULL DEFAULT (now()),
                                              PRIMARY KEY ("id"),
                                              CONSTRAINT "meeting_performance_scores_unique" UNIQUE ("meeting_id", "salesperson_id")
);

-- =========================================================
-- ÍNDICES
-- =========================================================
CREATE UNIQUE INDEX "users_unique_0" ON "users" ("cpf", "username");
CREATE INDEX "users_cpf_index" ON "users" ("cpf");

CREATE UNIQUE INDEX "addresses_unique_0" ON "addresses" ("postalcode", "number", "complement");

CREATE INDEX "squads_manager_id_index" ON "squads" ("manager_id");

-- CORRIGIDO: índice referenciava a coluna user_profile_id, que não existe mais.
CREATE UNIQUE INDEX "meeting_employees_unique_participant" ON "meeting_employees" ("meeting_id", "user_id");
CREATE INDEX "meeting_employees_meeting_id_index" ON "meeting_employees" ("meeting_id");

CREATE INDEX "clients_squad_id_index" ON "clients" ("squad_id");

CREATE INDEX "messages_session_id_index" ON "messages" ("session_id");
CREATE INDEX "messages_created_on_index" ON "messages" ("created_on");

CREATE INDEX "transcript_chunks_embedding_index" ON "transcript_chunks" USING HNSW ("embedding" vector_cosine_ops);

CREATE INDEX "meeting_strategic_scores_client_index" ON "meeting_strategic_scores" ("client_id", "calculated_at");
CREATE INDEX "meeting_performance_scores_salesperson_index" ON "meeting_performance_scores" ("salesperson_id", "calculated_at");

-- =========================================================
-- COMENTÁRIOS
-- =========================================================
COMMENT ON COLUMN "users"."modified_on" IS 'ATUALIZA QUANDO QUALQUER OPERACAO CRUD OCORRER (EXCETO READ)';
COMMENT ON COLUMN "users"."created_on" IS 'DATA DE HOJE EM CASO DE NULO';

COMMENT ON COLUMN "salespersons"."total_meetings" IS 'total de reuniões participadas no último mês';

COMMENT ON COLUMN "managers"."total_meetings" IS 'total de reuniões do gerente e da sua equipe';
COMMENT ON COLUMN "managers"."total_squads" IS 'total de squads geridas por esse gerente';
COMMENT ON COLUMN "managers"."unit" IS 'unidade/filial do gerente';
COMMENT ON COLUMN "managers"."score_goal" IS 'meta de score definida para a equipe';
COMMENT ON COLUMN "managers"."created_by" IS 'usuário (admin) que criou este registro de gerente';

COMMENT ON COLUMN "meetings"."scheduled" IS 'Data e hora em que a reunião foi agendada';
COMMENT ON COLUMN "meetings"."duration_min" IS 'Duração da reunião em minutos';
COMMENT ON COLUMN "meetings"."type" IS 'Define qual tabela de analytic aplica: SALESPERSON -> meeting_performance_analyses, DIRECTOR -> meeting_strategic_analyses';

COMMENT ON COLUMN "meeting_employees"."feedback_tip" IS 'Dica individualizada por participante -- nulo quando a transcrição não identifica quem fez cada fala (sem diarização por locutor)';

COMMENT ON COLUMN "clients"."revenue" IS 'faturamento mensal';
COMMENT ON COLUMN "clients"."squad_id" IS 'Carteira do cliente -- base do cálculo de percentual de impacto financeiro';

COMMENT ON COLUMN "transcript_chunks"."transcript_id" IS 'Identificador da transcrição';
COMMENT ON COLUMN "transcript_chunks"."embedding" IS 'Vetor de embedding gerado pelo modelo local (384 dimensões)';

COMMENT ON COLUMN "audits"."entity_name" IS 'Nome da entidade do banco de dados';
COMMENT ON COLUMN "audits"."user_id" IS 'Usuário que realizou a alteração';
COMMENT ON COLUMN "audits"."created_at" IS 'Data de criação';
COMMENT ON COLUMN "audits"."previous" IS 'Último valor antes da modificação';
COMMENT ON COLUMN "audits"."new" IS 'Novo valor após a modificação';

COMMENT ON COLUMN "meeting_strategic_analyses"."client_id" IS 'Desnormalizado de meetings.client_id para evitar join -- mantido em sincronia pela aplicação';
COMMENT ON COLUMN "meeting_strategic_analyses"."financial_impact" IS 'Nota qualitativa 0-10 do impacto financeiro (gerada pela IA)';
COMMENT ON COLUMN "meeting_strategic_analyses"."financial_impact_value" IS 'Valor monetário em risco ou oportunidade identificado na reunião';

COMMENT ON COLUMN "meeting_performance_analyses"."missed_opportunities" IS 'Erros e oportunidades perdidas (0-8) -- SUBTRAI do score base';

COMMENT ON COLUMN "meeting_strategic_scores"."wallet_percentage" IS 'Percentual do valor em risco/oportunidade sobre a receita total da carteira';
COMMENT ON COLUMN "meeting_strategic_scores"."financial_factor" IS 'Fator do ajuste financeiro aplicado (ex: 1.20, 0.90, 1.00)';

COMMENT ON COLUMN "meeting_performance_scores"."base_score" IS 'Nota base coletiva da reunião -- idêntica para todos os vendedores presentes';
COMMENT ON COLUMN "meeting_performance_scores"."multiplier" IS 'Multiplicador individual de tendência do vendedor (últimas 5 reuniões)';
COMMENT ON COLUMN "meeting_performance_scores"."streak_type" IS 'Direção da sequência atual (ALTA/QUEDA/ESTAVEL) -- guardado para auditoria';
COMMENT ON COLUMN "meeting_performance_scores"."streak_count" IS 'Quantidade de reuniões consecutivas na sequência atual';

-- =========================================================
-- FOREIGN KEYS
-- =========================================================
ALTER TABLE "meeting_employees" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "meeting_employees" ADD FOREIGN KEY ("meeting_id") REFERENCES "meetings" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "sessions" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "salespersons" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "salespersons" ADD FOREIGN KEY ("squad_id") REFERENCES "squads" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "clients" ADD FOREIGN KEY ("address_id") REFERENCES "addresses" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "clients" ADD FOREIGN KEY ("squad_id") REFERENCES "squads" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "meetings" ADD FOREIGN KEY ("client_id") REFERENCES "clients" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "meetings" ADD FOREIGN KEY ("transcript_id") REFERENCES "transcripts" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "squads" ADD FOREIGN KEY ("manager_id") REFERENCES "managers" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "user_profiles" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "user_profiles" ADD FOREIGN KEY ("profile_id") REFERENCES "profiles" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "users" ADD FOREIGN KEY ("address_id") REFERENCES "addresses" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "users" ADD FOREIGN KEY ("created_by") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "client_analyses" ADD FOREIGN KEY ("client_id") REFERENCES "clients" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

-- CORRIGIDO: referenciava a coluna "muser_id" (typo), que não existe.
ALTER TABLE "managers" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "managers" ADD FOREIGN KEY ("created_by") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "messages" ADD FOREIGN KEY ("session_id") REFERENCES "sessions" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "transcript_chunks" ADD FOREIGN KEY ("transcript_id") REFERENCES "transcripts" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "meeting_performance_analyses" ADD FOREIGN KEY ("meeting_id") REFERENCES "meetings" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "meeting_strategic_analyses" ADD FOREIGN KEY ("meeting_id") REFERENCES "meetings" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "meeting_strategic_analyses" ADD FOREIGN KEY ("client_id") REFERENCES "clients" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "meeting_strategic_scores" ADD FOREIGN KEY ("meeting_id") REFERENCES "meetings" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "meeting_strategic_scores" ADD FOREIGN KEY ("client_id") REFERENCES "clients" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "meeting_performance_scores" ADD FOREIGN KEY ("meeting_id") REFERENCES "meetings" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "meeting_performance_scores" ADD FOREIGN KEY ("salesperson_id") REFERENCES "salespersons" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "audits" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION DEFERRABLE INITIALLY IMMEDIATE;