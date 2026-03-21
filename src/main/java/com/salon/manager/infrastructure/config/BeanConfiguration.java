package com.salon.manager.infrastructure.config;

import com.salon.manager.domain.repository.CitaRepository;
import com.salon.manager.domain.service.ValidadorSolapes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* Uso la clase BeanConfiguration ya que con esta clase le digo a Spring que me cree un objeto y regístralo como un bean para que pueda ser inyectado en otras clases.
 * En este caso, me crea un bean de tipo ValidadorSolapes.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ValidadorSolapes validadorSolapes(CitaRepository citaRepository) {
        return new ValidadorSolapes(citaRepository);
    }
}
