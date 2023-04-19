package application.vendas.rest.controller;

import application.vendas.domain.entity.Produto;
import application.vendas.domain.repository.Produtos;
import application.vendas.exception.RegraNegocioException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private Produtos produtos;

    public ProdutoController(Produtos produtos) {
        this.produtos = produtos;
    };

    @PostMapping
    @ResponseStatus(CREATED)
    public Produto save ( @Valid @RequestBody Produto produto){
        return produtos.save(produto);
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void update(@PathVariable Integer id,
                       @RequestBody @Valid Produto produto){
        produtos
                .findById(id)
                .map( produtoExistente ->{
                    produto.setId(produtoExistente.getId());
                    produtos.save(produto);
                    return produto;
                })
                .orElseThrow( () ->
                        new RegraNegocioException("Produto não encontrado"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id){
        produtos
                .findById(id)
                .map( produto -> {
                    produtos.delete(produto);
                    return Void.TYPE;
                })
                .orElseThrow(() ->
                        new RegraNegocioException("Produto não encontrado" ));
    }

    @GetMapping("{id}")
    public Produto getProdutoById(@PathVariable Integer id ){
        return produtos.findById(id)
                .orElseThrow(()->
                        new RegraNegocioException("Produto não encontrado"));

    }

    @GetMapping
    public List<Produto> find(Produto filtro){
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example example = Example.of(filtro, matcher);
        return produtos.findAll(example);

    }


}
