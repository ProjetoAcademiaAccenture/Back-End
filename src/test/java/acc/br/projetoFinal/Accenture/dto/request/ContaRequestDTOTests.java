package acc.br.projetoFinal.Accenture.dto.request;

import acc.br.projetoFinal.Accenture.enums.TipoConta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Cobertura Total - ContaRequestDTO")
class ContaRequestDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // =========================================================
    // HELPER
    // =========================================================

    private ContaRequestDTO dtoPadrao() {
        return ContaRequestDTO.builder()
                .clienteId(1L)
                .senhaTransacao("1234")
                .tipoConta(TipoConta.CORRENTE)
                .build();
    }

    private Set<ConstraintViolation<ContaRequestDTO>> validar(ContaRequestDTO dto) {
        return validator.validate(dto);
    }

    // =========================================================
    // TESTES ESTRUTURAIS: construtores, builder, getters/setters
    // =========================================================

    @Test
    @DisplayName("NoArgsConstructor: deve criar instância vazia sem exceção")
    void noArgsConstructor_DeveCriarInstanciaVazia() {
        assertNotNull(new ContaRequestDTO());
    }

    @Test
    @DisplayName("AllArgsConstructor: deve criar instância com todos os campos")
    void allArgsConstructor_DeveCriarInstanciaCompleta() {
        ContaRequestDTO dto = new ContaRequestDTO(1L, "4321", TipoConta.POUPANCA);

        assertAll("AllArgsConstructor",
                () -> assertEquals(1L,              dto.getClienteId()),
                () -> assertEquals("4321",           dto.getSenhaTransacao()),
                () -> assertEquals(TipoConta.POUPANCA, dto.getTipoConta())
        );
    }

    @Test
    @DisplayName("Builder: deve construir DTO campo a campo")
    void builder_DeveConstruirDTOCampoACampo() {
        ContaRequestDTO dto = ContaRequestDTO.builder()
                .clienteId(5L)
                .senhaTransacao("9999")
                .tipoConta(TipoConta.JURIDICA)
                .build();

        assertAll("Builder",
                () -> assertEquals(5L,               dto.getClienteId()),
                () -> assertEquals("9999",            dto.getSenhaTransacao()),
                () -> assertEquals(TipoConta.JURIDICA, dto.getTipoConta())
        );
    }

    @Test
    @DisplayName("Setters/Getters: deve atribuir e retornar cada campo corretamente")
    void settersGetters_DeveAtribuirERetornarCampos() {
        ContaRequestDTO dto = new ContaRequestDTO();

        dto.setClienteId(10L);
        dto.setSenhaTransacao("0000");
        dto.setTipoConta(TipoConta.CORRENTE);

        assertAll("setters e getters",
                () -> assertEquals(10L,               dto.getClienteId()),
                () -> assertEquals("0000",             dto.getSenhaTransacao()),
                () -> assertEquals(TipoConta.CORRENTE, dto.getTipoConta())
        );
    }

    // =========================================================
    // TESTES: cada valor do enum TipoConta
    // =========================================================

    @Test
    @DisplayName("TipoConta: deve aceitar CORRENTE")
    void tipoConta_Corrente_DeveSerAtribuido() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setTipoConta(TipoConta.CORRENTE);
        assertEquals(TipoConta.CORRENTE, dto.getTipoConta());
        assertTrue(validar(dto).isEmpty());
    }

    @Test
    @DisplayName("TipoConta: deve aceitar POUPANCA")
    void tipoConta_Poupanca_DeveSerAtribuido() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setTipoConta(TipoConta.POUPANCA);
        assertEquals(TipoConta.POUPANCA, dto.getTipoConta());
        assertTrue(validar(dto).isEmpty());
    }

    @Test
    @DisplayName("TipoConta: deve aceitar JURIDICA")
    void tipoConta_Juridica_DeveSerAtribuido() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setTipoConta(TipoConta.JURIDICA);
        assertEquals(TipoConta.JURIDICA, dto.getTipoConta());
        assertTrue(validar(dto).isEmpty());
    }

    // =========================================================
    // TESTES DE VALIDAÇÃO: DTO válido
    // =========================================================

    @Test
    @DisplayName("Validação: DTO completamente válido não deve ter violações")
    void validacao_DTOValido_NaoDeveTemViolacoes() {
        assertTrue(validar(dtoPadrao()).isEmpty());
    }

    // =========================================================
    // TESTES DE VALIDAÇÃO: clienteId — @NotNull
    // =========================================================

    @Test
    @DisplayName("Validação: clienteId null deve gerar violação @NotNull")
    void validacao_ClienteIdNull_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setClienteId(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("clienteId")));
    }

    // =========================================================
    // TESTES DE VALIDAÇÃO: senhaTransacao — @NotBlank + @Size
    // =========================================================

    @Test
    @DisplayName("Validação: senhaTransacao null deve gerar violação @NotBlank")
    void validacao_SenhaNull_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    @DisplayName("Validação: senhaTransacao vazia deve gerar violação @NotBlank")
    void validacao_SenhaVazia_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao("");

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    @DisplayName("Validação: senhaTransacao em branco deve gerar violação @NotBlank")
    void validacao_SenhaEmBranco_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao("    ");

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    @DisplayName("Validação: senhaTransacao com 3 caracteres deve gerar violação @Size(min=4)")
    void validacao_SenhaMenorQueMinimo_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao("123");

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    @DisplayName("Validação: senhaTransacao com 5 caracteres deve gerar violação @Size(max=4)")
    void validacao_SenhaMaiorQueMaximo_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao("12345");

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")));
    }

    @Test
    @DisplayName("Validação: senhaTransacao com exatamente 4 caracteres deve ser válida")
    void validacao_SenhaComExatamente4Chars_DeveSerValida() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao("1234");
        assertTrue(validar(dto).isEmpty());
    }

    @Test
    @DisplayName("Validação: mensagem de erro de senhaTransacao deve ser correta")
    void validacao_MensagemErroSenha_DeveEstarCorreta() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setSenhaTransacao(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("senhaTransacao")
                        && v.getMessage().equals("Senha de trasação é obrigatória")));
    }

    // =========================================================
    // TESTES DE VALIDAÇÃO: tipoConta — @NotNull
    // =========================================================

    @Test
    @DisplayName("Validação: tipoConta null deve gerar violação @NotNull")
    void validacao_TipoContaNull_DeveGerarViolacao() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setTipoConta(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tipoConta")));
    }

    @Test
    @DisplayName("Validação: mensagem de erro de tipoConta deve ser correta")
    void validacao_MensagemErroTipoConta_DeveEstarCorreta() {
        ContaRequestDTO dto = dtoPadrao();
        dto.setTipoConta(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tipoConta")
                        && v.getMessage().equals("Tipo da conta é obrigatório")));
    }

    @Test
    @DisplayName("Validação: múltiplos campos inválidos devem gerar múltiplas violações")
    void validacao_MultiplosCamposInvalidos_DeveGerarMultiplasViolacoes() {
        ContaRequestDTO dto = new ContaRequestDTO();
        // clienteId=null, senhaTransacao=null, tipoConta=null
        Set<ConstraintViolation<ContaRequestDTO>> violations = validar(dto);
        assertTrue(violations.size() >= 3);
    }

    // =========================================================
    // TESTES: equals() — todos os branches do @Data
    // =========================================================

    private ContaRequestDTO criarDTOIdentico() {
        return ContaRequestDTO.builder()
                .clienteId(1L)
                .senhaTransacao("1234")
                .tipoConta(TipoConta.CORRENTE)
                .build();
    }

    @Test
    @DisplayName("equals: mesma instância deve ser igual (reflexividade)")
    void equals_MesmaInstancia_DeveSerIgual() {
        ContaRequestDTO dto = criarDTOIdentico();
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals: dois DTOs com todos os campos iguais devem ser iguais")
    void equals_TodosCamposIguais_DeveSerIgual() {
        assertEquals(criarDTOIdentico(), criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: comparação com null deve retornar false")
    void equals_ComNull_DeveRetornarFalse() {
        assertNotEquals(null, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: comparação com tipo diferente deve retornar false (canEqual)")
    void equals_TipoObjetoDiferente_DeveRetornarFalse() {
        assertNotEquals(criarDTOIdentico(), new Object());
    }

    @Test
    @DisplayName("equals: dois DTOs vazios devem ser iguais")
    void equals_AmbosVazios_DeveSerIgual() {
        assertEquals(new ContaRequestDTO(), new ContaRequestDTO());
    }

    @Test
    @DisplayName("equals: clienteId diferente deve retornar false")
    void equals_ClienteIdDiferente_DeveRetornarFalse() {
        ContaRequestDTO d2 = criarDTOIdentico();
        d2.setClienteId(99L);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: senhaTransacao diferente deve retornar false")
    void equals_SenhaDiferente_DeveRetornarFalse() {
        ContaRequestDTO d2 = criarDTOIdentico();
        d2.setSenhaTransacao("9999");
        assertNotEquals(criarDTOIdentico(), d2);
    }

    @Test
    @DisplayName("equals: tipoConta diferente deve retornar false")
    void equals_TipoContaDiferente_DeveRetornarFalse() {
        ContaRequestDTO d2 = criarDTOIdentico();
        d2.setTipoConta(TipoConta.POUPANCA);
        assertNotEquals(criarDTOIdentico(), d2);
    }

    // --- branches null ---

    @Test
    @DisplayName("equals: this.clienteId==null e other!=null deve retornar false")
    void equals_ThisClienteIdNull_DeveRetornarFalse() {
        ContaRequestDTO d1 = criarDTOIdentico();
        d1.setClienteId(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: ambos clienteId==null deve continuar comparando demais campos")
    void equals_AmbosClienteIdNull_DeveCompararDemaisCampos() {
        ContaRequestDTO d1 = criarDTOIdentico();
        ContaRequestDTO d2 = criarDTOIdentico();
        d1.setClienteId(null);
        d2.setClienteId(null);
        assertEquals(d1, d2);
    }

    @Test
    @DisplayName("equals: this.senhaTransacao==null e other!=null deve retornar false")
    void equals_ThisSenhaNull_DeveRetornarFalse() {
        ContaRequestDTO d1 = criarDTOIdentico();
        d1.setSenhaTransacao(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    @Test
    @DisplayName("equals: this.tipoConta==null e other!=null deve retornar false")
    void equals_ThisTipoContaNull_DeveRetornarFalse() {
        ContaRequestDTO d1 = criarDTOIdentico();
        d1.setTipoConta(null);
        assertNotEquals(d1, criarDTOIdentico());
    }

    // =========================================================
    // TESTES: hashCode()
    // =========================================================

    @Test
    @DisplayName("hashCode: objetos iguais devem ter mesmo hashCode")
    void hashCode_ObjetosIguais_DeveTerMesmoHashCode() {
        assertEquals(criarDTOIdentico().hashCode(), criarDTOIdentico().hashCode());
    }

    @Test
    @DisplayName("hashCode: objetos diferentes devem ter hashCodes diferentes")
    void hashCode_ObjetosDiferentes_DeveTerHashCodesDiferentes() {
        ContaRequestDTO d2 = criarDTOIdentico();
        d2.setSenhaTransacao("9999");
        assertNotEquals(criarDTOIdentico().hashCode(), d2.hashCode());
    }

    @Test
    @DisplayName("hashCode: DTO com campos null não deve lançar exceção")
    void hashCode_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new ContaRequestDTO().hashCode());
    }

    // =========================================================
    // TESTES: toString()
    // =========================================================

    @Test
    @DisplayName("toString: deve conter todos os campos")
    void toString_DeveConterCamposPrincipais() {
        String result = criarDTOIdentico().toString();
        assertAll("toString campos",
                () -> assertTrue(result.contains("clienteId")),
                () -> assertTrue(result.contains("senhaTransacao")),
                () -> assertTrue(result.contains("tipoConta"))
        );
    }

    @Test
    @DisplayName("toString: DTO com campos null não deve lançar exceção")
    void toString_CamposNull_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> new ContaRequestDTO().toString());
    }
}