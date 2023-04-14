package application.vendas.rest.controller;

import application.vendas.domain.entity.Cliente;
import application.vendas.domain.entity.Produto;
import application.vendas.domain.repository.Produtos;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private Produtos produtos;

    public ProdutoController(Produtos produtos) {
        this.produtos = produtos;
    }

    String mensg = "Produto não encontrado";

    @GetMapping("{id}")
    public Produto getProdutoById(@PathVariable Integer id ){
        return produtos.findById(id)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, mensg));

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto save (@RequestBody Produto produto){
        return produtos.save(produto);
    }
}
