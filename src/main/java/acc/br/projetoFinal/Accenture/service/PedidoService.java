package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;
    private final PagamentoService pagamentoService;

    public static final BigDecimal DESCONTO_PIX_BOLETO = new BigDecimal("0.05");

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        Pedido pedido = Pedido.builder()
            .cliente(cliente)
            .status(StatusPedido.CRIADO)
            .valorBruto(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .valorFinal(BigDecimal.ZERO)
            .build();

        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal valorBruto = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

            if (produto.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(itemDto.getQuantidade())
                .precoUnitario(produto.getPreco())
                .build();

            itens.add(item);
            valorBruto = valorBruto.add(
                produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()))
            );
        }

        pedido.setItens(itens);
        pedido.setValorBruto(valorBruto);
        MetodoPagamento metodo = MetodoPagamento.valueOf(dto.getMetodoPagamento());
        BigDecimal desconto = pagamentoService.calcularDesconto(
            metodo,
            valorBruto
        );
        pedido.setDesconto(desconto);
        pedido.setValorFinal(valorBruto.subtract(desconto));

        Pedido salvo = pedidoRepository.save(pedido);

        estoqueService.reservarItens(salvo);
        salvo.setStatus(StatusPedido.RESERVADO);

        pagamentoService.criarParaPedido(salvo, dto.getMetodoPagamento());
        salvo = pedidoRepository.save(salvo);

        return PedidoResponseDTO.fromEntity(salvo, metodo);
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
        return PedidoResponseDTO.fromEntity(pedido);
    }

    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) throws AccessDeniedException {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        if (!emailLogado.equals(cliente.getEmail())) {
            throw new AccessDeniedException ("Você não tem permissão para ver pedidos de outro cliente.");
        }
        return pedidoRepository.findByClienteId(clienteId).stream()
            .map(PedidoResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
            .map(PedidoResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new CancelamentoException("Pedido já está cancelado");
        }

        if (pedido.getStatus() == StatusPedido.RESERVADO || pedido.getStatus() == StatusPedido.PAGO) {
            estoqueService.devolverItens(pedido);
        }

        if (pedido.getPagamento() != null) {
            pagamentoService.cancelar(pedido.getPagamento().getId());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido = pedidoRepository.save(pedido);

        return PedidoResponseDTO.fromEntity(pedido);
    }
}