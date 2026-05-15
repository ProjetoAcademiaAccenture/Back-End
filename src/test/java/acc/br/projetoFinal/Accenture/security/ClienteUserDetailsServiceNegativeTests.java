package acc.br.projetoFinal.Accenture.security;

import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("ClienteUserDetailsService - Testes Negativos")
class ClienteUserDetailsServiceNegativeTests {

    @Autowired
    private ClienteUserDetailsService userDetailsService;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private ContaRepository contaRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail("teste@example.com");
        cliente.setSenha("senha123");
        cliente.setTipoCliente(TipoCliente.ROLE_USER);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email não encontrado")
    void deveLancarExcecaoParaEmailNaoEncontrado() {
        when(clienteRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("naoexiste@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com mensagem apropriada")
    void deveLancarExcecaoComMensagemApropiada() {
        when(clienteRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("naoexiste@example.com");
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email vazio")
    void deveLancarExcecaoParaEmailVazio() {
        when(clienteRepository.findByEmail("")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email null")
    void deveLancarExcecaoParaEmailNull() {
        when(clienteRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email com espaços")
    void deveLancarExcecaoParaEmailComEspacos() {
        when(clienteRepository.findByEmail("   ")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("   ");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email case-sensitive não encontrado")
    void deveLancarExcecaoParaEmailCaseSensitiveNaoEncontrado() {
        when(clienteRepository.findByEmail("TESTE@EXAMPLE.COM")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("TESTE@EXAMPLE.COM");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email com caracteres especiais")
    void deveLancarExcecaoParaEmailComCaracteresEspeciais() {
        when(clienteRepository.findByEmail("teste!@#@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("teste!@#@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email muito longo")
    void deveLancarExcecaoParaEmailMuitoLongo() {
        String emailMuitoLongo = "a".repeat(1000) + "@example.com";
        when(clienteRepository.findByEmail(emailMuitoLongo)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(emailMuitoLongo);
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email com quebra de linha")
    void deveLancarExcecaoParaEmailComQuebralinha() {
        when(clienteRepository.findByEmail("teste@example.com\n")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("teste@example.com\n");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email com valores unicode")
    void deveLancarExcecaoParaEmailComUnicode() {
        when(clienteRepository.findByEmail("usuário@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("usuário@example.com");
        });
    }

    @Test
    @DisplayName("Não deve encontrar usuário com email similar mas diferente")
    void naoDeveEncontrarEmailSimilar() {
        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByEmail("teste.email@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("teste.email@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException se cliente com tipoCliente null for retornado")
    void deveLancarExcecaoParaClienteComTipoNull() {
        Cliente clienteSemTipo = new Cliente();
        clienteSemTipo.setEmail("teste@example.com");
        clienteSemTipo.setSenha("senha123");
        clienteSemTipo.setTipoCliente(null);

        when(clienteRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(clienteSemTipo));

        assertThrows(NullPointerException.class, () -> {
            userDetailsService.loadUserByUsername("teste@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para múltiplas tentativas com email errado")
    void deveLancarExcecaoParaMultiplasTentativas() {
        when(clienteRepository.findByEmail("errado@example.com")).thenReturn(Optional.empty());

        // Primeira tentativa
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("errado@example.com");
        });

        // Segunda tentativa
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("errado@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email contendo dois @")
    void deveLancarExcecaoParaEmailComDoisArroba() {
        when(clienteRepository.findByEmail("teste@@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("teste@@example.com");
        });
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException para email sem domínio")
    void deveLancarExcecaoParaEmailSemDominio() {
        when(clienteRepository.findByEmail("teste@")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("teste@");
        });
    }
}
