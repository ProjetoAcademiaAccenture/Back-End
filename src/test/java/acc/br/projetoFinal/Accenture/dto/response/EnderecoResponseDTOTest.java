package acc.br.projetoFinal.Accenture.dto.response;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import acc.br.projetoFinal.Accenture.model.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EnderecoResponseDTO - Testes unitários")
class EnderecoResponseDTOTest {

    private Endereco enderecoFake() {
        Endereco e = new Endereco();
        e.setId(1L);
        e.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        e.setCep("58000-000");
        e.setLogradouro("Rua das Flores");
        e.setNumero("123");
        e.setComplemento("Apto 4");
        e.setBairro("Centro");
        e.setCidade("João Pessoa");
        e.setUf("PB");
        return e;
    }

    private EnderecoResponseDTO dtoCompleto() {
        return EnderecoResponseDTO.builder()
                .id(1L)
                .tipoEndereco("RESIDENCIAL")
                .cep("58000-000")
                .logradouro("Rua das Flores")
                .numero("123")
                .complemento("Apto 4")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .build();
    }

    @Nested @DisplayName("Construtores")
    class Construtores {

        @Test @DisplayName("NoArgsConstructor deve criar DTO com todos os campos nulos")
        void noArgsConstructorDeveCriarObjetoVazio() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            assertAll("noArgs",
                    () -> assertNull(dto.getId()),
                    () -> assertNull(dto.getTipoEndereco()),
                    () -> assertNull(dto.getCep()),
                    () -> assertNull(dto.getLogradouro()),
                    () -> assertNull(dto.getNumero()),
                    () -> assertNull(dto.getComplemento()),
                    () -> assertNull(dto.getBairro()),
                    () -> assertNull(dto.getCidade()),
                    () -> assertNull(dto.getUf())
            );
        }

