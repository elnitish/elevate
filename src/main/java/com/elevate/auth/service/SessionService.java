package com.elevate.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.entity.SessionToken;
import com.elevate.auth.repository.SessionTokenRepository;

@Service
public class SessionService {

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    public boolean isValidSession(String sessionToken){
        Optional<SessionToken> token = sessionTokenRepository.findBySessionToken(sessionToken);
        return token.isPresent();
    }

    public SessionToken returnTenantAndUser(String sessionToken){
        Optional<SessionToken> token = sessionTokenRepository.findBySessionToken(sessionToken);
        if(token.isPresent()){
            return token.get();
        }
        return null;
    }

}
