package org.example.bank.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     *  Lettuce Client
     *      - Java 환경에서 Redis를 이용하기 위한 Redis 클라이언트
     *      - spring-boot-starter-data-redis 라이브러리 내에 내장되어 있음.
     *      - 멀티 스레드 환경 지원, 비동기 명령 실행 가능
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 연결을 위한 Connection 생성
        return new LettuceConnectionFactory(host, port);
    }

    /**
     *  Redis Template
     *      - 데이터 조작을 추상화하고 자동화해 주는 역할을 수행하여 코드의 중복을 줄여주는 클래스
     *      - String, List, Set, Sorted Set, Hash 등 다양한 Redis 데이터 구조를 쉽게 다룰 수 있음.
     *      - 연결 관리, 직렬화/역직렬화, 예외 처리 등을 처리해 주며, 기본적인 Redis 데이터 액세스 코드를 간결하게 만들어줌.
     * @return RedisTemplate<String, Object>
     */
    @Bean
    // Redis 데이터 처리를 위한 템플릿 구성
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Redis 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key-Value 형태로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash Key-Value 형태로 직렬화
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 기본 직렬화
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    /**
     *  단일 데이터에 접근하여 다양한 연산을 수행
     * @return ValueOperations<String, Object>
     */
    public ValueOperations<String, Object> getValueOperations() {
        return this.redisTemplate().opsForValue();
    }

    /**
     *  리스트에 접근하여 다양한 연산을 수행
     * @return ListOperations<String, Object>
     */
    public ListOperations<String, Object> getListOperations() {
        return this.redisTemplate().opsForList();
    }

    /**
     *  Redis 작업 중 삽입, 갱신, 삭제에 대해 처리 및 예외를 수행
     * @param operation
     * @return
     */
    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            System.out.println("Redis 작업 중 오류 발생 :: " + e.getMessage());
            return 0;
        }
    }

}