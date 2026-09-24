ALTER TABLE formulario_pergunta DROP CHECK ck_formulario_pergunta_ordem;
ALTER TABLE formulario_pergunta ADD CONSTRAINT ck_formulario_pergunta_ordem CHECK (ordem BETWEEN 1 AND 15);

INSERT INTO perfil (nome, descricao)
SELECT 'PROFESSOR', 'Professor que pode ser habilitado como avaliador'
WHERE NOT EXISTS (SELECT 1 FROM perfil WHERE nome = 'PROFESSOR');

INSERT INTO formulario (titulo, descricao)
SELECT 'Avaliação de Projeto Integrador', 'Formulário padrão para avaliação de projetos integradores.'
WHERE NOT EXISTS (SELECT 1 FROM formulario WHERE titulo = 'Avaliação de Projeto Integrador');

INSERT INTO pergunta (titulo, descricao)
SELECT 'O problema ou necessidade que o projeto pretende solucionar está claramente identificado?', 'Avalie a clareza na identificação do problema ou necessidade.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O problema ou necessidade que o projeto pretende solucionar está claramente identificado?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'Os objetivos do projeto estão claros, bem definidos e são coerentes com a proposta?', 'Avalie a clareza, definição e coerência dos objetivos.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'Os objetivos do projeto estão claros, bem definidos e são coerentes com a proposta?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto apresenta uma solução adequada para o problema identificado?', 'Avalie a adequação da solução proposta.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto apresenta uma solução adequada para o problema identificado?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'A proposta apresenta relevância e pode gerar benefícios para o público-alvo ou para a comunidade?', 'Avalie a relevância e os benefícios esperados.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'A proposta apresenta relevância e pode gerar benefícios para o público-alvo ou para a comunidade?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto apresenta algum diferencial, inovação ou abordagem criativa?', 'Avalie o diferencial, a inovação e a criatividade.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto apresenta algum diferencial, inovação ou abordagem criativa?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'A metodologia utilizada para desenvolver o projeto está bem estruturada e justificada?', 'Avalie a estrutura e a justificativa da metodologia.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'A metodologia utilizada para desenvolver o projeto está bem estruturada e justificada?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto demonstra conhecimento e domínio sobre o tema abordado?', 'Avalie o conhecimento e domínio demonstrados.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto demonstra conhecimento e domínio sobre o tema abordado?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'A solução proposta é viável considerando tempo, recursos, materiais, tecnologia e/ou custos?', 'Avalie a viabilidade da solução.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'A solução proposta é viável considerando tempo, recursos, materiais, tecnologia e/ou custos?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto apresenta resultados ou evidências que demonstram que a proposta pode funcionar?', 'Avalie os resultados e evidências apresentados.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto apresenta resultados ou evidências que demonstram que a proposta pode funcionar?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto atende às necessidades e características do público-alvo?', 'Avalie o atendimento às necessidades do público-alvo.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto atende às necessidades e características do público-alvo?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'A equipe demonstrou organização, planejamento e divisão adequada das atividades?', 'Avalie organização, planejamento e divisão das atividades.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'A equipe demonstrou organização, planejamento e divisão adequada das atividades?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'Os integrantes demonstram domínio do projeto durante a apresentação e conseguem explicar suas decisões?', 'Avalie o domínio do projeto e a justificativa das decisões.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'Os integrantes demonstram domínio do projeto durante a apresentação e conseguem explicar suas decisões?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'A apresentação do projeto é clara, objetiva e bem organizada?', 'Avalie clareza, objetividade e organização da apresentação.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'A apresentação do projeto é clara, objetiva e bem organizada?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'O projeto possui potencial para ser aplicado ou desenvolvido além da apresentação acadêmica?', 'Avalie o potencial de continuidade e aplicação do projeto.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'O projeto possui potencial para ser aplicado ou desenvolvido além da apresentação acadêmica?');
INSERT INTO pergunta (titulo, descricao)
SELECT 'De maneira geral, o projeto alcança os objetivos propostos e apresenta qualidade suficiente para ser considerado satisfatório?', 'Avalie a qualidade geral e o alcance dos objetivos.'
WHERE NOT EXISTS (SELECT 1 FROM pergunta WHERE titulo = 'De maneira geral, o projeto alcança os objetivos propostos e apresenta qualidade suficiente para ser considerado satisfatório?');

INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 1 FROM formulario f JOIN pergunta p ON p.titulo = 'O problema ou necessidade que o projeto pretende solucionar está claramente identificado?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 1);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 2 FROM formulario f JOIN pergunta p ON p.titulo = 'Os objetivos do projeto estão claros, bem definidos e são coerentes com a proposta?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 2);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 3 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto apresenta uma solução adequada para o problema identificado?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 3);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 4 FROM formulario f JOIN pergunta p ON p.titulo = 'A proposta apresenta relevância e pode gerar benefícios para o público-alvo ou para a comunidade?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 4);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 5 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto apresenta algum diferencial, inovação ou abordagem criativa?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 5);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 6 FROM formulario f JOIN pergunta p ON p.titulo = 'A metodologia utilizada para desenvolver o projeto está bem estruturada e justificada?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 6);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 7 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto demonstra conhecimento e domínio sobre o tema abordado?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 7);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 8 FROM formulario f JOIN pergunta p ON p.titulo = 'A solução proposta é viável considerando tempo, recursos, materiais, tecnologia e/ou custos?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 8);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 9 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto apresenta resultados ou evidências que demonstram que a proposta pode funcionar?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 9);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 10 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto atende às necessidades e características do público-alvo?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 10);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 11 FROM formulario f JOIN pergunta p ON p.titulo = 'A equipe demonstrou organização, planejamento e divisão adequada das atividades?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 11);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 12 FROM formulario f JOIN pergunta p ON p.titulo = 'Os integrantes demonstram domínio do projeto durante a apresentação e conseguem explicar suas decisões?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 12);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 13 FROM formulario f JOIN pergunta p ON p.titulo = 'A apresentação do projeto é clara, objetiva e bem organizada?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 13);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 14 FROM formulario f JOIN pergunta p ON p.titulo = 'O projeto possui potencial para ser aplicado ou desenvolvido além da apresentação acadêmica?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 14);
INSERT INTO formulario_pergunta (formulario_id, pergunta_id, ordem)
SELECT f.id, p.id, 15 FROM formulario f JOIN pergunta p ON p.titulo = 'De maneira geral, o projeto alcança os objetivos propostos e apresenta qualidade suficiente para ser considerado satisfatório?'
WHERE f.titulo = 'Avaliação de Projeto Integrador' AND NOT EXISTS (SELECT 1 FROM formulario_pergunta fp WHERE fp.formulario_id = f.id AND fp.ordem = 15);
