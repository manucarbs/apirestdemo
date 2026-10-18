package edu.sv.ues.dam235.apirestdemo.utilities;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {
    private final Set<String> revoked = ConcurrentHashMap.newKeySet();

    public void revoke(String token) {
        revoked.add(token);
    }

    public boolean isRevoked(String token) {
        return token != null && revoked.contains(token);
    }
}