package kyrylo.delivery.com.deliveryusersmicroservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import kyrylo.delivery.com.deliveryusersmicroservice.services.JwtService;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Simulating successful authentication");

        // Створюємо токен аутентифікації з "пустим" користувачем і надаємо йому базову роль
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Встановлюємо аутентифікацію в контексті безпеки
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Продовжуємо ланцюг фільтрів
        filterChain.doFilter(request, response);

//        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            logger.info("Not found token");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String token = authorizationHeader.substring(7);
//
//        filterChain.doFilter(request, response);
//
//        try {
//            jwtService.validateToken(token);
//            logger.info("Token -> {}", token);
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Unauthorized: Token validation failed");
//        }
    }
}
