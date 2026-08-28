package br.university.tcc.service;

import br.university.tcc.dto.LoginRequest;
import br.university.tcc.dto.LoginResponse;
import br.university.tcc.entity.Perfil;
import br.university.tcc.entity.Usuario;
import br.university.tcc.entity.UsuarioPerfil;
import br.university.tcc.repository.UsuarioRepository;
import br.university.tcc.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    private UsuarioRepository usuarioRepository;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setup() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        when(jwtUtil.generateToken(anyMap(), anyString())).thenReturn("fake-token");
        authService = new AuthService(usuarioRepository, jwtUtil);
    }

    @Test
    void loginSuccess() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Usuário");
        u.setEmail("user@example.com");
        u.setSenha(encoder.encode("123456"));
        u.setAtivo(true);

        Perfil p = new Perfil("ADMINISTRADOR");
        UsuarioPerfil up = new UsuarioPerfil();
        up.setUsuario(u);
        up.setPerfil(p);
        u.getUsuarioPerfis().add(up);

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setSenha("123456");

        LoginResponse resp = authService.login(req);
        assertNotNull(resp);
        assertEquals("fake-token", resp.getToken());
        assertNotNull(resp.getUsuario());
        assertEquals(u.getEmail(), resp.getUsuario().getEmail());
    }

    @Test
    void loginInvalidPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Usuário");
        u.setEmail("user2@example.com");
        u.setSenha(encoder.encode("correct"));
        u.setAtivo(true);

        when(usuarioRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setEmail("user2@example.com");
        req.setSenha("wrong");

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void loginUserNotFound() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest();
        req.setEmail("noone@example.com");
        req.setSenha("x");
        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }

    @Test
    void loginInactiveUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Usuario u = new Usuario();
        u.setId(2L);
        u.setNome("Inactive");
        u.setEmail("inactive@example.com");
        u.setSenha(encoder.encode("123"));
        u.setAtivo(false);

        when(usuarioRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setEmail("inactive@example.com");
        req.setSenha("123");

        assertThrows(IllegalArgumentException.class, () -> authService.login(req));
    }
}
