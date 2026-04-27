package com.salon.manager.application.service;

import com.salon.manager.application.dto.response.CitaResponse;
import com.salon.manager.application.dto.response.HuecoResponse;
import com.salon.manager.common.enums.EstadoCita;
import com.salon.manager.common.exception.ResourceNotFoundException;
import com.salon.manager.domain.model.Cita;
import com.salon.manager.domain.model.Servicio;
import com.salon.manager.domain.repository.CitaRepository;
import com.salon.manager.domain.repository.ServicioRepository;
import com.salon.manager.domain.repository.UsuarioRepository;
import com.salon.manager.domain.service.ValidadorSolapes;
import com.salon.manager.infrastructure.persistence.mapper.CitaMapper;
import com.salon.manager.logger.LoggerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaAppService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final ValidadorSolapes validadorSolapes;
    private final LoggerServiceImpl loggerService;

    @Transactional
    public CitaResponse crearCita(Long clienteId, Long profesionalId,
                                  Long servicioId, LocalDateTime inicio,
                                  String alergias,
                                  String observaciones) {

        loggerService.logAccion("CREAR_CITA",
                "Creando cita - Cliente: " + clienteId + ", Profesional: " + profesionalId,
                clienteId);

        usuarioRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        usuarioRepository.buscarPorId(profesionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado"));
        servicioRepository.buscarPorId(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        LocalDateTime fin = inicio.plusMinutes(30);
        validadorSolapes.validar(profesionalId, inicio, fin);

        Cita cita = new Cita();
        cita.setClienteId(clienteId);
        cita.setProfesionalId(profesionalId);
        cita.setServicioId(servicioId);
        cita.setFechaInicio(inicio);
        cita.setFechaFin(fin);
        cita.setAlergias(alergias);
        cita.setObservaciones(observaciones);
        cita.setAtendido(false);
        cita.setFechaCreacion(LocalDateTime.now());

        Cita guardada = citaRepository.guardar(cita);

        loggerService.logAccion("CITA_CREADA",
                "Cita creada con ID: " + guardada.getId(),
                clienteId);

        return enriquecerResponse(guardada);
    }

    @Transactional
    public CitaResponse marcarAtendida(Long citaId) {

        loggerService.logAccion("MARCAR_ATENDIDA",
                "Marcando cita como atendida",
                citaId);

        Cita cita = citaRepository.buscarPorId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        cita.marcarAtendida();
        Cita guardada = citaRepository.guardar(cita);

        loggerService.logAccion("CITA_ATENDIDA",
                "Cita marcada como atendida",
                citaId);

        return enriquecerResponse(guardada);
    }

    @Transactional
    public CitaResponse cancelarCita(Long citaId) {
        Cita cita = citaRepository.buscarPorId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        //Validar que se pueda cancelar la cita
        if (cita.getEstado() == EstadoCita.REALIZADA) {
            throw new RuntimeException("No se puede cancelar una cita ya realizada");
        }

        cita.cancelar();

        Cita guardada = citaRepository.guardar(cita);

        loggerService.logAccion("CITA_CANCELADA",
                "Cita cancelada por el sistema",
                cita.getClienteId());

        return enriquecerResponse(guardada);
    }

    @Transactional
    public CitaResponse confirmarCita(Long citaId) {
        Cita cita = citaRepository.buscarPorId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        //Validar que no sea una fecha pasada
        if (cita.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede confirmar una cita con fecha pasada");
        }

        cita.confirmar();

        Cita guardada = citaRepository.guardar(cita);

        loggerService.logAccion("CITA_CONFIRMADA",
                "Cita confirmada por profesional",
                cita.getClienteId());

        return enriquecerResponse(guardada);
    }

    public List<CitaResponse> obtenerPorCliente(Long clienteId) {
        return citaRepository.buscarPorCliente(clienteId).stream()
                .map(this::enriquecerResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> obtenerPorProfesional(Long profesionalId) {
        return citaRepository.buscarPorProfesional(profesionalId).stream()
                .map(this::enriquecerResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> obtenerPorFecha(LocalDateTime fecha) {
        return citaRepository.buscarPorFecha(fecha).stream()
                .map(this::enriquecerResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarCita(Long citaId) {
        citaRepository.eliminar(citaId);
    }

    //Con los id busco los datos del usuario, proesional y servicio.
    private CitaResponse enriquecerResponse(Cita cita) {
        CitaResponse response = CitaMapper.toResponse(cita);

        usuarioRepository.buscarPorId(cita.getClienteId())
                .ifPresent(u -> response.setClienteNombre(u.getNombre()));

        usuarioRepository.buscarPorId(cita.getProfesionalId())
                .ifPresent(u -> response.setProfesionalNombre(u.getNombre()));

        servicioRepository.buscarPorId(cita.getServicioId())
                .ifPresent(s -> response.setServicioNombre(s.getNombre()));

        return response;
    }

    public List<HuecoResponse> calcularHuecosDisponibles(
            LocalDate fecha,
            Long profesionalId,
            Long servicioId) {

        //Validar que la fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden seleccionar fechas pasadas");
        }

        //Obtener duración del servicio
        Servicio servicio = servicioRepository.buscarPorId(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        int duracionServicio = servicio.getDuracionMin();


        //Generar todos los huecos del día (9:00-14:00 y 17:00-20:00 es el horario)
        List<HuecoResponse> huecos = new ArrayList<>();

        //Mañana: 9:00 - 14:00
        huecos.addAll(generarHuecosParaRango(
                fecha, 9, 0, 14, 15, profesionalId
        ));

        //Tarde: 17:00 - 20:00
        huecos.addAll(generarHuecosParaRango(
                fecha, 17, 0, 20, 15, profesionalId
        ));

        return huecos;
    }

    private List<HuecoResponse> generarHuecosParaRango(
            LocalDate fecha,
            int horaInicio,
            int minutoInicio,
            int horaFin,
            int minutoFin,
            Long profesionalId) {

        List<HuecoResponse> huecos = new ArrayList<>();

        LocalDateTime inicio = LocalDateTime.of(fecha, LocalTime.of(horaInicio, minutoInicio));
        LocalDateTime fin = LocalDateTime.of(fecha, LocalTime.of(horaFin, minutoFin));
        List<Cita> citasExistentes = citaRepository.buscarPorProfesionalYFecha(profesionalId, fecha);

        LocalDateTime horaActual = inicio;

        while (!horaActual.isAfter(fin)) {
            String horaStr = String.format("%02d:%02d",
                    horaActual.getHour(),
                    horaActual.getMinute()
            );

            //Verificar si este hueco de 15 min se solapa con alguna cita existente
            LocalDateTime finalHoraActual = horaActual;
            boolean ocupado = citasExistentes.stream()
                    .anyMatch(cita -> {
                        //Inicio y fin de la cita existente
                        LocalDateTime inicioCita = cita.getFechaInicio();

                        //Obtener duración del servicio de la cita existente
                        int duracionCitaExistente = servicioRepository.buscarPorId(cita.getServicioId())
                                .map(Servicio::getDuracionMin)
                                .orElse(30);  //Default 30 min si no encuentra

                        LocalDateTime finCita = inicioCita.plusMinutes(duracionCitaExistente);

                        //El hueco actual es de 15 minutos
                        LocalDateTime finHueco = finalHoraActual.plusMinutes(15);

                        //Verificar solape:
                        //Un hueco se solapa si empieza antes de que termine la cita
                        //Y termina después de que empiece la cita
                        return finalHoraActual.isBefore(finCita) && finHueco.isAfter(inicioCita);
                    });

            huecos.add(new HuecoResponse(
                    horaStr,
                    !ocupado,
                    ocupado ? "OCUPADO" : "LIBRE"
            ));

            //Incrementar de 15 en 15 minutos
            horaActual = horaActual.plusMinutes(15);
        }


        return huecos;
    }


}
