package com.kg.backend.service.pk;

import org.springframework.stereotype.Service;

@Service
public interface ReceiveBotMoveService {
    String receiveBotMove(Integer userId, Integer direction);
}

