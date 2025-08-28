package com.furkanerd.hr_management_system.security;

import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.service.EmployeeService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final EmployeeService employeeService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,EmployeeService employeeService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.employeeService = employeeService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String authorizationHeader = request.getHeader("Authorization");

        String token=null;
        String username=null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token=authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            }catch(Exception e) {
                logger.error("Jwt token parsing error : " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(jwtUtil.validateToken(token,userDetails)) {

                try {
                    Employee employee = employeeService.getEmployeeEntityByEmail(username);

                    if (employee.isMustChangePassword()) {
                        String requestURI = request.getRequestURI();
                        if (!requestURI.equals("/api/v1/auth/change-password")) {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Password change required\",\"message\":\"You must change your password before accessing other endpoints\"}");
                            return;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error checking password change requirement: " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }


                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