        @Test @DisplayName("AllArgsConstructor deve preencher todos os campos na ordem correta")
        void allArgsConstructorDevePreencherTodosOsCampos() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO(
                    2L, "COMERCIAL", "01310-100", "Av. Paulista",
                    "1000", "Sl 5", "Bela Vista", "São Paulo", "SP"
            );
            assertAll("allArgs",
                    () -> assertEquals(2L,             dto.getId()),
                    () -> assertEquals("COMERCIAL",    dto.getTipoEndereco()),
                    () -> assertEquals("01310-100",    dto.getCep()),
                    () -> assertEquals("Av. Paulista", dto.getLogradouro()),
                    () -> assertEquals("1000",         dto.getNumero()),
                    () -> assertEquals("Sl 5",         dto.getComplemento()),
                    () -> assertEquals("Bela Vista",   dto.getBairro()),
                    () -> assertEquals("São Paulo",    dto.getCidade()),
                    () -> assertEquals("SP",           dto.getUf())
            );
        }

        @Test @DisplayName("Builder deve construir o DTO corretamente")
        void builderDeveConstruirCorretamente() {
            EnderecoResponseDTO dto = dtoCompleto();
            assertAll("builder",
                    () -> assertEquals(1L,               dto.getId()),
                    () -> assertEquals("RESIDENCIAL",    dto.getTipoEndereco()),
                    () -> assertEquals("58000-000",      dto.getCep()),
                    () -> assertEquals("Rua das Flores", dto.getLogradouro()),
                    () -> assertEquals("123",            dto.getNumero()),
                    () -> assertEquals("Apto 4",         dto.getComplemento()),
                    () -> assertEquals("Centro",         dto.getBairro()),
                    () -> assertEquals("João Pessoa",    dto.getCidade()),
                    () -> assertEquals("PB",             dto.getUf())
            );
        }

        @Test @DisplayName("Builder deve aceitar todos os campos nulos — cenário negativo")
        void builderDeveAceitarCamposNulos() {
            assertDoesNotThrow(() -> {
                EnderecoResponseDTO dto = EnderecoResponseDTO.builder().build();
                assertNull(dto.getId());
                assertNull(dto.getTipoEndereco());
                assertNull(dto.getCep());
                assertNull(dto.getLogradouro());
                assertNull(dto.getNumero());
                assertNull(dto.getComplemento());
                assertNull(dto.getBairro());
                assertNull(dto.getCidade());
                assertNull(dto.getUf());
            });
        }
    }

    @Nested @DisplayName("fromEntity")
    class FromEntity {

        @Test @DisplayName("fromEntity deve mapear todos os campos corretamente")
        void fromEntityDeveMapearTodosOsCampos() {
            Endereco endereco = enderecoFake();
            EnderecoResponseDTO dto = EnderecoResponseDTO.fromEntity(endereco);
            assertAll("fromEntity",
                    () -> assertEquals(endereco.getId(),                  dto.getId()),
                    () -> assertEquals(endereco.getTipoEndereco().name(), dto.getTipoEndereco()),
                    () -> assertEquals(endereco.getCep(),                 dto.getCep()),
                    () -> assertEquals(endereco.getLogradouro(),          dto.getLogradouro()),
                    () -> assertEquals(endereco.getNumero(),              dto.getNumero()),
                    () -> assertEquals(endereco.getComplemento(),         dto.getComplemento()),
                    () -> assertEquals(endereco.getBairro(),              dto.getBairro()),
                    () -> assertEquals(endereco.getCidade(),              dto.getCidade()),
                    () -> assertEquals(endereco.getUf(),                  dto.getUf())
            );
        }

        @Test @DisplayName("fromEntity deve retornar DTO não nulo")
        void fromEntityDeveRetornarDtoNaoNulo() {
            assertNotNull(EnderecoResponseDTO.fromEntity(enderecoFake()));
        }

        @Test @DisplayName("fromEntity deve converter tipoEndereco para String via .name()")
        void fromEntityDeveConverterTipoEnderecoParaString() {
            Endereco endereco = enderecoFake();
            EnderecoResponseDTO dto = EnderecoResponseDTO.fromEntity(endereco);
            assertEquals(endereco.getTipoEndereco().name(), dto.getTipoEndereco());
        }

        @Test @DisplayName("fromEntity com complemento nulo deve manter nulo no DTO — cenário negativo")
        void fromEntityComComplementoNuloDeveManterNulo() {
            Endereco endereco = enderecoFake();
            endereco.setComplemento(null);
            assertNull(EnderecoResponseDTO.fromEntity(endereco).getComplemento());
        }

        @Test @DisplayName("fromEntity com numero nulo deve manter nulo no DTO — cenário negativo")
        void fromEntityComNumeroNuloDeveManterNulo() {
            Endereco endereco = enderecoFake();
            endereco.setNumero(null);
            assertNull(EnderecoResponseDTO.fromEntity(endereco).getNumero());
        }

        @Test @DisplayName("fromEntity com id nulo deve manter nulo no DTO — cenário negativo")
        void fromEntityComIdNuloDeveManterNulo() {
            Endereco endereco = enderecoFake();
            endereco.setId(null);
            assertNull(EnderecoResponseDTO.fromEntity(endereco).getId());
        }
    }

    @Nested @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test @DisplayName("Deve atualizar id via setter")
        void deveAtualizarId() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setId(99L);
            assertEquals(99L, dto.getId());
        }

        @Test @DisplayName("Deve aceitar id nulo via setter — cenário negativo")
        void deveAceitarIdNulo() {
            EnderecoResponseDTO dto = dtoCompleto();
            dto.setId(null);
            assertNull(dto.getId());
        }

        @Test @DisplayName("Deve atualizar tipoEndereco via setter")
        void deveAtualizarTipoEndereco() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setTipoEndereco("COMERCIAL");
            assertEquals("COMERCIAL", dto.getTipoEndereco());
        }

        @Test @DisplayName("Deve atualizar cep via setter")
        void deveAtualizarCep() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setCep("12345-678");
            assertEquals("12345-678", dto.getCep());
        }

        @Test @DisplayName("Deve aceitar cep vazio — cenário de borda negativo")
        void deveAceitarCepVazio() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setCep("");
            assertEquals("", dto.getCep());
        }

        @Test @DisplayName("Deve atualizar logradouro via setter")
        void deveAtualizarLogradouro() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setLogradouro("Rua Nova");
            assertEquals("Rua Nova", dto.getLogradouro());
        }

        @Test @DisplayName("Deve atualizar numero via setter")
        void deveAtualizarNumero() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setNumero("S/N");
            assertEquals("S/N", dto.getNumero());
        }

        @Test @DisplayName("Deve atualizar complemento via setter")
        void deveAtualizarComplemento() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setComplemento("Bloco B");
            assertEquals("Bloco B", dto.getComplemento());
        }

        @Test @DisplayName("Deve aceitar complemento nulo via setter — cenário negativo")
        void deveAceitarComplementoNulo() {
            EnderecoResponseDTO dto = dtoCompleto();
            dto.setComplemento(null);
            assertNull(dto.getComplemento());
        }

        @Test @DisplayName("Deve atualizar bairro via setter")
        void deveAtualizarBairro() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setBairro("Manaíra");
            assertEquals("Manaíra", dto.getBairro());
        }

        @Test @DisplayName("Deve atualizar cidade via setter")
        void deveAtualizarCidade() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setCidade("Campina Grande");
            assertEquals("Campina Grande", dto.getCidade());
        }

        @Test @DisplayName("Deve atualizar uf via setter")
        void deveAtualizarUf() {
            EnderecoResponseDTO dto = new EnderecoResponseDTO();
            dto.setUf("PE");
            assertEquals("PE", dto.getUf());
        }

        @Test @DisplayName("Deve aceitar uf nula via setter — cenário negativo")
        void deveAceitarUfNula() {
            EnderecoResponseDTO dto = dtoCompleto();
            dto.setUf(null);
            assertNull(dto.getUf());
        }
    }

    @Nested @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test @DisplayName("Dois DTOs com mesmos valores devem ser iguais")
        void doisDTOsIguaisDevemSerIguais() {
            assertEquals(dtoCompleto(), dtoCompleto());
            assertEquals(dtoCompleto().hashCode(), dtoCompleto().hashCode());
        }

        @Test @DisplayName("DTO deve ser igual a si mesmo")
        void deveSerIgualASiMesmo() {
            EnderecoResponseDTO dto = dtoCompleto();
            assertEquals(dto, dto);
        }

        @Test @DisplayName("DTO não deve ser igual a null — cenário negativo")
        void naoDeveSerIgualANull() {
            assertNotEquals(null, dtoCompleto());
        }

        @Test @DisplayName("DTO não deve ser igual a outro tipo — cenário negativo")
        void naoDeveSerIgualAOutroTipo() {
            assertNotEquals("string", dtoCompleto());
        }

        @Test @DisplayName("DTOs com id diferente não devem ser iguais — cenário negativo")
        void dtosComIdDiferenteNaoDevemSerIguais() {
            EnderecoResponseDTO a = dtoCompleto();
            EnderecoResponseDTO b = dtoCompleto();
            b.setId(999L);
            assertNotEquals(a, b);
        }

        @Test @DisplayName("DTOs com cep diferente não devem ser iguais — cenário negativo")
        void dtosComCepDiferenteNaoDevemSerIguais() {
            EnderecoResponseDTO a = dtoCompleto();
            EnderecoResponseDTO b = dtoCompleto();
            b.setCep("00000-000");
            assertNotEquals(a, b);
        }

        @Test @DisplayName("DTOs com uf diferente não devem ser iguais — cenário negativo")
        void dtosComUfDiferenteNaoDevemSerIguais() {
            EnderecoResponseDTO a = dtoCompleto();
            EnderecoResponseDTO b = dtoCompleto();
            b.setUf("SP");
            assertNotEquals(a, b);
        }

        @Test @DisplayName("hashCode deve diferir quando id é diferente")
        void hashCodeDeveDiferirComIdDiferente() {
            EnderecoResponseDTO a = dtoCompleto();
            EnderecoResponseDTO b = dtoCompleto();
            b.setId(777L);
            assertNotEquals(a.hashCode(), b.hashCode());
        }
    }

    @Nested @DisplayName("toString")
    class ToStringTests {

        @Test @DisplayName("toString não deve retornar nulo")
        void toStringNaoDeveRetornarNulo() {
            assertNotNull(dtoCompleto().toString());
        }

        @Test @DisplayName("toString deve conter os valores dos campos principais")
        void toStringDeveConterValoresDoCampos() {
            String result = dtoCompleto().toString();
            assertAll("toString",
                    () -> assertTrue(result.contains("58000-000")),
                    () -> assertTrue(result.contains("João Pessoa")),
                    () -> assertTrue(result.contains("PB")),
                    () -> assertTrue(result.contains("RESIDENCIAL"))
            );
        }

        @Test @DisplayName("toString de DTO vazio não deve lançar exceção — cenário negativo")
        void toStringDtoVazioNaoDeveLancarExcecao() {
            assertDoesNotThrow(() -> assertNotNull(new EnderecoResponseDTO().toString()));
        }
    }

    @Nested @DisplayName("Builder")
    class BuilderTests {

        @Test @DisplayName("Builder.toString() não deve lançar exceção")
        void builderToStringNaoDeveLancarExcecao() {
            assertDoesNotThrow(() ->
                    assertNotNull(EnderecoResponseDTO.builder().id(1L).cep("58000-000").uf("PB").toString())
            );
        }

        @Test @DisplayName("Builder deve sobrescrever o último valor atribuído ao mesmo campo")
        void builderDeveSobrescreverValor() {
            EnderecoResponseDTO dto = EnderecoResponseDTO.builder()
                    .cep("00000-000")
                    .cep("58000-000")
                    .build();
            assertEquals("58000-000", dto.getCep());
        }
    }

    @Nested @DisplayName("Casos de borda")
    class CasosDeBorda {

        @Test @DisplayName("Deve aceitar strings com espaços nos campos de texto")
        void deveAceitarStringsComEspacos() {
            EnderecoResponseDTO dto = EnderecoResponseDTO.builder()
                    .logradouro("   ").bairro("   ").build();
            assertEquals("   ", dto.getLogradouro());
            assertEquals("   ", dto.getBairro());
        }

        @Test @DisplayName("Deve aceitar id Long.MAX_VALUE")
        void deveAceitarIdMaximo() {
            EnderecoResponseDTO dto = EnderecoResponseDTO.builder().id(Long.MAX_VALUE).build();
            assertEquals(Long.MAX_VALUE, dto.getId());
        }

        @Test @DisplayName("Dois DTOs sem id e mesmos dados devem ser iguais")
        void doisDtosSemIdDevemSerIguais() {
            EnderecoResponseDTO a = EnderecoResponseDTO.builder().cep("58000-000").uf("PB").build();
            EnderecoResponseDTO b = EnderecoResponseDTO.builder().cep("58000-000").uf("PB").build();
            assertEquals(a, b);
        }
    }
}