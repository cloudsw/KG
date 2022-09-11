package com.kg.backend.consumer.utils;

import com.kg.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    public static Integer getUserId(String token) {
        int useId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            useId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return useId;

    }
}
