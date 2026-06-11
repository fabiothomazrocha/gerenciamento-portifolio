-- Criação da tabela de membros (cache local do serviço externo)
CREATE TABLE membros (
    id          BIGSERIAL PRIMARY KEY,
    id_externo  VARCHAR(50) UNIQUE NOT NULL,
    nome        VARCHAR(255) NOT NULL,
    atribuicao  VARCHAR(100) NOT NULL,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Criação da tabela de projetos
CREATE TABLE projetos (
    id                  BIGSERIAL PRIMARY KEY,
    nome                VARCHAR(255) NOT NULL,
    data_inicio         DATE NOT NULL,
    previsao_termino    DATE NOT NULL,
    data_real_termino   DATE,
    orcamento_total     NUMERIC(15,2) NOT NULL,
    descricao           TEXT,
    status              VARCHAR(50) NOT NULL DEFAULT 'EM_ANALISE',
    gerente_id          BIGINT REFERENCES membros(id),
    criado_em           TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabela de associação projeto <-> membro
CREATE TABLE projeto_membros (
    projeto_id  BIGINT NOT NULL REFERENCES projetos(id),
    membro_id   BIGINT NOT NULL REFERENCES membros(id),
    PRIMARY KEY (projeto_id, membro_id)
);

-- Índices
CREATE INDEX idx_projetos_status ON projetos(status);
CREATE INDEX idx_projetos_gerente ON projetos(gerente_id);
CREATE INDEX idx_projeto_membros_membro ON projeto_membros(membro_id);
