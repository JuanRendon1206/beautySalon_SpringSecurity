package com.riwi.beautySalon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.riwi.beautySalon.infraestructure.helpers.JwtFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity //Activar Spring security
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    private final AuthenticationProvider authenticationProvider;
    
    @Autowired
    private final JwtFilter jwtFilter;


    //Como es constante la ponemos en mayusculas por buenas practicas
    //Crear rutas publicas para la config, el ** indica que todo lo que contenga el auth lo permitira como publico
    private final String[] PUBLIC_RESOURCES = { "/services/public/get", "/auth/**"};
    
    /*
    - @Bean anotation: Esta anotacion le indica a spring boot el objeto retornado
        por el metodo debe ser registrado como un bean en el contexto de spring(lata)

    - SecurityFilterChain es para condigurar el spring security sin importar la config q tiene por defecto

    - HttpSecurity es para config el request y response por algo que ya trae
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        //throws Exception es como tener un try cath por si algo falla
        return http
            .csrf(csrf -> csrf.disable()) //Desabilitar proteccion csrf -> ya que es de Statelest.
            .authorizeHttpRequests(authRequest -> authRequest 
                .requestMatchers(PUBLIC_RESOURCES).permitAll() //rutas publicas
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //Agregarmos el proveedor de autenticación
            .authenticationProvider(authenticationProvider)
            //Agregar el filtro personalizado antes del filtro de spring security
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
