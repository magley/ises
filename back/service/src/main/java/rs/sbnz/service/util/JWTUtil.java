package rs.sbnz.service.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {
    // TODO: Externalize secret.
    private static String secret = "QVlFX0dVVk5PUl9USElTX0lTX0FfV0VFX0JJVF9VTlNBRkVfSU5OSVQ=";

    public String generateJWT(String username, Long id, String role) {   
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("role", role);
        claims.put("email", username);
        return Jwts.builder()
            .setIssuer("ises")
            .setClaims(claims)
            .setSubject(username)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public String getUsernameFromJWT(String jwt) {
        return getAllClaims(jwt).getSubject();
    }

    private Claims getAllClaims(String jwt) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
    }

    public String getJwtFromHeader(String header) {
        if (header == null || !header.contains("Bearer")) {
            return null;
        }

        try {
            String jwt = header.substring(header.indexOf("Bearer ") + 7);
            if (jwt.isEmpty()) {
                return null;
            }
            return jwt;
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
