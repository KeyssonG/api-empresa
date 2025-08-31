package keysson.apis.empresa.service;


import keysson.apis.empresa.dto.PendingMessages;
import keysson.apis.empresa.dto.RegisteredCompanyEvent;
import keysson.apis.empresa.repository.RabbitRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RabbitService {


    private final RabbitRepository rabbitRepository;

    public RabbitService(RabbitRepository rabbitRepository) {
        this.rabbitRepository = rabbitRepository;
    }

    public void saveMessagesInBank(RegisteredCompanyEvent event, int status) throws SQLException {
        PendingMessages pendingMessages = new PendingMessages();
        pendingMessages.setId(event.getIdEmpresa());
        pendingMessages.setName(event.getName());
        pendingMessages.setEmail(event.getEmail());
        pendingMessages.setCnpj(event.getCnpj());
        pendingMessages.setUsername(event.getUsername());
        pendingMessages.setStatus(status);

        rabbitRepository.saveMenssage(pendingMessages);
    }
}
