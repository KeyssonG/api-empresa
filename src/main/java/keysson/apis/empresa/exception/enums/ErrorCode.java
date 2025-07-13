package keysson.apis.empresa.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    CNPJ_JA_CADASTRADO("Empresa com este CNPJ já está cadastrada.", HttpStatus.BAD_REQUEST),
    EMAIL_INVALIDO("O e-mail informado é inválido.", HttpStatus.BAD_REQUEST),
    EMPRESA_NAO_ENCONTRADA("Empresa não encontrada.", HttpStatus.NOT_FOUND),
    ERRO_INTERNO("Erro interno no servidor.", HttpStatus.INTERNAL_SERVER_ERROR),
    ERRO_CADASTRAR("Erro ao cadastrar empresa", HttpStatus.BAD_REQUEST),
    USUARIOS_NAO_ENCONTRADOS("Quantidade de usuários não encontrada nessa data.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}