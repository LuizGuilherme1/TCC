package br.university.tcc.service;

import br.university.tcc.dto.LoginRequest;
import br.university.tcc.dto.LoginResponse;
import br.university.tcc.dto.UsuarioResponse;
import br.university.tcc.entity.Usuario;
import br.university.tcc.entity.UsuarioPerfil;
import br.university.tcc.repository.UsuarioRepository;
import br.university.tcc.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos"));

        if (!user.getAtivo()) throw new IllegalArgumentException("Usuário inativo");

        if (!passwordEncoder.matches(request.getSenha(), user.getSenha())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        List<String> perfis = user.getUsuarioPerfis().stream()
            .map(UsuarioPerfil::getPerfil)
            .map(p -> p.getNome())
            .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("nome", user.getNome());
        claims.put("email", user.getEmail());
        claims.put("perfis", perfis);

        String token = jwtUtil.generateToken(claims, user.getEmail());

        UsuarioResponse ur = new UsuarioResponse();
        ur.setId(user.getId());
        ur.setNome(user.getNome());
        ur.setEmail(user.getEmail());
        ur.setPerfis(perfis);

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUsuario(ur);
        return resp;
    }
}
