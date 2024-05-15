package com.riwi.beautySalon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.riwi.beautySalon.domain.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ApplicationConfig {

    //1. Inyectamos el repo de el user
    @Autowired
    private final UserRepository userRepository;

    //2. Obtener la config de springboot security por defecto}
    // AuthenticationManager: Permite el manejo de el usuario en toda la app
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }


    /*
     * Guarda la contraseña de el usuario y el nombre de este, y es el que hace la comparacion de las dos contraseñas, y guardaremos el tipo de encriptacion de esta
     * 
     * El DaoAuthenticationProvider, se usa muy comunmente para proveer datos a nuestra app
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        //Puedo usar var y el interfiere para saber de que tipo es 
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        authenticationProvider.setUserDetailsService(this.userDetailsService());

        return authenticationProvider();
    }


    //Este es un metodo que se encargara de traer los detalles de el usuario durante la autenticacion; para utilizarlo en authenticationProvider .
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> this.userRepository.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    /*
     * Define un Bean para PasswordEncoder, este es utilizado para encriptar y desencriptar las contraseñas en la app;
     * Retorna una instancia de BCryptPasswordEncoder, es un metodo cifrado o hash fuertemente y ampliamente utilizado
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
