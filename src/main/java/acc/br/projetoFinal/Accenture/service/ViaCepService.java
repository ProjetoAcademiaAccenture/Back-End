package acc.br.projetoFinal.Accenture.service;

import acc.br.projetoFinal.Accenture.dto.response.ViaCepResponseDTO;
import acc.br.projetoFinal.Accenture.exception.RecursoNaoEncontradoException;
import acc.br.projetoFinal.Accenture.model.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final RestTemplate restTemplate;
    private static final String URL = "https://viacep.com.br/ws/{cep}/json/";

    public Endereco buscarEnderecoPorCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8)
            throw new IllegalArgumentException("CEP inválido: " + cep);

        try {
            ViaCepResponseDTO resp = restTemplate.getForObject(URL, ViaCepResponseDTO.class, cepLimpo);

            if (resp == null || resp.isErro())
                throw new RecursoNaoEncontradoException("CEP não encontrado: " + cep);

            return Endereco.builder()
                    .cep(cepLimpo)
                    .logradouro(resp.getLogradouro())
                    .bairro(resp.getBairro())
                    .cidade(resp.getLocalidade())
                    .uf(resp.getUf())
                    .build();
        } catch (Exception e) {
            throw new RecursoNaoEncontradoException("Erro ao buscar CEP: " + cep);
        }
    }
}
