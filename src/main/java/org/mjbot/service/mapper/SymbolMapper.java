package org.mjbot.service.mapper;

import org.mapstruct.*;
import org.mjbot.domain.Symbol;
import org.mjbot.service.dto.SymbolDTO;

/**
 * Mapper for the entity {@link Symbol} and its DTO {@link SymbolDTO}.
 */
@Mapper(componentModel = "spring")
public interface SymbolMapper extends EntityMapper<SymbolDTO, Symbol> {}
