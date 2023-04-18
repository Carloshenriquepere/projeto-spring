package application.vendas.service.impl;


import application.vendas.domain.entity.Cliente;
import application.vendas.domain.entity.ItemPedido;
import application.vendas.domain.entity.Pedido;
import application.vendas.domain.entity.Produto;
import application.vendas.domain.repository.Clientes;
import application.vendas.domain.repository.ItensPedidos;
import application.vendas.domain.repository.Pedidos;
import application.vendas.domain.repository.Produtos;
import application.vendas.exception.RegraNegocioException;
import application.vendas.rest.dto.ItemPedidoDTO;
import application.vendas.rest.dto.PedidoDTO;
import application.vendas.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl  implements PedidoService {

    private final Pedidos repository;
    private final Clientes clientesRepository;
    private final Produtos produtosRepository;
    private final ItensPedidos itensPedidoRepository;


    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
       Cliente cliente = clientesRepository
               .findById(idCliente)
                .orElseThrow( () ->
                        new RegraNegocioException("Código de cliente inválido"));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        List<ItemPedido> itensPedido = converterItens(pedido, dto.getItens());
        repository.save(pedido);
        itensPedidoRepository.saveAll(itensPedido);
        pedido.setItens(itensPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    private List<ItemPedido> converterItens ( Pedido pedido, List<ItemPedidoDTO> itens){
        if(itens.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem itens. ");
        }

        return itens
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(  () ->
                                    new RegraNegocioException("Código de produto Inválido: " + idProduto));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}
