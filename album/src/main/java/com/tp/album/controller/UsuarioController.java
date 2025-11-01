package com.tp.album.controller;

import com.tp.album.model.dto.CrearUsuarioDTO;
import com.tp.album.model.dto.UsuarioResponseDTO;
import com.tp.album.model.dto.ActualizarUsuarioDTO;
import com.tp.album.model.entities.Usuario;
import com.tp.album.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
            UsuarioResponseDTO body = new UsuarioResponseDTO(creado);
            return ResponseEntity.created(URI.create("/usuarios/" + body.getId())).body(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@RequestBody ActualizarUsuarioDTO dto) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(dto);
            return ResponseEntity.ok(new UsuarioResponseDTO(actualizado));
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioPorId(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuarioPorId(id);
            return ResponseEntity.noContent().build();
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
