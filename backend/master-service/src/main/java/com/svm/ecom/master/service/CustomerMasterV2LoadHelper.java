package com.svm.ecom.master.service;

import com.svm.ecom.master.repository.masters.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Runs each Masters DB load in its own transaction (REQUIRES_NEW).
 * When a load fails (e.g. missing table), only that transaction rolls back;
 * the caller's transaction is not marked rollback-only.
 */
@Component
public class CustomerMasterV2LoadHelper {

    private final CustomerMasterV2Repository repository;

    public CustomerMasterV2LoadHelper(CustomerMasterV2Repository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<BillingRow> loadBilling() {
        return repository.findAllBilling();
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<CustomerContactRow> loadContacts() {
        return repository.findAllActiveContacts();
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<FinancialInfoRow> loadFinancial() {
        return repository.findAllFinancialInfo();
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<NotificationRow> loadNotifications() {
        return repository.findAllNotifications();
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<SalesRepRow> loadSalesReps() {
        return repository.findAllSalesReps();
    }

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<EnsRow> loadEns() {
        return repository.findAllEns();
    }
}
