package acc.br.projetoFinal.Accenture.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Loja + Banco")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de clientes, produtos, pedidos e contas com fluxo bancário simulado")
                        .contact(new Contact()
                                .name("Accenture - Projeto Final")
                                .url("https://www.accenture.com")
                                .email("projeto@accenture.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
