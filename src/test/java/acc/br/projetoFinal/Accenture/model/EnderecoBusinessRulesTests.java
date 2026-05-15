package acc.br.projetoFinal.Accenture.model;

import acc.br.projetoFinal.Accenture.enums.TipoEndereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Endereco - Testes de Cobertura Total")
class EnderecoBusinessRulesTests {

    private Cliente clienteCompartilhado;

    @BeforeEach
    void setUp() {
        // Criamos uma instância fixa para evitar o erro de referência de memória (Hash)
        clienteCompartilhado = new Cliente();
        clienteCompartilhado.setId(1L);
        clienteCompartilhado.setNome("João Silva");
    }

    private Endereco enderecoCompleto() {
        return Endereco.builder()
                .id(1L)
                .cliente(clienteCompartilhado)
                .tipoEndereco(TipoEndereco.RESIDENCIAL)
                .cep("58000000")
                .logradouro("Rua das Flores")
                .numero("123")
                .complemento("Apto 4")
                .bairro("Centro")
                .cidade("João Pessoa")
                .uf("PB")
                .build();
    }

    @Nested
    @DisplayName("1. Construtores e Builder")
    class Construtores {

        @Test
        @DisplayName("Deve testar NoArgsConstructor e valor default")
        void testeNoArgsConstructor() {
            Endereco e = new Endereco();
            assertNotNull(e);
            // Verifica se o builder default ou inicialização manual funciona
            e.setTipoEndereco(TipoEndereco.RESIDENCIAL);
            assertEquals(TipoEndereco.RESIDENCIAL, e.getTipoEndereco());
        }

        @Test
        @DisplayName("Deve testar AllArgsConstructor")
        void testeAllArgsConstructor() {
            Endereco e = new Endereco(1L, clienteCompartilhado, TipoEndereco.COMERCIAL, 
                "58000", "Rua A", "1", "C1", "Bairro", "Cidade", "UF");
            assertEquals(1L, e.getId());
            assertEquals("58000", e.getCep());
        }

        @Test
        @DisplayName("Builder deve funcionar com campos nulos")
        void testeBuilderNulls() {
            Endereco e = Endereco.builder().build();
            assertNull(e.getId());
        }
    }

    @Nested
    @DisplayName("2. Getters e Setters")
    class GettersSetters {
        @Test
        @DisplayName("Deve cobrir todos os setters")
        void testeSetters() {
            Endereco e = new Endereco();
            e.setId(1L);
            e.setCliente(clienteCompartilhado);
            e.setTipoEndereco(TipoEndereco.ENTREGA);
            e.setCep("123");
            e.setLogradouro("Log");
            e.setNumero("10");
            e.setComplemento("Comp");
            e.setBairro("Bai");
            e.setCidade("Cid");
            e.setUf("UF");

            assertAll("getters",
                () -> assertEquals(1L, e.getId()),
                () -> assertEquals(clienteCompartilhado, e.getCliente()),
                () -> assertEquals(TipoEndereco.ENTREGA, e.getTipoEndereco()),
                () -> assertEquals("123", e.getCep()),
                () -> assertEquals("Log", e.getLogradouro()),
                () -> assertEquals("10", e.getNumero()),
                () -> assertEquals("Comp", e.getComplemento()),
                () -> assertEquals("Bai", e.getBairro()),
                () -> assertEquals("Cid", e.getCidade()),
                () -> assertEquals("UF", e.getUf())
            );
        }
    }

    @Nested
    @DisplayName("3. Equals e HashCode (Aumentando Branches)")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual ao mesmo objeto e a um objeto idêntico")
        void testeEqualsIdentico() {
            Endereco e1 = enderecoCompleto();
            Endereco e2 = enderecoCompleto();

            assertEquals(e1, e1); // Mesma referência
            assertEquals(e1, e2); // Valores idênticos
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        @DisplayName("Deve falhar no equals com objetos diferentes ou nulos")
        void testeEqualsDiferente() {
            Endereco e1 = enderecoCompleto();
            assertNotEquals(e1, null);
            assertNotEquals(e1, new Object());
            
            Endereco e2 = enderecoCompleto();
            e2.setId(2L);
            assertNotEquals(e1, e2);

            Endereco e3 = enderecoCompleto();
            e3.setCep(null);
            assertNotEquals(e1, e3);
        }

        @Test
        @DisplayName("Deve testar branches de campos nulos no Equals")
        void testeEqualsNullBranches() {
            Endereco e1 = new Endereco();
            Endereco e2 = new Endereco();
            assertEquals(e1, e2); // Ambos nulos são iguais

            e1.setCep("58000");
            assertNotEquals(e1, e2);
        }
        
        @Test
        @DisplayName("Deve testar canEqual do Lombok")
        void testeCanEqual() {
            Endereco e1 = new Endereco();
            assertTrue(e1.canEqual(new Endereco()));
            assertFalse(e1.canEqual(new Cliente()));
        }
    }

    @Nested
    @DisplayName("4. Enum e ToString")
    class EnumToString {

        @Test
        @DisplayName("Deve cobrir todos os valores do Enum")
        void testeEnumValues() {
            for (TipoEndereco t : TipoEndereco.values()) {
                assertNotNull(TipoEndereco.valueOf(t.name()));
            }
        }

        @Test
        @DisplayName("ToString deve conter dados da classe")
        void testeToString() {
            String s = enderecoCompleto().toString();
            assertTrue(s.contains("58000000"));
            assertTrue(s.contains("id=1"));
        }
    }
}