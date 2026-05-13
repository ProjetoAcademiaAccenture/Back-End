package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.request.PagamentoRequestDTO;
import acc.br.projetoFinal.Accenture.dto.response.PagamentoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.MetodoPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusBoleto;
import acc.br.projetoFinal.Accenture.enums.StatusPagamento;
import acc.br.projetoFinal.Accenture.enums.StatusPedido;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.exception.SaldoInsuficienteException;
import acc.br.projetoFinal.Accenture.model.Boleto;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Pagamento;
import acc.br.projetoFinal.Accenture.model.Pedido;
import acc.br.projetoFinal.Accenture.model.TentativaPagamento;
import acc.br.projetoFinal.Accenture.repository.BoletoRepository;
import acc.br.projetoFinal.Accenture.repository.PagamentoRepository;
import acc.br.projetoFinal.Accenture.repository.PedidoRepository;
import acc.br.projetoFinal.Accenture.repository.TentativaPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static acc.br.projetoFinal.Accenture.service.PedidoService.DESCONTO_PIX_BOLETO;

@Service
@RequiredArgsConstructor
public class PagamentoService {
	private static final BigDecimal MULTA_ATRASO_BOLETO = new BigDecimal("0.02");

	private final PagamentoRepository pagamentoRepository;
	private final PedidoRepository pedidoRepository;
	private final TentativaPagamentoRepository tentativaPagamentoRepository;
	private final BoletoRepository boletoRepository;

	private final ContaService contaService;
	private final EstoqueService estoqueService;

	@Transactional
	public PagamentoResponseDTO criarParaPedido(Pedido pedido, String metodoPagamento) {
		MetodoPagamento metodo = MetodoPagamento.valueOf(metodoPagamento.toUpperCase());

		Pagamento pagamento = Pagamento.builder()
				.pedido(pedido)
				.status(StatusPagamento.PENDENTE)
				.metodo(metodo)
				.valorBruto(pedido.getValorBruto())
				.desconto(pedido.getDesconto())
				.valorFinal(pedido.getValorFinal())
				.dataCriacao(LocalDateTime.now())
				.build();

		Pagamento salvo = pagamentoRepository.save(pagamento);
		return PagamentoResponseDTO.fromEntity(salvo);
	}

