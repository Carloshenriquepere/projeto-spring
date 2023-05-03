package application.vendas.rest.controller;


import application.vendas.domain.entity.Usuario;
import application.vendas.exception.SenhaInvalidaException;
import application.vendas.rest.dto.CredenciaisDTO;
import application.vendas.rest.dto.TokenDTO;
import application.vendas.security.jwt.JwtService;
import application.vendas.service.impl.UsuarioServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Api("Api Usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salvar um usuario")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuário salvo com sucesso."),
            @ApiResponse(code = 404, message = "Erro ao salvar um Usuário.")
    })
    public Usuario salvar( @Valid @RequestBody Usuario usuario){
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioService.salvar(usuario);
    }

    @PostMapping("/auth")
    @ApiOperation("Autenticar um usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário autenticado com sucesso."),
            @ApiResponse(code = 404, message = "Erro ao autenticar um Usuário.")
    })
    public TokenDTO autenticar(@RequestBody CredenciaisDTO credenciais){
        try {
            Usuario usuario = Usuario.builder()
                    .login(credenciais.getLogin())
                    .senha(credenciais.getSenha())
                    .build();
            UserDetails usuarioAutenticado = usuarioService.autenticar(usuario);
            String token = jwtService.gerarToken(usuario);
            return new TokenDTO(usuario.getLogin(), token);

        }catch (UsernameNotFoundException  |  SenhaInvalidaException e  ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
