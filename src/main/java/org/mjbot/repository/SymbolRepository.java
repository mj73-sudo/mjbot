package org.mjbot.repository;

import java.util.List;
import org.mjbot.domain.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Symbol entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymbolRepository extends JpaRepository<Symbol, Long>, JpaSpecificationExecutor<Symbol> {
    boolean existsAllBySymbol(String symbol);

    List<Symbol> findAllByActive(Boolean active);
}
