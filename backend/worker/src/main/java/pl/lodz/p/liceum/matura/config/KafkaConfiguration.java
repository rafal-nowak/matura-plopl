package pl.lodz.p.liceum.matura.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskEvent;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {


//    public static final String KAFKA_IP_ADDRESS = "127.0.0.1:9092";
//    public static final String KAFKA_GROUP_ID = "group_json";
//    public static final String KAFKA_TRUSTED_PACKAGES = "*";
//    public static final String TASKS_INBOUND_TOPIC = "Kafka_Task_json";
//    public static final String TASKS_OUTBOUND_TOPIC = "Kafka_Task_Report_json";

    @Bean
    public ConsumerFactory<String, TaskEvent> taskConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>();


        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaProperties.getTrustedPackages());


        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(TaskEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskEvent> taskKafkaListenerFactory(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, TaskEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskConsumerFactory(kafkaProperties));
        return factory;
    }

    @Bean
    public ProducerFactory<String, TaskEvent> taskProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }


    @Bean
    public KafkaTemplate<String, TaskEvent> taskKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(taskProducerFactory(kafkaProperties));
    }


}
