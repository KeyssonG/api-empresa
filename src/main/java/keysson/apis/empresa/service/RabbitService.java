package keysson.apis.empresa.service;

import keysson.apis.empresa.dto.EmpresaCadastradaEvent;
import keysson.apis.empresa.dto.MensagensPendentes;
import keysson.apis.empresa.repository.RabbitRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RabbitService {


    private final RabbitRepository rabbitRepository;

    public RabbitService(RabbitRepository rabbitRepository) {
        this.rabbitRepository = rabbitRepository;
    }

    public void saveMessagesInBank(EmpresaCadastradaEvent event, int status) throws SQLException {
        MensagensPendentes mensagenPendente = new MensagensPendentes();
        mensagenPendente.setId(event.getIdEmpresa());
        mensagenPendente.setName(event.getName());
        mensagenPendente.setEmail(event.getEmail());
        mensagenPendente.setCnpj(event.getEmail());
        mensagenPendente.setUsername(event.getUsername());
        mensagenPendente.setStatus(status);

        rabbitRepository.saveMenssage(mensagenPendente);
    }
}
