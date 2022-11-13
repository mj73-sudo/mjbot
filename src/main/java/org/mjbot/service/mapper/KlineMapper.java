package org.mjbot.service.mapper;

import org.mapstruct.*;
import org.mjbot.domain.Kline;
import org.mjbot.domain.Symbol;
import org.mjbot.service.dto.KlineDTO;
import org.mjbot.service.dto.SymbolDTO;

/**
 * Mapper for the entity {@link Kline} and its DTO {@link KlineDTO}.
 */
@Mapper(componentModel = "spring")
public interface KlineMapper extends EntityMapper<KlineDTO, Kline> {
    @Mapping(target = "symbol", source = "symbol", qualifiedByName = "symbolSymbol")
    KlineDTO toDto(Kline s);

    @Named("symbolSymbol")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "symbol", source = "symbol")
    SymbolDTO toDtoSymbolSymbol(Symbol symbol);
}
