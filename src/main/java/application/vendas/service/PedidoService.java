package application.vendas.service;

import application.vendas.domain.entity.Pedido;
import application.vendas.rest.dto.PedidoDTO;

public interface PedidoService {

    Pedido salvar (PedidoDTO dto);
}
