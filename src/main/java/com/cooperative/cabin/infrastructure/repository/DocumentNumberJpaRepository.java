package com.cooperative.cabin.infrastructure.repository;

import com.cooperative.cabin.domain.model.DocumentNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentNumberJpaRepository extends JpaRepository<DocumentNumber, Long> {

    /**
     * Busca un número de documento por su valor
     * 
     * @param documentNumber el número de documento a buscar
     * @return Optional con el DocumentNumber si existe
     */
    Optional<DocumentNumber> findByDocumentNumber(String documentNumber);

    /**
     * Verifica si un número de documento existe y está activo
     * 
     * @param documentNumber el número de documento a verificar
     * @return true si existe y está activo, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM DocumentNumber d " +
            "WHERE d.documentNumber = :documentNumber AND d.status = 'ACTIVE'")
    boolean existsByDocumentNumberAndActive(@Param("documentNumber") String documentNumber);

    /**
     * Busca un número de documento activo por su valor
     * 
     * @param documentNumber el número de documento a buscar
     * @return Optional con el DocumentNumber si existe y está activo
     */
    @Query("SELECT d FROM DocumentNumber d " +
            "WHERE d.documentNumber = :documentNumber AND d.status = 'ACTIVE'")
    Optional<DocumentNumber> findActiveByDocumentNumber(@Param("documentNumber") String documentNumber);

    /**
     * Verifica si un número de documento existe (independientemente del estado)
     * 
     * @param documentNumber el número de documento a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByDocumentNumber(String documentNumber);
}
