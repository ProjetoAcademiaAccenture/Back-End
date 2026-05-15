package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.ExtratoResponseDTO;
import acc.br.projetoFinal.Accenture.enums.TipoExtrato;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.model.Extrato;
import acc.br.projetoFinal.Accenture.repository.ExtratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceTests {

    @Mock
    private ExtratoRepository extratoRepository;

    @InjectMocks
    private ExtratoService extratoService;

    private Conta conta;
    private Extrato extrato1;
    private Extrato extrato2;
    private Extrato extrato3;

    @BeforeEach
    void setUp() {
        conta = Conta.builder()
                .id(1L)
                .saldo(new BigDecimal("5000.00"))
                .build();

        LocalDateTime agora = LocalDateTime.now();

        extrato1 = Extrato.builder()
                .id(1L)
                .conta(conta)
                .tipo(TipoExtrato.DEBITO)
                .valor(new BigDecimal("150.00"))
                .saldoAntes(new BigDecimal("5000.00"))
                .saldoDepois(new BigDecimal("4850.00"))
                .descricao("Pagamento do pedido #123")
                .dataHora(agora.minusDays(5))
                .build();

        extrato2 = Extrato.builder()
                .id(2L)
                .conta(conta)
                .tipo(TipoExtrato.CREDITO)
                .valor(new BigDecimal("200.00"))
                .saldoAntes(new BigDecimal("4850.00"))
                .saldoDepois(new BigDecimal("5050.00"))
                .descricao("Depósito")
                .dataHora(agora.minusDays(3))
                .build();

        extrato3 = Extrato.builder()
                .id(3L)
                .conta(conta)
                .tipo(TipoExtrato.DEBITO)
                .valor(new BigDecimal("100.00"))
                .saldoAntes(new BigDecimal("5050.00"))
                .saldoDepois(new BigDecimal("4950.00"))
                .descricao("Pagamento do pedido #124")
                .dataHora(agora.minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os extratos de uma conta ordenados por data decrescente")
    void deveListarTodosOsExtratosPorConta() {
        List<Extrato> extratos = List.of(extrato3, extrato2, extrato1);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .isNotNull()
                .hasSize(3)
                .extracting(ExtratoResponseDTO::getId)
                .containsExactly(3L, 2L, 1L);

        verify(extratoRepository, times(1)).findByContaIdOrderByDataHoraDesc(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando conta não possui extratos")
    void deveRetornarListaVaziaQuandoNaoHaExtratos() {
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(extratoRepository, times(1)).findByContaIdOrderByDataHoraDesc(1L);
    }

    @Test
    @DisplayName("Deve listar extratos por período específico")
    void deveListarExtratosPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(4);
        LocalDateTime fim = LocalDateTime.now().minusDays(2);

        List<Extrato> extratos = List.of(extrato2);
        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodo(1L, inicio, fim);

        assertThat(resultado)
                .isNotNull()
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getId)
                .containsExactly(2L);

        verify(extratoRepository, times(1))
                .findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim);
    }

    @Test
    @DisplayName("Deve listar extratos por tipo (débito)")
    void deveListarExtratosPorTipoDebito() {
        List<Extrato> extratos = List.of(extrato3, extrato1);
        when(extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.DEBITO))
                .thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorTipo(1L, TipoExtrato.DEBITO);

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(ExtratoResponseDTO::getTipo)
                .containsOnly("DEBITO");

        verify(extratoRepository, times(1))
                .findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.DEBITO);
    }

    @Test
    @DisplayName("Deve listar extratos por tipo (crédito)")
    void deveListarExtratosPorTipoCredito() {
        List<Extrato> extratos = List.of(extrato2);
        when(extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.CREDITO))
                .thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorTipo(1L, TipoExtrato.CREDITO);

        assertThat(resultado)
                .isNotNull()
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getTipo)
                .containsOnly("CREDITO");

        verify(extratoRepository, times(1))
                .findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.CREDITO);
    }

    @Test
    @DisplayName("Deve listar extratos por período e tipo")
    void deveListarExtratosPorPeriodoETipo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(4);
        LocalDateTime fim = LocalDateTime.now();

        List<Extrato> extratos = List.of(extrato2);
        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodoETipo(1L, inicio, fim, TipoExtrato.CREDITO);

        assertThat(resultado)
                .isNotNull()
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getTipo)
                .containsOnly("CREDITO");

        verify(extratoRepository, times(1))
                .findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando filtrar por período e tipo sem resultados")
    void deveRetornarListaVaziaQuandoPeriodoETipoNaoTemdados() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now().minusDays(20);

        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodoETipo(1L, inicio, fim, TipoExtrato.DEBITO);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve mapear corretamente Extrato para ExtratoResponseDTO")
    void deveMapearExtratoParaDTO() {
        List<Extrato> extratos = List.of(extrato1);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .hasSize(1);

        ExtratoResponseDTO dto = resultado.get(0);
        assertThat(dto)
                .extracting("id", "tipo", "valor", "saldoAntes", "saldoDepois", "descricao")
                .containsExactly(1L, "DEBITO", new BigDecimal("150.00"), new BigDecimal("5000.00"),
                        new BigDecimal("4850.00"), "Pagamento do pedido #123");
    }

    @Test
    @DisplayName("Deve manter ordenação decrescente por data em listarPorConta")
    void deveManterodenacaoDecrescentePorData() {
        List<Extrato> extratos = List.of(extrato3, extrato2, extrato1);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .extracting(ExtratoResponseDTO::getDataHora)
                .isSortedAccordingTo((d1, d2) -> d2.compareTo(d1)); // Ordem decrescente
    }

    @Test
    @DisplayName("Deve chamar repository com parâmetros corretos ao listar por período")
    void devePassarParametrosCorretosAoRepository() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(List.of());

        extratoService.listarPorPeriodo(1L, inicio, fim);

        verify(extratoRepository, times(1))
                .findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(eq(1L), eq(inicio), eq(fim));
    }

}
