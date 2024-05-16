package com.example.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public class ResponseUtils {

    /**
     * 返回response错误信息
     *
     * @param exchange ServerWebExchange
     * @param message  异常信息
     * @param status   HttpStatus
     * @return Mono<Void>
     */
    public static Mono<Void> error(ServerWebExchange exchange, String message, HttpStatus status) {
        return Mono.defer(() -> {
            byte[] bytes;
            try {
                bytes = new ObjectMapper().writeValueAsBytes(new HashMap<String, Object>() {{
                    put("status", status.value());
                    put("message", message);
                }});
            } catch (JsonProcessingException e) {
                bytes = new byte[0];
            }
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(status);
            response.getHeaders().remove("Authorization");
            response.getHeaders().set("remove-token", "1");
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        });
    }

}
