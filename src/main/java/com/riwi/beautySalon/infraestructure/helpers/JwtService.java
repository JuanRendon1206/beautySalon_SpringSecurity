package com.riwi.beautySalon.infraestructure.helpers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//de aca debe de venir: SecretKey
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.riwi.beautySalon.domain.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    /*1. Crear la firma o clav
     * esta tiene que tener minimo 32 caracteres normales para concertir a base64
     * esta es la clave convertida: mi super clave secreta secreta secreta, mi super clave secreta secreta secreta
     */
    private final String SECRET_KEY = "bWkgc3VwZXIgY2xhdmUgc2VjcmV0YSBzZWNyZXRhIHNlY3JldGEsIG1pIHN1cGVyIGNsYXZlIHNlY3JldGEgc2VjcmV0YSBzZWNyZXRh";

    //2. METODO PARA ENCRIPTAR LA CLAVE SECRETA
    public SecretKey getKey(){

        //Convertir la llave a bytes
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        //Retornamos la llave cifrada
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //3. Construit el JWT
    public String getToken(Map<String, Object> claims, User user){

        return Jwts.builder()
                    .claims(claims) //Agrego el cuerpo del jwt
                    .subject(user.getUsername()) //Agrego de quien es el JWT
                    .issuedAt(new Date(System.currentTimeMillis()))//Fecha de creacion
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) //Le decimos que dura un dia, osea 24 horas, esta es la fecha de expiracion
                    .signWith(this.getKey()) //Firmar el token
                    .compact();
    }

    //4. Metodo para obteneer el JWT, utilizaremos sobreescritura, por eso se llama igual

    public String getToken(User user){

        //Crear el map de claims
        /*
         * Recordemos que el HashMap es como un objeto literal de js
         * {
         *   "KEY": "VALUE"
         * }
         */

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());

        //como el es inteligente mira la cantidad de argumentos que le pasamos al sobre escribir para saber cual de los metodos debe de utilizar
        return getToken(claims, user);
    }
}