	@Transactional
	public PagamentoResponseDTO processar(PagamentoRequestDTO dto) {
		Pagamento pagamento = pagamentoRepository.findById(dto.getPagamentoId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado"));

		Pedido pedido = pagamento.getPedido();

		if (pedido.getStatus() != StatusPedido.RESERVADO) {
			throw new IllegalArgumentException(
					"Pedido deve estar RESERVADO para pagamento"
			);
		}

		MetodoPagamento metodo = dto.getMetodoPagamento();

		if (metodo == MetodoPagamento.BOLETO) {
			return gerarBoleto(pagamento, pedido);
		}

		Conta contaCliente = contaService.buscarContaDoCliente(
				pedido.getCliente().getId()
		);

		Conta contaEmpresa = contaService.buscarContaEmpresa();

		contaService.validarSenhaTransacao(
				contaCliente,
				dto.getSenhaTransacao()
		);

		BigDecimal valorBruto = pedido.getValorBruto();
		BigDecimal desconto = pedido.getDesconto();
		BigDecimal valorFinal = pagamento.getValorFinal();

		TentativaPagamento tentativa = TentativaPagamento.builder()
				.pagamento(pagamento)
				.metodo(metodo)
				.status(StatusPagamento.PENDENTE)
				.valorTentado(valorFinal)
				.dataTentativa(LocalDateTime.now())
				.build();

		try {
			String descricao = "Pagamento pedido #" + pedido.getId();

			if (metodo == MetodoPagamento.CREDITO) {
				contaService.debitarLimiteCredito(
						contaCliente,
						valorFinal,
						pedido,
						pagamento,
						descricao
				);
			} else {
				if (contaCliente.getSaldo().compareTo(valorFinal) < 0) {
					throw new SaldoInsuficienteException(
							"Saldo insuficiente"
					);
				}
				contaService.debitarSaldo(
						contaCliente,
						valorFinal,
						pedido,
						pagamento,
						descricao
				);
			}
			contaService.creditarSaldo(
					contaEmpresa,
					valorFinal,
					pedido,
					pagamento,
					descricao
			);

			pagamento.setMetodo(metodo);
			pagamento.setStatus(StatusPagamento.APROVADO);
			pagamento.setValorBruto(valorBruto);
			pagamento.setDesconto(desconto);
			pagamento.setValorFinal(valorFinal);
			pagamento.setDataConclusao(LocalDateTime.now());

			pedido.setDesconto(desconto);
			pedido.setValorFinal(valorFinal);
			pedido.setStatus(StatusPedido.PAGO);

			tentativa.setStatus(StatusPagamento.APROVADO);
			tentativa.setMensagem("Pagamento aprovado");

		} catch (RuntimeException ex) {
			pagamento.setStatus(StatusPagamento.RECUSADO);

			tentativa.setStatus(StatusPagamento.RECUSADO);
			tentativa.setMensagem(ex.getMessage());
		}

		pagamentoRepository.save(pagamento);
		pedidoRepository.save(pedido);
		tentativaPagamentoRepository.save(tentativa);

		return PagamentoResponseDTO.fromEntity(pagamento);
	}

	private PagamentoResponseDTO gerarBoleto(
			Pagamento pagamento,
			Pedido pedido
	) {

		BigDecimal valorBruto = pedido.getValorBruto();

		BigDecimal desconto = calcularDesconto(
				MetodoPagamento.BOLETO,
				valorBruto
		);

		BigDecimal valorFinal = valorBruto
				.subtract(desconto)
				.setScale(2, RoundingMode.HALF_UP);

		pagamento.setMetodo(MetodoPagamento.BOLETO);
		pagamento.setValorBruto(valorBruto);
		pagamento.setDesconto(desconto);
		pagamento.setValorFinal(valorFinal);

		pagamentoRepository.save(pagamento);

		Boleto boleto = Boleto.builder()
				.pagamento(pagamento)
				.codigoBarras(gerarCodigoBarras())
				.valor(valorFinal)
				.dataVencimento(LocalDate.now().plusDays(3))
				.status(StatusBoleto.PENDENTE)
				.build();

		boletoRepository.save(boleto);

		TentativaPagamento tentativa = TentativaPagamento.builder()
				.pagamento(pagamento)
				.metodo(MetodoPagamento.BOLETO)
				.status(StatusPagamento.PENDENTE)
				.valorTentado(valorFinal)
				.mensagem("Boleto gerado com sucesso")
				.dataTentativa(LocalDateTime.now())
				.build();

		tentativaPagamentoRepository.save(tentativa);

		return PagamentoResponseDTO.fromEntity(pagamento);
	}

	@Transactional
	public PagamentoResponseDTO pagarBoleto(Long boletoId, String senhaTransacao) {

		Boleto boleto = boletoRepository.findById(boletoId)
				.orElseThrow(() ->
						new RecursoNaoEncontradoException("Boleto não encontrado")
				);

		if (boleto.getStatus() == StatusBoleto.PAGO) {
			throw new IllegalArgumentException("Boleto já pago");
		}

		if (boleto.getStatus() == StatusBoleto.CANCELADO) {
			throw new IllegalArgumentException("Boleto cancelado");
		}

		Pagamento pagamento = boleto.getPagamento();
		Pedido pedido = pagamento.getPedido();

		Conta contaCliente = contaService.buscarContaDoCliente(
				pedido.getCliente().getId()
		);

		Conta contaEmpresa = contaService.buscarContaEmpresa();

		contaService.validarSenhaTransacao(
				contaCliente,
				senhaTransacao
		);

		BigDecimal valorCobrado = boleto.getValor();

		if (boleto.getDataVencimento().isBefore(LocalDate.now())) {

			BigDecimal multa = valorCobrado
					.multiply(MULTA_ATRASO_BOLETO)
					.setScale(2, RoundingMode.HALF_UP);

			valorCobrado = valorCobrado.add(multa);
		}

		if (contaCliente.getSaldo().compareTo(valorCobrado) < 0) {
			throw new SaldoInsuficienteException(
					"Saldo insuficiente para pagamento do boleto"
			);
		}

		String descricao = "Pagamento boleto pedido #" + pedido.getId();

		contaService.debitarSaldo(
				contaCliente,
				valorCobrado,
				pedido,
				pagamento,
				descricao
		);

		contaService.creditarSaldo(
				contaEmpresa,
				valorCobrado,
				pedido,
				pagamento,
				descricao
		);

		boleto.setStatus(StatusBoleto.PAGO);

		pagamento.setStatus(StatusPagamento.APROVADO);
		pagamento.setDataConclusao(LocalDateTime.now());

		pedido.setStatus(StatusPedido.PAGO);
		pedido.setValorFinal(valorCobrado);

		boletoRepository.save(boleto);
		pagamentoRepository.save(pagamento);
		pedidoRepository.save(pedido);

		TentativaPagamento tentativa = TentativaPagamento.builder()
				.pagamento(pagamento)
				.metodo(MetodoPagamento.BOLETO)
				.status(StatusPagamento.APROVADO)
				.valorTentado(valorCobrado)
				.mensagem("Boleto pago com sucesso")
				.dataTentativa(LocalDateTime.now())
				.build();

		tentativaPagamentoRepository.save(tentativa);

		return PagamentoResponseDTO.fromEntity(pagamento);
	}

	@Transactional
	public void cancelar(Long pagamentoId) {

		Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
				.orElseThrow(() ->
						new RecursoNaoEncontradoException("Pagamento não encontrado")
				);

		Pedido pedido = pagamento.getPedido();

		if (pagamento.getStatus() == StatusPagamento.APROVADO) {

			Conta contaCliente = contaService.buscarContaDoCliente(
					pedido.getCliente().getId()
			);

			Conta contaEmpresa = contaService.buscarContaEmpresa();

			BigDecimal valor = pagamento.getValorFinal();

			String descricao = "Estorno pedido #" + pedido.getId();

			contaService.creditarSaldo(
					contaCliente,
					valor,
					pedido,
					pagamento,
					descricao
			);

			contaService.debitarSaldo(
					contaEmpresa,
					valor,
					pedido,
					pagamento,
					descricao
			);

			pagamento.setStatus(StatusPagamento.ESTORNADO);
		}

		else {
			pagamento.setStatus(StatusPagamento.CANCELADO);
		}

		boletoRepository.findByPagamentoId(pagamento.getId())
				.ifPresent(boleto -> {
					boleto.setStatus(StatusBoleto.CANCELADO);
					boletoRepository.save(boleto);
				});

		pagamento.setDataConclusao(LocalDateTime.now());

		pedido.setStatus(StatusPedido.CANCELADO);

		estoqueService.devolverItens(pedido);

		pagamentoRepository.save(pagamento);
		pedidoRepository.save(pedido);
	}

	public PagamentoResponseDTO buscarPorId(Long id) {

		Pagamento pagamento = pagamentoRepository.findById(id)
				.orElseThrow(() ->
						new RecursoNaoEncontradoException("Pagamento não encontrado")
				);

		return PagamentoResponseDTO.fromEntity(pagamento);
	}

	public PagamentoResponseDTO buscarPorPedidoId(Long pedidoId) {

		Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
				.orElseThrow(() ->
						new RecursoNaoEncontradoException(
								"Pagamento não encontrado para pedido"
						)
				);

		return PagamentoResponseDTO.fromEntity(pagamento);
	}

	private String gerarCodigoBarras() {

		StringBuilder codigo = new StringBuilder();

		for (int i = 0; i < 44; i++) {
			codigo.append((int) (Math.random() * 10));
		}

		return codigo.toString();
	}

	protected BigDecimal calcularDesconto(
			MetodoPagamento metodo,
			BigDecimal valorBruto
	) {
		if (
				metodo == MetodoPagamento.PIX
						|| metodo == MetodoPagamento.BOLETO
		) {
			return valorBruto
					.multiply(DESCONTO_PIX_BOLETO)
					.setScale(2, RoundingMode.HALF_UP);
		}

		return BigDecimal.ZERO;
	}
}