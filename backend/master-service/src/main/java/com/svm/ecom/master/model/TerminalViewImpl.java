package com.svm.ecom.master.model;

/**
 * Implementation class for TerminalView interface
 * Used with native SQL queries
 */
public class TerminalViewImpl implements TerminalView {

    private String terminalCode;
    private String terminalName;

    public TerminalViewImpl(String terminalCode, String terminalName) {
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
    }

    @Override
    public String getTerminalCode() {
        return terminalCode;
    }

    @Override
    public String getTerminalName() {
        return terminalName;
    }

}
