package br.university.tcc.service;

import br.university.tcc.dto.UsuarioRequest;
import br.university.tcc.entity.Perfil;
import br.university.tcc.entity.Usuario;
import br.university.tcc.entity.UsuarioPerfil;
import br.university.tcc.entity.UsuarioPerfilId;
import br.university.tcc.repository.PerfilRepository;
import br.university.tcc.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    public Usuario createUsuario(UsuarioRequest req, List<String> perfis) {
        if (usuarioRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        Usuario u = new Usuario();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setSenha(passwordEncoder.encode(req.getSenha()));

        for (String pnome : perfis) {
            Perfil p = perfilRepository.findByNome(pnome).orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado: " + pnome));
            UsuarioPerfil up = new UsuarioPerfil();
            up.setId(new UsuarioPerfilId());
            up.setUsuario(u);
            up.setPerfil(p);
            u.getUsuarioPerfis().add(up);
        }

        return usuarioRepository.save(u);
    }

    public Optional<Usuario> findById(Long id) { return usuarioRepository.findById(id); }
    public List<Usuario> listAll() { return usuarioRepository.findAll(); }

        public Usuario promoverParaProfessor(Long id) {
        return adicionarPerfil(id, "PROFESSOR");
        }

        public Usuario promoverParaAvaliador(Long id) {
        return adicionarPerfil(id, "AVALIADOR");
        }

        private Usuario adicionarPerfil(Long id, String nomePerfil) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        boolean jaPossuiPerfil = usuario.getUsuarioPerfis().stream()
            .anyMatch(up -> nomePerfil.equals(up.getPerfil().getNome()));
        if (!jaPossuiPerfil) {
            Perfil perfil = perfilRepository.findByNome(nomePerfil)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado: " + nomePerfil));
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setId(new UsuarioPerfilId());
            usuarioPerfil.setUsuario(usuario);
            usuarioPerfil.setPerfil(perfil);
            usuario.getUsuarioPerfis().add(usuarioPerfil);
        }

        return usuarioRepository.save(usuario);
    }
}
