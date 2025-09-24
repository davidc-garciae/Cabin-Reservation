package com.cooperative.cabin.infrastructure.config;

import com.cooperative.cabin.domain.model.Cabin;
import com.cooperative.cabin.infrastructure.repository.CabinJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Profile("!test") // No ejecutar en tests
public class DataInitializer {

    @Bean
    public CommandLineRunner initCabins(CabinJpaRepository cabinRepository) {
        return args -> {
            // Solo crear cabañas si no existen
            if (cabinRepository.count() == 0) {
                createSampleCabins(cabinRepository);
            }
        };
    }

    private void createSampleCabins(CabinJpaRepository cabinRepository) {
        // Cabaña 1: Cabaña del Lago
        Cabin cabin1 = new Cabin(
                "Cabaña del Lago",
                "Hermosa cabaña con vista al lago, ideal para familias. Incluye terraza privada y acceso directo al lago.",
                6,
                3,
                2,
                BigDecimal.valueOf(150.00),
                6,
                "{\"wifi\": true, \"parking\": true, \"kitchen\": true, \"lake_view\": true, \"terrace\": true}",
                "{\"address\": \"Lago del Sol 123, Villa del Lago\", \"coordinates\": {\"lat\": 40.7128, \"lng\": -74.0060}}");
        cabinRepository.save(cabin1);

        // Cabaña 2: Cabaña del Bosque
        Cabin cabin2 = new Cabin(
                "Cabaña del Bosque",
                "Acogedora cabaña en el corazón del bosque, perfecta para una escapada romántica.",
                4,
                2,
                1,
                BigDecimal.valueOf(120.00),
                4,
                "{\"wifi\": true, \"parking\": true, \"fireplace\": true, \"forest_view\": true}",
                "{\"address\": \"Bosque Encantado 456, Villa del Bosque\", \"coordinates\": {\"lat\": 40.7589, \"lng\": -73.9851}}");
        cabinRepository.save(cabin2);

        // Cabaña 3: Cabaña de Montaña
        Cabin cabin3 = new Cabin(
                "Cabaña de Montaña",
                "Cabaña de lujo en la montaña con vistas espectaculares y todas las comodidades.",
                8,
                4,
                3,
                BigDecimal.valueOf(250.00),
                8,
                "{\"wifi\": true, \"parking\": true, \"kitchen\": true, \"hot_tub\": true, \"mountain_view\": true, \"fireplace\": true}",
                "{\"address\": \"Cumbre Alta 789, Villa de Montaña\", \"coordinates\": {\"lat\": 40.7831, \"lng\": -73.9712}}");
        cabinRepository.save(cabin3);

        // Cabaña 4: Cabaña Rústica
        Cabin cabin4 = new Cabin(
                "Cabaña Rústica",
                "Cabaña tradicional con encanto rústico, ideal para desconectar y disfrutar de la naturaleza.",
                2,
                1,
                1,
                BigDecimal.valueOf(80.00),
                2,
                "{\"parking\": true, \"fireplace\": true, \"rustic_charm\": true}",
                "{\"address\": \"Camino Rural 321, Villa Rústica\", \"coordinates\": {\"lat\": 40.7505, \"lng\": -73.9934}}");
        cabinRepository.save(cabin4);

        // Cabaña 5: Cabaña Familiar
        Cabin cabin5 = new Cabin(
                "Cabaña Familiar",
                "Amplia cabaña diseñada para familias grandes, con múltiples espacios de entretenimiento.",
                10,
                5,
                4,
                BigDecimal.valueOf(300.00),
                10,
                "{\"wifi\": true, \"parking\": true, \"kitchen\": true, \"game_room\": true, \"pool\": true, \"garden\": true}",
                "{\"address\": \"Avenida Familiar 654, Villa Familiar\", \"coordinates\": {\"lat\": 40.7282, \"lng\": -73.9942}}");
        cabinRepository.save(cabin5);

        System.out.println("✅ Cabañas de prueba creadas exitosamente!");
    }
}
