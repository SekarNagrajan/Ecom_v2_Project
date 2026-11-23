package com.svm.ecom.master.repository.masters;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for Agency v2 (Masters DB).
 * Uses mastersPersistenceUnit for read-only native queries.
 */
@Repository
public class AgencyMasterV2Repository {

    private static final String SQL_ALL_AGENCIES =
            "select agencycode,agencyname,type,address,city,countrycode,email,location "
            + "from agency(nolock) where status='A' and Status_Agy='A'";

    private static final String SQL_PORTS_BY_AGENCIES =
            "select agencycode,portcode,type,validfrom,validto "
            + "from agencylocationsnew(nolock) where status='A' and agencycode in (%s)";

    @PersistenceContext(unitName = "mastersPersistenceUnit")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<AgencyRow> findAllActiveAgencies() {
        Query query = entityManager.createNativeQuery(SQL_ALL_AGENCIES);
        List<Object[]> results = query.getResultList();
        return results.stream().map(AgencyRow::new).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<AgencyPortRow> findPortsByAgencyCodes(List<String> agencyCodes) {
        if (agencyCodes == null || agencyCodes.isEmpty()) {
            return Collections.emptyList();
        }
        StringBuilder inList = new StringBuilder();
        for (String code : agencyCodes) {
            if (code == null || code.isEmpty()) {
                continue;
            }
            if (inList.length() > 0) {
                inList.append(',');
            }
            inList.append('\'').append(code.replace("'", "''")).append('\'');
        }
        if (inList.length() == 0) {
            return Collections.emptyList();
        }
        String sql = String.format(SQL_PORTS_BY_AGENCIES, inList);
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        return results.stream().map(AgencyPortRow::new).collect(Collectors.toList());
    }
}
