package com.microservico.estoquepreco.connections;

import com.microservico.estoquepreco.constantes.RabbitmqConstantes;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConnection {
    private static final String NOME_EXCHANGE = "amqp.direct";

    private AmqpAdmin amqpAdmin;

    public RabbitMQConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    private Queue fila(String nomeFila) {
        return new Queue(nomeFila, true, false, false);
    }

    private DirectExchange trocadireta() {
        return new DirectExchange(NOME_EXCHANGE);
    }

    private Binding relacionamento(Queue fila, DirectExchange troca) {
        return new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
    }

    @PostConstruct
    private void adicionar() { // Removido o parâmetro
        Queue filaEstoque = this.fila(RabbitmqConstantes.FILA_ESTOQUE);
        Queue filaPreco = this.fila(RabbitmqConstantes.FILA_PRECO);

        DirectExchange troca = this.trocadireta();

        Binding ligacaoEstoque = this.relacionamento(filaEstoque, troca);
        Binding ligacaoPreco = this.relacionamento(filaPreco, troca);

        // Criando as filas do RabbitMQ
        this.amqpAdmin.declareQueue(filaEstoque);
        this.amqpAdmin.declareQueue(filaPreco);
        this.amqpAdmin.declareExchange(troca);
        this.amqpAdmin.declareBinding(ligacaoEstoque);
        this.amqpAdmin.declareBinding(ligacaoPreco);
    }
}