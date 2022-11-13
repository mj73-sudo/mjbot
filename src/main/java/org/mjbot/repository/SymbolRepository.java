package org.mjbot.repository;

import org.mjbot.domain.Symbol;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Symbol entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymbolRepository extends JpaRepository<Symbol, Long>, JpaSpecificationExecutor<Symbol> {}
