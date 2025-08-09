package com.example.taskmanager.config;

/*
 * 目前都使用設定檔 ， 先不自己建立 Producer 設定
 * */
//@Configuration
//public class KafkaProducerConfig {
//	
//	// 要使用 KafkaProperties 才會帶入設定檔的設定
//	// 其餘要覆蓋 或補充的再寫上去
//	@Bean
//	public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
//		
//			// 其他設定由設定檔抓取預設值
//			// 這邊也能覆蓋設定檔的設定值
//			Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
//			// 以下都是設定檔就有的預設值，除非要覆蓋，否則可不必再寫一次
//			// configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//			// configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//			// configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//			
//			configProps.put(ProducerConfig.ACKS_CONFIG, "1");
//			configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
//
//			return new DefaultKafkaProducerFactory<>(configProps);
//	}
//	
//	@Bean
//	public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
//		return new KafkaTemplate<>(producerFactory);
//	}
//}
