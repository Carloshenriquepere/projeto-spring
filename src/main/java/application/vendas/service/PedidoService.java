package application.vendas.service;

import application.vendas.domain.entity.Pedido;
import application.vendas.domain.enums.StatusPedido;
import application.vendas.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar (PedidoDTO dto);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizaStatus (Integer id, StatusPedido statusPedido);
}
