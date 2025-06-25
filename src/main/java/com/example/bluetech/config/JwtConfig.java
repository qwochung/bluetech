package com.example.bluetech.config;

import com.example.bluetech.entity.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.StringJoiner;

@Configuration
@Slf4j
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${access-expiration}")
    private Long accessExpiration;

    @Value("${refresh-expiration}")
    private Long refreshExpiration;
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(secretKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public String generateToken(User user, String type){
        Instant now = Instant.now();
        Instant validity = now.plusSeconds(type.equals("access") ? accessExpiration : refreshExpiration);


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer(user.getEmail())
                .expiresAt(validity)
                .subject(user.getEmail())
                .claim("scope" , buildScope(user))
                .build();


        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder().encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    };

    private String buildScope(User user) {
        StringJoiner scope = new StringJoiner(" ");

        log.info("scope: {}", scope.toString());
        return scope.toString();
    }

    public Jwt checkValidRefreshToken(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                this.getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            System.out.println(">>> JWT refresh error: " + e.getMessage());
            throw e;
        }
    }

}
