package acc.br.projetoFinal.Accenture.config;

import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {
        return args -> {

            if (clienteRepository.findByCpf("00000000000").isEmpty()) {

                Cliente empresa = Cliente.builder()
                    .nome("EMPRESA")
                    .cpf("00000000000")
                    .email("empresa@loja.com")
                    .telefone("1140000000")
                    .senha(passwordEncoder.encode("123456"))
                    .tipoCliente(TipoCliente.ROLE_ADMIN)
                    .build();

                Cliente empresaSalva = clienteRepository.save(empresa);

                Conta contaEmpresa = Conta.builder()
                    .numeroConta("EMPRESA-001")
                    .saldo(new BigDecimal("10000.00"))
                    .tipo(TipoConta.JURIDICA)
                    .cliente(empresaSalva)
                    .ativo(true)
                    .build();

                contaRepository.save(contaEmpresa);

                System.out.println("✓ Conta da empresa criada com sucesso!");
                System.out.println("  Número da conta: EMPRESA-001");
                System.out.println("  Saldo inicial: R$ 10.000,00");
            }
        };
    }
}