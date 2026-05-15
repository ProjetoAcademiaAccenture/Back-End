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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceNegativeTests {

    @Mock
    private ExtratoRepository extratoRepository;

    @InjectMocks
    private ExtratoService extratoService;

    private Conta conta;
    private Extrato extrato1;
    private Extrato extrato2;

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
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando listar extratos com contaId inválido")
    void deveRetornarListaVaziaComContaIdInvalido() {
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(999L)).thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(999L);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(extratoRepository, times(1)).findByContaIdOrderByDataHoraDesc(999L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando período não contém transações")
    void deveRetornarListaVaziaQuandoPeriodoNaoTemTransacoes() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now().minusDays(25);

        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodo(1L, inicio, fim);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(extratoRepository, times(1))
                .findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando filtrar por tipo sem resultados")
    void deveRetornarListaVaziaQuandoTipoNaoEncontradoEmResultados() {
        when(extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.ESTORNO))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorTipo(1L, TipoExtrato.ESTORNO);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(extratoRepository, times(1))
                .findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.ESTORNO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando filtrar CREDITO sem resultados")
    void deveRetornarListaVaziaQuandoNaoHaCreditos() {
        when(extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.CREDITO))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorTipo(1L, TipoExtrato.CREDITO);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();

        verify(extratoRepository, times(1))
                .findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.CREDITO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando combinar período e tipo sem resultados")
    void deveRetornarListaVaziaQuandoCombinarPeriodoETipoSemResultados() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodoETipo(1L, inicio, fim, TipoExtrato.ESTORNO);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve filtrar corretamente por tipo ao combinar período e tipo")
    void deveVerificarTipoAoFiltrarPorPeriodoETipo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        List<Extrato> extratos = List.of(extrato1, extrato2); // Um de cada tipo
        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodoETipo(1L, inicio, fim, TipoExtrato.DEBITO);

        // Deve retornar apenas os débitos
        assertThat(resultado)
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getTipo)
                .containsOnly("DEBITO");
    }

    @Test
    @DisplayName("Deve não incluir valores null na lista de resultados")
    void deveNaoIncluirNullNaListaDeResultados() {
        List<Extrato> extratos = List.of(extrato1, extrato2);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .isNotNull()
                .doesNotContainNull()
                .allMatch(dto -> dto.getId() != null);
    }

    @Test
    @DisplayName("Deve lidar com contaId = 0 (inválido)")
    void deveHandlearComContaIdZero() {
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(0L)).thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(0L);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve lidar com contaId negativo (inválido)")
    void deveHandlearComContaIdNegativo() {
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(-1L)).thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(-1L);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve lidar com período invertido (fim anterior ao início)")
    void deveHandlearComPeriodoInvertido() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = LocalDateTime.now().minusDays(10);

        when(extratoRepository.findByContaIdAndDataHoraBetweenOrderByDataHoraDesc(1L, inicio, fim))
                .thenReturn(List.of());

        List<ExtratoResponseDTO> resultado = extratoService.listarPorPeriodo(1L, inicio, fim);

        assertThat(resultado)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista imutável (ou lista que não causa erros ao ser consumida)")
    void deveRetornarListaSegura() {
        List<Extrato> extratos = List.of(extrato1, extrato2);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThatCode(() -> {
            resultado.forEach(dto -> assertThat(dto).isNotNull());
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve retornar diferentes tipos de extratos sem misturar")
    void deveSepararCorretamenteTiposDeExtratos() {
        List<Extrato> debitosOnly = List.of(extrato1);
        when(extratoRepository.findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.DEBITO))
                .thenReturn(debitosOnly);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorTipo(1L, TipoExtrato.DEBITO);

        assertThat(resultado)
                .isNotEmpty()
                .allMatch(dto -> "DEBITO".equals(dto.getTipo()));

        verify(extratoRepository, never())
                .findByContaIdAndTipoOrderByDataHoraDesc(1L, TipoExtrato.CREDITO);
    }

    @Test
    @DisplayName("Deve preservar informações de saldo antes e depois")
    void devePreservarInformacoesDeSaldo() {
        List<Extrato> extratos = List.of(extrato1);
        when(extratoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(extratos);

        List<ExtratoResponseDTO> resultado = extratoService.listarPorConta(1L);

        assertThat(resultado)
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getSaldoAntes)
                .containsExactly(new BigDecimal("5000.00"));

        assertThat(resultado)
                .hasSize(1)
                .extracting(ExtratoResponseDTO::getSaldoDepois)
                .containsExactly(new BigDecimal("4850.00"));
    }

}
