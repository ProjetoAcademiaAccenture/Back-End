package acc.br.projetoFinal.Accenture.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private static final String EMAIL_DESTINO  = "cliente@email.com";
    private static final String NOME_CLIENTE   = "Maria Silva";
    private static final String NUMERO_CONTA   = "12345-6";
    private static final String TIPO_CONTA     = "CORRENTE";

    // =======================================================================
    // enviarBoasVindas — caminho feliz
    // =======================================================================

    @Test
    void enviarBoasVindas_deveEnviarEmailComCamposCorretos() {
        emailService.enviarBoasVindas(EMAIL_DESTINO, NOME_CLIENTE);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensagem = captor.getValue();

        assertAll(
            () -> assertNotNull(mensagem.getTo()),
            () -> assertEquals(EMAIL_DESTINO, mensagem.getTo()[0]),
            () -> assertEquals("Bem-vindo ao Accenture Bank!", mensagem.getSubject()),
            () -> assertNotNull(mensagem.getText()),
            () -> assertTrue(mensagem.getText().contains(NOME_CLIENTE))
        );
    }

    @Test
    void enviarBoasVindas_deveChamarMailSenderUmaVez() {
        emailService.enviarBoasVindas(EMAIL_DESTINO, NOME_CLIENTE);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // =======================================================================
    // enviarBoasVindas — caminho de exceção (branch catch)
    // =======================================================================

    @Test
    void enviarBoasVindas_naoDeveLancarExcecao_quandoMailSenderFalha() {
        doThrow(new RuntimeException("SMTP indisponível"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.enviarBoasVindas(EMAIL_DESTINO, NOME_CLIENTE));
    }

    @Test
    void enviarBoasVindas_deveContinuarExecucao_aposExcecaoDoMailSender() {
        doThrow(new RuntimeException("Timeout"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // não lança — o catch absorve e loga
        emailService.enviarBoasVindas(EMAIL_DESTINO, NOME_CLIENTE);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // =======================================================================
    // enviarDadosConta — caminho feliz
    // =======================================================================

    @Test
    void enviarDadosConta_deveEnviarEmailComCamposCorretos() {
        emailService.enviarDadosConta(EMAIL_DESTINO, NOME_CLIENTE, NUMERO_CONTA, TIPO_CONTA);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensagem = captor.getValue();

        assertAll(
            () -> assertNotNull(mensagem.getTo()),
            () -> assertEquals(EMAIL_DESTINO, mensagem.getTo()[0]),
            () -> assertEquals("Sua conta bancária foi criada!", mensagem.getSubject()),
            () -> assertNotNull(mensagem.getText()),
            () -> assertTrue(mensagem.getText().contains(NOME_CLIENTE)),
            () -> assertTrue(mensagem.getText().contains(NUMERO_CONTA)),
            () -> assertTrue(mensagem.getText().contains(TIPO_CONTA))
        );
    }

    @Test
    void enviarDadosConta_deveChamarMailSenderUmaVez() {
        emailService.enviarDadosConta(EMAIL_DESTINO, NOME_CLIENTE, NUMERO_CONTA, TIPO_CONTA);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // =======================================================================
    // enviarDadosConta — caminho de exceção (branch catch)
    // =======================================================================

    @Test
    void enviarDadosConta_naoDeveLancarExcecao_quandoMailSenderFalha() {
        doThrow(new RuntimeException("SMTP indisponível"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.enviarDadosConta(EMAIL_DESTINO, NOME_CLIENTE, NUMERO_CONTA, TIPO_CONTA));
    }

    @Test
    void enviarDadosConta_deveContinuarExecucao_aposExcecaoDoMailSender() {
        doThrow(new RuntimeException("Conexão recusada"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.enviarDadosConta(EMAIL_DESTINO, NOME_CLIENTE, NUMERO_CONTA, TIPO_CONTA);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // =======================================================================
    // Conteúdo do corpo do e-mail
    // =======================================================================

    @Test
    void enviarBoasVindas_corpoDeveConterMensagemDeCadastro() {
        emailService.enviarBoasVindas(EMAIL_DESTINO, NOME_CLIENTE);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String corpo = captor.getValue().getText();
        assertAll(
            () -> assertTrue(corpo.contains("cadastro foi realizado com sucesso")),
            () -> assertTrue(corpo.contains("Equipe Accenture Bank"))
        );
    }

    @Test
    void enviarDadosConta_corpoDeveConterDadosBancarios() {
        emailService.enviarDadosConta(EMAIL_DESTINO, NOME_CLIENTE, NUMERO_CONTA, TIPO_CONTA);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String corpo = captor.getValue().getText();
        assertAll(
            () -> assertTrue(corpo.contains("Número da Conta")),
            () -> assertTrue(corpo.contains("Tipo da Conta")),
            () -> assertTrue(corpo.contains("Guarde essas informações")),
            () -> assertTrue(corpo.contains("Equipe Accenture Bank"))
        );
    }
}