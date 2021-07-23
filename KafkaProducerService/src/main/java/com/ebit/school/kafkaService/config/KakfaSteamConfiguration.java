package com.ebit.school.kafkaService.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KakfaSteamConfiguration {

	@Autowired
	private KafkaProperties kafkaProperties;

	@Value("${spring.kafka.consumer.group-id")
	String groupId;
	
	@Value("${spring.kafka.consumer.bootstrap-servers")
	String bootStrapServer;
	
	
		
	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, groupId);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String());
		props.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, String.class);
		return new StreamsConfig(props);
	}

	@Bean
	public KStream<String, String> kStreamJson(StreamsBuilder builder) {
		KStream<String, String> source = builder.stream("test-stream",
				Consumed.with(Serdes.String(), Serdes.String()));

		source.foreach(new ForeachAction<String, String>() {
		    public void apply(String key, String value) {
		        System.out.println(key + ": " + value);
		    }
		 });
		
		source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
		    @Override
		    public Iterable<String> apply(String value) {
		        ArrayList<String> keywords = new ArrayList<String>();

		       System.out.println(value);

		        return keywords;
		    }
		});
		
		return source;
	}
}