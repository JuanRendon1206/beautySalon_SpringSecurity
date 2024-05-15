package com.riwi.beautySalon.infraestructure.helpers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

//Exrendemos de: OncePerRequestFilter para crear filtros personalizados
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        //1. Obtener el token de el request
        String token = this.getTokenFromRequest(request);

        //2. Si el token es nulo entonces que siga con los filtros de security
        if (token == null) {
            filterChain.doFilter(request, response);
            return ;
        }

        //En caso de que el token no sea nulo, seguimos con nuestros filtros personalizados...
        //3. Validar sacando el UserName de el token con el metodo creado en el JwtService
        String userName = this.jwtService.getUsernameFromToken(token);

        //4. Si no lo encuentra en el contexto de spring
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Esto quiere decir que no tiene una sesion activa, asi que vamos a activarsela

            //5. Obtener la info de el usuario}
            UserDetails user = userDetailsService.loadUserByUsername(userName);

            if (this.jwtService.isTokenValid(token, user)) {
                
                //Creamos la autenticacion y la registramos en el contexto de spring
                //El var es para no tener que colocar que la variable es de tipo UsernamePasswordAuthenticationToken, si no que al hacer el new UsernamePasswordAuthenticationToken, el se da cuenta y la asigna automaticamente
                var authToken  = new UsernamePasswordAuthenticationToken(userName, null, user.getAuthorities());

                /*
                 * setDetails: Establece detalles adicionales de la autenticacion,
                 * como la direccion IP y la sesión de donde se realiza la solicitud
                 */
                authToken.setDetails(new WebAuthenticationDetails(request));
                
                //Guardar la autenticacion ene l contexto de spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //Si pasa estos filtros le damos el permiso para continuar
        filterChain.doFilter(request, response);
    }

    public String getTokenFromRequest(HttpServletRequest request){
        
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Si el token tiene una longuitud y a demas comienza con la palabra Bearer
        if (StringUtils.hasLength(token) && token.startsWith("Bearer ")) {
            return token.substring(7);        
        }

        return null;
    }
    
}