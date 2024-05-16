package com.riwi.beautySalon.infraestructure.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.riwi.beautySalon.api.dto.request.LoginReq;
import com.riwi.beautySalon.api.dto.request.RegisterEmployeeReq;
import com.riwi.beautySalon.api.dto.request.RegisterReq;
import com.riwi.beautySalon.api.dto.response.AuthResp;
import com.riwi.beautySalon.domain.entities.Employee;
import com.riwi.beautySalon.domain.entities.User;
import com.riwi.beautySalon.domain.repositories.ClientRepository;
import com.riwi.beautySalon.domain.repositories.EmployeeRepository;
import com.riwi.beautySalon.domain.repositories.UserRepository;
import com.riwi.beautySalon.infraestructure.abstract_service.IAuthService;
import com.riwi.beautySalon.infraestructure.helpers.JwtService;
import com.riwi.beautySalon.utils.enums.Role;
import com.riwi.beautySalon.utils.exceptions.BadRequestException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService{

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtService jwtService;

    //Esto lo usaremos para encriptar la contraseña para que el desarrollador no vea la contraseña de los clientes
    @Autowired
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final EmployeeRepository employeeRepository;

    @Autowired
    private final ClientRepository clientRepository;

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

    @Override
    public AuthResp registerEmployee(RegisterEmployeeReq request) {
        
        User user = validateUser(request.getUserName(), request.getPassword(), Role.EMPLOYEE);

        this.userRepository.save(user);

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(request.getRole())
                .user(user)
                .build();

        this.employeeRepository.save(employee);

        return AuthResp
                .builder()
                .message("Empleado registrado correctamente")
                .token(this.jwtService.getToken(user))
                .build();

        //Esto queda protegido
    }

    private User validateUser(String username, String password, Role role){

        /*1. Validar que el usuario no existe */
        User exist = this.findUser(username);

        if (exist != null) {
            throw new BadRequestException("El usuario ya existe");
        }

        // Contruir el usuario
        return User.builder()
                .userName(username)
                .password(this.passwordEncoder.encode(password))
                .role(role)
                .build();
    }
}
