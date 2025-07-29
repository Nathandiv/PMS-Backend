package pmsBackend.pmsBackend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pmsBackend.pmsBackend.entity.User;
import pmsBackend.pmsBackend.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors; // Import Collectors for older Java versions if needed

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private final UserRepository userRepository;

    public List<GrantedAuthority> extractAuthorities(String token) {
        var claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        // The "authorities" claim is typically a List of Strings in the JWT.
        // We cast it to List<String> directly for better type safety.
        // If it's possible for 'authorities' to be something else, you might need
        // more robust error handling or type checking here.
        var rawAuthorities = (List<String>) claims.get("authorities");

        return rawAuthorities.stream()
                .map(SimpleGrantedAuthority::new) // Converts String role to SimpleGrantedAuthority
                .collect(Collectors.toList()); // Collects into a List<GrantedAuthority>
        // Using toList() directly is fine for Java 16+ but this is more explicit.
        // If you use toList(), ensure the return type matches the stream's element type, or cast.
    }

    public String generateToken(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("authorities", List.of("ROLE_" + user.getRole().name()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && validateToken(token);
    }
}