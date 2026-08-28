CREATE TABLE perfil (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_perfil PRIMARY KEY (id),
    CONSTRAINT uk_perfil_nome UNIQUE (nome)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE situacao (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_situacao PRIMARY KEY (id),
    CONSTRAINT uk_situacao_nome UNIQUE (nome)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE status_avaliacao (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_status_avaliacao PRIMARY KEY (id),
    CONSTRAINT uk_status_avaliacao_nome UNIQUE (nome)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE usuario (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    senha VARCHAR(255) NOT NULL COMMENT 'Hash da senha',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE usuario_perfil (
    usuario_id BIGINT UNSIGNED NOT NULL,
    perfil_id BIGINT UNSIGNED NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_usuario_perfil PRIMARY KEY (usuario_id, perfil_id),
    CONSTRAINT fk_usuario_perfil_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_perfil_perfil FOREIGN KEY (perfil_id)
        REFERENCES perfil (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_usuario_perfil_perfil_id (perfil_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE formulario (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_formulario PRIMARY KEY (id),
    CONSTRAINT uk_formulario_titulo UNIQUE (titulo)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE pergunta (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_pergunta PRIMARY KEY (id),
    CONSTRAINT uk_pergunta_titulo UNIQUE (titulo)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE projeto_integrador (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    situacao_id BIGINT UNSIGNED NOT NULL DEFAULT 1,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT NULL,
    ano SMALLINT UNSIGNED NOT NULL,
    semestre TINYINT UNSIGNED NOT NULL,
    data_avaliacao DATE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_projeto_integrador PRIMARY KEY (id),
    CONSTRAINT uk_projeto_integrador_titulo_ano_semestre UNIQUE (titulo, ano, semestre),
    CONSTRAINT ck_projeto_integrador_ano CHECK (ano BETWEEN 1900 AND 9999),
    CONSTRAINT ck_projeto_integrador_semestre CHECK (semestre IN (1, 2)),
    CONSTRAINT fk_projeto_integrador_situacao FOREIGN KEY (situacao_id)
        REFERENCES situacao (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_projeto_integrador_situacao_id (situacao_id),
    INDEX idx_projeto_integrador_ano (ano),
    INDEX idx_projeto_integrador_semestre (semestre),
    INDEX idx_projeto_integrador_data_avaliacao (data_avaliacao),
    INDEX idx_projeto_integrador_ano_semestre (ano, semestre)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE projeto_aluno (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    projeto_integrador_id BIGINT UNSIGNED NOT NULL,
    usuario_aluno_id BIGINT UNSIGNED NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_projeto_aluno PRIMARY KEY (id),
    CONSTRAINT uk_projeto_aluno_projeto_usuario UNIQUE (projeto_integrador_id, usuario_aluno_id),
    CONSTRAINT fk_projeto_aluno_projeto FOREIGN KEY (projeto_integrador_id)
        REFERENCES projeto_integrador (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_projeto_aluno_usuario FOREIGN KEY (usuario_aluno_id)
        REFERENCES usuario (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_projeto_aluno_usuario_aluno_id (usuario_aluno_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE formulario_pergunta (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    formulario_id BIGINT UNSIGNED NOT NULL,
    pergunta_id BIGINT UNSIGNED NOT NULL,
    ordem TINYINT UNSIGNED NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_formulario_pergunta PRIMARY KEY (id),
    CONSTRAINT uk_formulario_pergunta_formulario_pergunta UNIQUE (formulario_id, pergunta_id),
    CONSTRAINT uk_formulario_pergunta_formulario_ordem UNIQUE (formulario_id, ordem),
    CONSTRAINT ck_formulario_pergunta_ordem CHECK (ordem BETWEEN 1 AND 10),
    CONSTRAINT fk_formulario_pergunta_formulario FOREIGN KEY (formulario_id)
        REFERENCES formulario (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_formulario_pergunta_pergunta FOREIGN KEY (pergunta_id)
        REFERENCES pergunta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_formulario_pergunta_pergunta_id (pergunta_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE avaliacao (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    formulario_id BIGINT UNSIGNED NOT NULL,
    projeto_integrador_id BIGINT UNSIGNED NOT NULL,
    usuario_avaliador_id BIGINT UNSIGNED NOT NULL,
    status_avaliacao_id BIGINT UNSIGNED NOT NULL DEFAULT 1,
    data_hora_inicio DATETIME(6) NULL,
    data_hora_finalizacao DATETIME(6) NULL,
    pontuacao_total SMALLINT UNSIGNED NULL,
    media DECIMAL(3, 2) NULL,
    observacao TEXT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_avaliacao PRIMARY KEY (id),
    CONSTRAINT uk_avaliacao_projeto_avaliador UNIQUE (projeto_integrador_id, usuario_avaliador_id),
    CONSTRAINT ck_avaliacao_pontuacao_total CHECK (
        pontuacao_total IS NULL OR pontuacao_total BETWEEN 10 AND 50
    ),
    CONSTRAINT ck_avaliacao_media CHECK (media IS NULL OR media BETWEEN 1.00 AND 5.00),
    CONSTRAINT ck_avaliacao_datas CHECK (
        data_hora_finalizacao IS NULL
        OR (data_hora_inicio IS NOT NULL AND data_hora_finalizacao >= data_hora_inicio)
    ),
    CONSTRAINT fk_avaliacao_formulario FOREIGN KEY (formulario_id)
        REFERENCES formulario (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_avaliacao_projeto FOREIGN KEY (projeto_integrador_id)
        REFERENCES projeto_integrador (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_avaliacao_usuario_avaliador FOREIGN KEY (usuario_avaliador_id)
        REFERENCES usuario (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_avaliacao_status FOREIGN KEY (status_avaliacao_id)
        REFERENCES status_avaliacao (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_avaliacao_formulario_id (formulario_id),
    INDEX idx_avaliacao_usuario_avaliador_id (usuario_avaliador_id),
    INDEX idx_avaliacao_status_avaliacao_id (status_avaliacao_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE resposta_avaliacao (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    avaliacao_id BIGINT UNSIGNED NOT NULL,
    formulario_pergunta_id BIGINT UNSIGNED NOT NULL,
    pontuacao TINYINT UNSIGNED NOT NULL,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_resposta_avaliacao PRIMARY KEY (id),
    CONSTRAINT uk_resposta_avaliacao_avaliacao_pergunta UNIQUE (avaliacao_id, formulario_pergunta_id),
    CONSTRAINT ck_resposta_avaliacao_pontuacao CHECK (pontuacao BETWEEN 1 AND 5),
    CONSTRAINT fk_resposta_avaliacao_avaliacao FOREIGN KEY (avaliacao_id)
        REFERENCES avaliacao (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_resposta_avaliacao_formulario_pergunta FOREIGN KEY (formulario_pergunta_id)
        REFERENCES formulario_pergunta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_resposta_avaliacao_formulario_pergunta_id (formulario_pergunta_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO perfil (id, nome, descricao)
VALUES
    (1, 'ADMINISTRADOR', 'Administrador do sistema'),
    (2, 'AVALIADOR', 'Avaliador de projetos integradores'),
    (3, 'ALUNO', 'Aluno participante de projeto integrador');

INSERT INTO situacao (id, nome, descricao)
VALUES
    (1, 'CADASTRADO', 'Projeto cadastrado e ainda não iniciado para avaliação'),
    (2, 'EM_AVALIACAO', 'Projeto em processo de avaliação'),
    (3, 'AVALIADO', 'Projeto com avaliação concluída'),
    (4, 'CANCELADO', 'Projeto cancelado');

INSERT INTO status_avaliacao (id, nome, descricao)
VALUES
    (1, 'PENDENTE', 'Avaliação criada e ainda não iniciada'),
    (2, 'EM_PREENCHIMENTO', 'Avaliação em preenchimento'),
    (3, 'FINALIZADA', 'Avaliação finalizada');
