package com.example.bluetech.config;

import com.example.bluetech.messaging.message.PredictMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "post.exchange";
    public static final String ROUTING_PREDICT_KEY = "post.predict";
    public static final String ROUTING_DELAY_KEY = "post.delay";
    public static final String PREDICT_QUEUE = "post.predict.queue";
    public static final String DELAY_QUEUE = "post.delay.queue";



    @Bean
    public DirectExchange exchange() {
        DirectExchange exchange = new DirectExchange(EXCHANGE);
        return exchange;
    }

    @Bean
    public Queue predictQueue() {
        return QueueBuilder.durable(PREDICT_QUEUE).build();
    }

    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE);
        args.put("x-dead-letter-routing-key", ROUTING_PREDICT_KEY);
        args.put("x-message-ttl", 60000);
        return QueueBuilder.durable(DELAY_QUEUE).withArguments(args).build();
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder
                .bind(delayQueue())
                .to(exchange())
                .with(ROUTING_DELAY_KEY);
    }

    @Bean
    public Binding predictBinding() {
        return BindingBuilder
                .bind(predictQueue())
                .to(exchange())
                .with(ROUTING_PREDICT_KEY);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.bluetech.messaging.message.PredictMessage", PredictMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message delivered successfully");
            } else {
                log.error("Message delivery failed: {}", cause);
            }
        });
        template.setMessageConverter(jsonMessageConverter());
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("deliveryTag");
            message.getMessageProperties().getHeaders().remove("contentLength");
            message.getMessageProperties().getHeaders().remove("priority");
            return message;
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter()); // ✅ Thêm dòng này
        factory.setErrorHandler(t -> {
            System.err.println("Consumer error: " + t.getMessage());
            t.printStackTrace();
        });
        return factory;
    }


}
