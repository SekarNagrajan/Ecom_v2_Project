package com.svm.ecom.master.repository.masters;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Commodity v2 (Masters DB).
 * Uses mastersPersistenceUnit for read-only native queries.
 */
@Repository
public class CommodityMasterV2Repository {

    private static final String SQL_ALL_COMMODITIES =
            "select distinct c1.ComdtyCode,c1.ComdtyName,c1.ComdtyType,c1.Restricted,c1.EXEMPTCOMDTY,"
            + "c2.ComdtyCode as grpcode,c2.ComdtyName as groupname "
            + "from commodity c1(nolock) "
            + "inner join commodity c2(nolock) on c1.grpcode=c2.ComdtyCode "
            + "where c1.status='A' and c2.status='A' and c1.parentCode<>'0'";

    @PersistenceContext(unitName = "mastersPersistenceUnit")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<CommodityRow> findAllActiveCommodities() {
        Query query = entityManager.createNativeQuery(SQL_ALL_COMMODITIES);
        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapRow).collect(Collectors.toList());
    }

    private CommodityRow mapRow(Object[] row) {
        CommodityRow r = new CommodityRow();
        r.setComdtyCode(getString(row, 0));
        r.setComdtyName(getString(row, 1));
        r.setComdtyType(getString(row, 2));
        r.setRestricted(getString(row, 3));
        r.setExemptComdty(getString(row, 4));
        r.setGrpCode(getString(row, 5));
        r.setGroupName(getString(row, 6));
        return r;
    }

    private static String getString(Object[] row, int i) {
        if (row == null || i >= row.length || row[i] == null) return null;
        return String.valueOf(row[i]).trim();
    }
}
