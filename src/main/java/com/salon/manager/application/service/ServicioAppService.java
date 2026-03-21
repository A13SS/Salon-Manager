package com.salon.manager.application.service;

import com.salon.manager.domain.repository.ServicioRepository;
import com.salon.manager.infrastructure.persistence.mapper.ServicioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioAppService {

    private final ServicioRepository servicioRepository;

    public List<ServicioMapper.ServicioResponse> listarActivos() {
        return servicioRepository.listarActivos().stream()
                .map(ServicioMapper::toResponse)
                .collect(Collectors.toList());
    }
}
