package acc.br.projetoFinal.Accenture.config;

import acc.br.projetoFinal.Accenture.enums.TipoCliente;
import acc.br.projetoFinal.Accenture.enums.TipoConta;
import acc.br.projetoFinal.Accenture.model.Cliente;
import acc.br.projetoFinal.Accenture.model.Conta;
import acc.br.projetoFinal.Accenture.repository.ClienteRepository;
import acc.br.projetoFinal.Accenture.repository.ContaRepository;
import acc.br.projetoFinal.Accenture.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final String CPF_EMPRESA = "00000000000";
    private static final String NUMERO_CONTA_EMPRESA = "1234567-8";

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProdutoRepository produtoRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {

            Cliente empresa = clienteRepository.findByCpf(CPF_EMPRESA)
                .orElseGet(() -> clienteRepository.save(
                    Cliente.builder()
                        .nome("EMPRESA")
                        .cpf(CPF_EMPRESA)
                        .email("empresa@loja.com")
                        .telefone("1140000000")
                        .senha(passwordEncoder.encode("123456"))
                        .tipoCliente(TipoCliente.ROLE_ADMIN)
                        .build()
                ));

            if (contaRepository.findByNumeroConta(NUMERO_CONTA_EMPRESA).isEmpty()) {
                Conta contaEmpresa = Conta.builder()
                    .numeroConta(NUMERO_CONTA_EMPRESA)
                    .senhaTransacao(passwordEncoder.encode("1234"))
                    .saldo(new BigDecimal("10000.00"))
                    .limiteCreditoDisponivel(new BigDecimal("5000.00"))
                    .tipo(TipoConta.JURIDICA)
                    .cliente(empresa)
                    .build();

                contaRepository.save(contaEmpresa);

                System.out.println("✓ Conta da empresa criada com sucesso!");
                System.out.println("  Número da conta: " + NUMERO_CONTA_EMPRESA);
                System.out.println("  Saldo inicial: R$ 10.000,00");
            }

            ProdutoDataInitializer produtoInitializer = new ProdutoDataInitializer(produtoRepository);
            produtoInitializer.initProdutos().run(args);
        };
    }
}