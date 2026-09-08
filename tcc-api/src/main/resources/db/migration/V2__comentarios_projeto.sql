CREATE TABLE comentario_projeto (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    projeto_id BIGINT UNSIGNED NOT NULL,
    autor_id BIGINT UNSIGNED NOT NULL,
    texto TEXT NOT NULL,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_comentario_projeto PRIMARY KEY (id),
    CONSTRAINT fk_comentario_projeto_projeto FOREIGN KEY (projeto_id) REFERENCES projeto_integrador (id),
    CONSTRAINT fk_comentario_projeto_autor FOREIGN KEY (autor_id) REFERENCES usuario (id),
    INDEX idx_comentario_projeto_projeto_id (projeto_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;