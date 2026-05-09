package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.ItemPedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.request.PedidoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PedidoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.CancelamentoException;
import acc.br.projetoFinal.Accenture.exception.EstoqueInsuficienteException;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.ItemPedido;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.Produto;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ItemPedidoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ContaService contaService;
    private final EntityManager entityManager;

    public static final BigDecimal PERCENTUAL_MULTA = new BigDecimal("0.10");

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .status(StatusPedido.CRIADO)
                .valorTotal(BigDecimal.ZERO)
                .multaCancelamento(BigDecimal.ZERO)
                .build();
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemPedidoRequestDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

            if (produto.getQuantidadeEstoque() < itemDto.getQuantidade())
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedidoSalvo)
                    .produto(produto)
                    .quantidade(itemDto.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build();
            itemPedidoRepository.save(item);

            valorTotal = valorTotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));
        }

        pedidoSalvo.setValorTotal(valorTotal);
        return PedidoResponseDTO.fromEntity(pedidoRepository.save(pedidoSalvo));
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
        return PedidoResponseDTO.fromEntity(pedido);
    }

    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
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
    public PedidoResponseDTO reservarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.CRIADO)
            throw new IllegalArgumentException("Pedido deve estar em status CRIADO para ser reservado");

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            entityManager.flush();
            entityManager.refresh(produto);
            if (produto.getQuantidadeEstoque() < item.getQuantidade())
                throw new EstoqueInsuficienteException("Estoque insuficiente para: " + produto.getNome());
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.RESERVADO);
        return PedidoResponseDTO.fromEntity(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO pagarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.RESERVADO)
            throw new IllegalArgumentException("Pedido deve estar RESERVADO para ser pago");

        contaService.transferir(pedido.getCliente().getId(), pedido.getValorTotal(), pedido);
        pedido.setStatus(StatusPedido.PAGO);
        return PedidoResponseDTO.fromEntity(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.CANCELADO)
            throw new CancelamentoException("Pedido já está cancelado");

        if (pedido.getStatus() == StatusPedido.RESERVADO || pedido.getStatus() == StatusPedido.PAGO) {
            pedido.getItens().forEach(item -> {
                Produto p = item.getProduto();
                p.setQuantidadeEstoque(p.getQuantidadeEstoque() + item.getQuantidade());
                produtoRepository.save(p);
            });
        }

        if (pedido.getStatus() == StatusPedido.PAGO) {
            BigDecimal multa = pedido.getValorTotal().multiply(PERCENTUAL_MULTA)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal estorno = pedido.getValorTotal().subtract(multa);
            pedido.setMultaCancelamento(multa);
            contaService.estornarComMulta(pedido.getCliente().getId(), estorno, multa, pedido);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return PedidoResponseDTO.fromEntity(pedidoRepository.save(pedido));
    }
}