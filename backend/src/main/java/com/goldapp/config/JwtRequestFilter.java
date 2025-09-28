package com.goldapp.config;

import com.goldapp.service.UserService;
import org.springframework.lang.NonNull;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger debugLogger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader("Authorization");
    debugLogger.info("JWT Filter: Authorization header: {}", requestTokenHeader);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            debugLogger.info("JWT Filter: Extracted token: {}", jwtToken);
            try {
                username = jwtTokenUtil.extractUsername(jwtToken);
                debugLogger.info("JWT Filter: Extracted username: {}", username);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
                debugLogger.error("JWT Filter: Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
                debugLogger.error("JWT Filter: JWT Token has expired", e);
            }
        } else {
            debugLogger.warn("JWT Filter: No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            debugLogger.info("JWT Filter: Loading user details for username: {}", username);
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                debugLogger.info("JWT Filter: Token validated for user: {}", username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                debugLogger.warn("JWT Filter: Token validation failed for user: {}", username);
            }
        } else {
            debugLogger.warn("JWT Filter: Username is null or authentication already set");
        }
        chain.doFilter(request, response);
    }
}