package com.kg.backend.service.impl.user.account;

import com.kg.backend.service.user.account.RegisterService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        return null;
    }
}
