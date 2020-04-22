package com.dom.elastic.config;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

	@Value("${elasticsearch.host:localhost}")
	private String host;
	
	@Value("${elasticsearch.port:9300}")
	private int port;
	
	@Bean
	public Client elasticClient() {
		TransportClient client = null;
		
		try {
			System.out.println("HOST: " + host + " PORT: " + port);
			Settings settings = Settings.builder()
			        .put("cluster.name", "docker-cluster").build();
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return client;
	}
}
