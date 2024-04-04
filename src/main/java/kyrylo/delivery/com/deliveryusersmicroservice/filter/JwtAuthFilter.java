package kyrylo.delivery.com.deliveryusersmicroservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import kyrylo.delivery.com.deliveryusersmicroservice.services.JwtService;
import kyrylo.delivery.com.deliveryusersmicroservice.userDetails.DeliveryUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final DeliveryUserDetailsService userDetailsService;
    private final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private final String INTERNAL_SECRET_VALUE = "expected-value";
    private static final Logger logger = LogManager.getLogger(JwtAuthFilter.class);
    @Autowired
    public JwtAuthFilter(JwtService jwtService, DeliveryUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String internalSecret = request.getHeader(INTERNAL_SECRET_HEADER);

        logger.info("Internal Secret Header -> {}", internalSecret);

        if (INTERNAL_SECRET_VALUE.equals(internalSecret)) {
            Authentication internalAuthentication = new UsernamePasswordAuthenticationToken("internal", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(internalAuthentication);

            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Checking JWT Token");

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
