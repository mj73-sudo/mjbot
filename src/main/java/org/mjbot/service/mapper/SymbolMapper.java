package org.mjbot.service.mapper;

import com.kucoin.sdk.rest.response.SymbolResponse;
import java.util.List;
import org.mapstruct.*;
import org.mjbot.domain.Symbol;
import org.mjbot.service.dto.SymbolDTO;

/**
 * Mapper for the entity {@link Symbol} and its DTO {@link SymbolDTO}.
 */
@Mapper(componentModel = "spring")
public interface SymbolMapper extends EntityMapper<SymbolDTO, Symbol> {
    Symbol responseToSymbol(SymbolResponse response);

    List<Symbol> responseToSymbol(List<SymbolResponse> response);
}
