package com.tp.album.controller;

import com.tp.album.model.dto.CrearUsuarioDTO;
import com.tp.album.model.dto.UsuarioResponseDTO;
import com.tp.album.model.entities.Usuario;
import com.tp.album.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody CrearUsuarioDTO crearUsuarioDTO) {
        try {
            Usuario creado = usuarioService.crearUsuario(crearUsuarioDTO);
            UsuarioResponseDTO body = new UsuarioResponseDTO(
                creado.getId(),
                creado.getUsername(),
                creado.getRole() != null ? creado.getRole().name() : null
            );
            return ResponseEntity.created(URI.create("/usuarios/" + creado.getId())).body(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioPorId(@PathVariable Long id) {
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
