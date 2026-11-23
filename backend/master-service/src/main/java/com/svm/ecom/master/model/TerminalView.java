package com.svm.ecom.master.model;

/**
 * Interface-based projection for terminal master data
 * Used with Spring Data JPA native queries
 */
public interface TerminalView {
    String getTerminalCode();

    String getTerminalName();
}
