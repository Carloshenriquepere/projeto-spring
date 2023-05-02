package application.vendas.rest.controller;

import application.vendas.domain.entity.Cliente;
import application.vendas.domain.repository.Clientes;
import application.vendas.exception.RegraNegocioException;
import io.swagger.annotations.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {

    private Clientes clientes;

    public ClienteController(Clientes clientes) {
        this.clientes = clientes;
    }

    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um cliente ")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado."),
            @ApiResponse(code = 400, message = "Cliente não encontrado para o ID informando.")
    })
    public Cliente getClienteById(@PathVariable @ApiParam("Id do cliente") Integer id ){
        return clientes.findById(id)
                .orElseThrow(()->
                        new RegraNegocioException( "Cliente não encontrado"));

    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Salvar um novo cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente salvo com sucesso."),
            @ApiResponse(code = 404, message = "Erro de validação.")
    })
    public Cliente save ( @Valid @RequestBody Cliente cliente){
        return clientes.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id){
        clientes.findById(id)
                .map( cliente -> {
                    clientes.delete(cliente);
                    return cliente;
                })
                .orElseThrow(() ->new RegraNegocioException("Cliente não encontrado" ));
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void update(@PathVariable Integer id,
                                 @RequestBody @Valid Cliente cliente){
        clientes
                .findById(id)
                .map( clienteExistente ->{
                    cliente.setId(clienteExistente.getId());
                    clientes.save(cliente);
                    return clienteExistente;
                } ).orElseThrow(() ->new RegraNegocioException("Cliente não encontrado" ));
    }

    @GetMapping
    public List<Cliente> find(Cliente filtro){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example example = Example.of(filtro, matcher);
        return clientes.findAll(example);

    }
}
