package application.vendas.rest.controller;


import application.vendas.domain.entity.ItemPedido;
import application.vendas.domain.entity.Pedido;
import application.vendas.domain.enums.StatusPedido;
import application.vendas.exception.RegraNegocioException;
import application.vendas.rest.dto.AtualizacaoStatusPedidoDTO;
import application.vendas.rest.dto.InformacoesItemPedidoDTO;
import application.vendas.rest.dto.InformacoesPedidoDTO;
import application.vendas.rest.dto.PedidoDTO;
import application.vendas.service.PedidoService;
import io.swagger.annotations.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/api/pedidos")
@Api("Api Pedidos")
public class PedidoController {

    private PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Salvar um pedido")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Pedido salvo com sucesso."),
            @ApiResponse(code = 404, message = "Erro ao salvar um Pedido.")
    })
    public Integer save (@RequestBody @Valid PedidoDTO dto){
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um pedido")
    @ApiResponses({
            @ApiResponse(code = 302, message = "Pedido encontrado."),
            @ApiResponse(code = 400, message = "Pedido não encontrado para o ID informando.")
    })
    public InformacoesPedidoDTO getById( @ApiParam("ID do pedido") @PathVariable Integer id){
        return service
                .obterPedidoCompleto(id)
                .map(p -> converter(p) )
                .orElseThrow(() ->
                        new RegraNegocioException("Pedido não encontrado"));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Alterar dados de um pedido")
    @ApiResponses({
            @ApiResponse(code = 200, message = " Pedido alterado com sucesso."),
            @ApiResponse(code = 204, message = "Erro ao alterar um pedido.")
    })
    public void updateStatus( @ApiParam("ID do pedido")@PathVariable Integer id,@RequestBody @Valid AtualizacaoStatusPedidoDTO dto){
        String novoStatus = dto.getNovoStatus();
        service.atualizaStatus(id, StatusPedido.valueOf(novoStatus));

    }
    private InformacoesPedidoDTO converter (Pedido pedido){
         return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                 .status(pedido.getStatus().name())
                .itemInfo(converter(pedido.getItens()))
                .build();
    }
    private List<InformacoesItemPedidoDTO> converter(List<ItemPedido> itens){
        if (CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }
        return itens.stream().map(item -> InformacoesItemPedidoDTO.builder().descricaoProduto(item.getProduto().getDescricao())
                .precoUnitario(item.getProduto().getPreco())
                .quantidade(item.getQuantidade())
                .build()
        ).collect(Collectors.toList());
    }
}
