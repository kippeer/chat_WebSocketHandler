# Chat em Tempo Real com WebSocket

API de chat em tempo real desenvolvida com Spring Boot e WebSocket.

## Tecnologias

- Java 11
- Spring Boot 2.7.0
- WebSocket
- Maven
- Lombok

## Estrutura do Projeto

```
chat-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/exemplo/chat/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── handler/
│   │   │       ├── model/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── static/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Endpoints

### WebSocket

- **URL**: `ws://localhost:8080/chat`
- **Protocolo**: WebSocket
- **Descrição**: Endpoint principal para conexão WebSocket

### Mensagens

#### Formato de Mensagem (Envio)

```json
{
    "sender": "string",
    "content": "string",
    "type": "CHAT | JOIN | LEAVE"
}
```

#### Formato de Mensagem (Recebimento)

```json
{
    "id": "uuid",
    "sender": "string",
    "content": "string",
    "timestamp": "datetime",
    "type": "CHAT | JOIN | LEAVE"
}
```

### Tipos de Mensagem

- `CHAT`: Mensagem normal de chat
- `JOIN`: Notificação de entrada de usuário
- `LEAVE`: Notificação de saída de usuário

## Como Executar

1. Clone o repositório
2. Navegue até a pasta do projeto:
   ```bash
   cd chat-api
   ```
3. Execute o build:
   ```bash
   mvn clean install
   ```
4. Inicie a aplicação:
   ```bash
   mvn spring-boot:run
   ```
5. Acesse `http://localhost:8080` para ver a interface de teste

## Exemplo de Uso (JavaScript)

```javascript
// Conectar ao WebSocket
const socket = new WebSocket('ws://localhost:8080/chat');

// Enviar mensagem
const message = {
    sender: 'User',
    content: 'Hello World',
    type: 'CHAT'
};
socket.send(JSON.stringify(message));

// Receber mensagens
socket.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('Mensagem recebida:', message);
};
```

## Funcionalidades

- ✅ Conexão WebSocket em tempo real
- ✅ Suporte a múltiplos usuários simultâneos
- ✅ Mensagens instantâneas
- ✅ Notificações de entrada/saída de usuários
- ✅ Interface de teste básica
- ✅ Tratamento de exceções
- ✅ Validação de mensagens

## Segurança

- CORS configurado para permitir conexões de qualquer origem (`*`)
- Validação de formato de mensagens
- Proteção contra XSS através de sanitização de input
- Sessões WebSocket gerenciadas de forma thread-safe

## Testes

Execute os testes com:

```bash
mvn test
```

## Monitoramento

A aplicação possui logs detalhados configurados para o nível DEBUG:

```properties
logging.level.org.springframework.web=DEBUG
```

## Limitações

- Não possui persistência de mensagens
- Autenticação não implementada
- Sem suporte a salas de chat separadas
- Sem suporte a mensagens privadas

## Próximas Melhorias

- [ ] Implementar autenticação de usuários
- [ ] Adicionar persistência de mensagens
- [ ] Criar suporte a salas de chat
- [ ] Implementar mensagens privadas
- [ ] Adicionar suporte a emojis
- [ ] Implementar rate limiting
- [ ] Adicionar suporte a upload de arquivos

## Contribuição

1. Faça o fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT.