package com.tp.album.controller;

import com.tp.album.model.dto.UsuarioRequestDTO;
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
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioRequestDTO crearUsuarioDTO) {
        Usuario creado = usuarioService.crearUsuario(crearUsuarioDTO);
        UsuarioResponseDTO body = new UsuarioResponseDTO(creado);
        return ResponseEntity.created(URI.create("/usuarios/" + body.getId())).body(body);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuarios() {
        List <Usuario> usuarios = usuarioService.obtenerUsuarios();
        List <UsuarioResponseDTO> response = usuarios.stream().map(UsuarioResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioPorId(@PathVariable Long id) {
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
