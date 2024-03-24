package kyrylo.delivery.com.deliveryusersmicroservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Base64;
import java.util.StringTokenizer;

@Component
public class JwtService {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsernameWithoutValidation(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty or null");
        }

        StringTokenizer tokenizer = new StringTokenizer(token, ".");
        String header = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        String payload = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;

        if (payload == null) {
            throw new IllegalArgumentException("Invalid JWT token.");
        }

        String payloadDecoded = new String(Base64.getUrlDecoder().decode(payload));

        String username = extractFieldFromPayload(payloadDecoded, "sub");
        return username;
    }

    private String extractFieldFromPayload(String payload, String fieldName) {
        String searchFor = "\"" + fieldName + "\":\"";
        int startPos = payload.indexOf(searchFor);
        if (startPos < 0) {
            return null;
        }
        startPos += searchFor.length();
        int endPos = payload.indexOf("\"", startPos);
        if (endPos < 0) {
            return null;
        }

        return payload.substring(startPos, endPos);
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 600))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
