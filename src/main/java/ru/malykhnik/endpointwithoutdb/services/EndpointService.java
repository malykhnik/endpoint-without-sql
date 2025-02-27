package ru.malykhnik.endpointwithoutdb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.malykhnik.endpointwithoutdb.dto.*;
import ru.malykhnik.endpointwithoutdb.dto.Error;
import ru.malykhnik.endpointwithoutdb.util.TokenUtil;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EndpointService {

    private final TokenUtil util;
    private String cachedToken;

    @Value("${auth.login}")
    String login;
    @Value("${auth.password_hash}")
    String passwordHash;

    public Response<Message, Error> checkServiceStatus(String token) {
        ArrayList<ServiceDto> services = new ArrayList<>();
        ServiceDto serviceDto = new ServiceDto("endpoint", "active");
        services.add(serviceDto);

        if(!validateToken(token)) {
            var err = new Error("Wrong token");
            return new Response<>(null, err);
        }

        cachedToken = util.generateToken();
        var message = new Message(cachedToken, services);
        return new Response<>(message, null);
    }

    public boolean validateToken(String token) {
        return cachedToken.equals(token);
    }

    public Response getToken(AuthData auth) {
        if (!validateAuth(auth)) {
            var err = new Error("Wrong auth data");
            return new Response<>(null, err);
        }
        cachedToken = util.generateToken();
        var token = new TokenData(cachedToken);
        return new Response<>(token, null);
    }

    public boolean validateAuth(AuthData auth) {
        return auth.login().equals(this.login) && auth.password_hash().equals(this.passwordHash);
    }
}
