package com.salon.manager.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LoggerServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(LoggerServiceImpl.class);

    public void logAccion(String accion, String detalle, Long usuarioId) {
        logger.info("[{}] ACCIÓN: {} | DETALLE: {} | USUARIO: {}",
                LocalDateTime.now(), accion, detalle, usuarioId);
    }

    public void logError(String error, Long usuarioId) {
        logger.error("[{}] ERROR: {} | USUARIO: {}",
                LocalDateTime.now(), error, usuarioId);
    }
}
