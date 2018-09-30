package com.lequ.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
@PropertySource("classpath:application.properties")
public class RedisClusterConfig {
	@Autowired
	private Environment env;

	public RedisClusterConfiguration clusterConfiguration() {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("spring.redis.cluster.nodes", env.getProperty("spring.redis.cluster.nodes"));
		source.put("spring.redis.cluster.max-redirects",
				Integer.valueOf(env.getProperty("spring.redis.cluster.max-redirects")));
		source.put("spring.redis.cluster.timeout", Long.valueOf(env.getProperty("spring.redis.cluster.timeout")));

		MapPropertySource mps = new MapPropertySource("RedisClusterConfiguration", source);
		RedisClusterConfiguration rcc = new RedisClusterConfiguration(mps);
		return rcc;
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(600)).disableCachingNullValues();
		return cacheConfig;
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(clusterConfiguration());
		connectionFactory.afterPropertiesSet();
		return connectionFactory;
	}

	@Bean
	public RedisCacheManager cacheManager() {
		RedisCacheManager rcm = RedisCacheManager.builder(redisConnectionFactory()).cacheDefaults(cacheConfiguration())
				.build();
		return rcm;
	}
}
