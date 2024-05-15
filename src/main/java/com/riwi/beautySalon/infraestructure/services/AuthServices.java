package com.riwi.beautySalon.infraestructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.riwi.beautySalon.api.dto.request.LoginReq;
import com.riwi.beautySalon.api.dto.request.RegisterReq;
import com.riwi.beautySalon.api.dto.response.AuthResp;
import com.riwi.beautySalon.domain.entities.User;
import com.riwi.beautySalon.domain.repositories.UserRepository;
import com.riwi.beautySalon.infraestructure.abstract_service.IAuthService;
import com.riwi.beautySalon.infraestructure.helpers.JwtService;
import com.riwi.beautySalon.utils.enums.Role;
import com.riwi.beautySalon.utils.exceptions.BadRequestException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServices implements IAuthService{

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtService jwtService;

    //Esto lo usaremos para encriptar la contraseña para que el desarrollador no vea la contraseña de los clientes
    @Autowired
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResp login(LoginReq request) {
        
        try {
            //Autenticarnos en la app
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));

        } catch (Exception e) {
            throw new BadRequestException("Credenciales incorrectas");
        }

        User user = this.findUser(request.getUserName());

        return AuthResp.builder()
                .message("Autenticado correctamente")
                .token(this.jwtService.getToken(user))
                .build();
    }

    @Override
    public AuthResp register(RegisterReq request) {

        //1. Validar que el usuario NO exista
        User exist = this.findUser(request.getUserName());
        if (exist != null) {
            throw new BadRequestException("El usuario ya existe");
        }

        /*Construir el nuevo usuario */
        User user = User.builder()
                        .userName(request.getUserName())
                        //Guardar la contraseña codificada 
                        .password(this.passwordEncoder.encode(request.getPassword()))
                        .role(Role.CLIENT)
                        .build();

        /*Guardamos el user en la DB */
        user = this.userRepository.save(user);

        return AuthResp.builder()
                            .message("Registro completado exitosamente")
                            .token(this.jwtService.getToken(user))
                            .build();
    }
    
    private User findUser(String username){

        return this.userRepository.findByUserName(username)
            .orElse(null);
    }
}
