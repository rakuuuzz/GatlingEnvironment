package main.java.RedisDataLoader;

import com.redis.R;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class RedisLoader {

    private static class RedisConfig {
        String host;
        int port;
        String password;
        String username;
    }

    public static void publishToRedis(Map<Object, Object> data, Map<Object, Object> config) {
        RedisConfig redisConfig = parseRedisConfig(config);

        try (Jedis jedis = new Jedis(redisConfig.host, redisConfig.port)) {
//           Skip auth
//            if (redisConfig.password != null) {
//                jedis.auth(redisConfig.password);
//            }
            if (jedis.ping() != null)
                System.out.println("Successfully connect to Redis");
            else
                throw new RuntimeException("Error while connect to Redis");

            data.forEach((key, value) -> {
                jedis.set((String) key, value.toString());
            });
            data.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (Exception e) {
            throw new RuntimeException("Error while publish to Redis", e);
        }
    }

    private static RedisConfig parseRedisConfig(Map<Object, Object> config) {
        return new RedisConfig() {{
            host = (String) config.get("redis.host");
            port = Integer.parseInt((String) config.get("redis.port"));
            password = (String) config.get("redis.password");
            username = (String) config.get("redis.username");
        }};
    }

}
