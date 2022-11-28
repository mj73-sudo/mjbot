package org.mjbot.repository;

import java.util.List;
import java.util.Optional;
import org.mjbot.domain.Kline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Kline entity.
 */
@Repository
public interface KlineRepository extends JpaRepository<Kline, Long>, JpaSpecificationExecutor<Kline> {
    default Optional<Kline> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Kline> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Kline> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct kline from Kline kline left join fetch kline.symbol",
        countQuery = "select count(distinct kline) from Kline kline"
    )
    Page<Kline> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct kline from Kline kline left join fetch kline.symbol")
    List<Kline> findAllWithToOneRelationships();

    @Query("select kline from Kline kline left join fetch kline.symbol where kline.id =:id")
    Optional<Kline> findOneWithToOneRelationships(@Param("id") Long id);

    Kline findFirstBySymbol_IdAndTimeTypeOrderByTimeDesc(Long symbolId, String timeType);

    Kline findFirstByTimeAndTimeTypeAndSymbol_Id(Long time, String timeType, Long symbolId);

    @Query(
        "select kline from Kline kline " +
        "join kline.symbol symbol " +
        "where symbol.active=true and kline.timeType=:timeType and kline.time >= :fromDate"
    )
    List<Kline> getFiveMinutesRecords(@Param("fromDate") Long timestamp, @Param("timeType") String timeType);
}
