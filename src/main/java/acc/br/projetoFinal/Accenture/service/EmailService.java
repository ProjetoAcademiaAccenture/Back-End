package acc.br.projetoFinal.Accenture.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarBoasVindas(String emailDestino, String nomeCliente) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Bem-vindo ao Accenture Bank!");
            message.setText(String.format("""
                    Olá, %s!
                    
                    Seu cadastro foi realizado com sucesso.
                    Agora você pode criar sua conta bancária e começar a usar nossos serviços.
                    
                    Atenciosamente,
                    Equipe Accenture Bank
                    """, nomeCliente));

            mailSender.send(message);
            log.info("Email de boas-vindas enviado para: {}", emailDestino);

        } catch (Exception e) {
            log.error("Falha ao enviar email de boas-vindas para {}: {}", emailDestino, e.getMessage());
        }
    }

    public void enviarDadosConta(String emailDestino, String nomeCliente,
                                  String numeroConta, String tipoConta) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDestino);
            message.setSubject("Sua conta bancária foi criada!");
            message.setText(String.format("""
                    Olá, %s!
                    
                    Sua conta foi criada com sucesso. Seguem seus dados bancários:
                    
                    Número da Conta : %s
                    Tipo da Conta   : %s
                    
                    Guarde essas informações em local seguro.
                    
                    Atenciosamente,
                    Equipe Accenture Bank
                    """, nomeCliente, numeroConta, tipoConta));

            mailSender.send(message);
            log.info("Email de dados da conta enviado para: {}", emailDestino);

        } catch (Exception e) {
            log.error("Falha ao enviar email de dados da conta para {}: {}", emailDestino, e.getMessage());
        }
    }
}