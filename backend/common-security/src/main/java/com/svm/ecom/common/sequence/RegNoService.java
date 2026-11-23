package com.svm.ecom.common.sequence;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class RegNoService {
    private final AtomicLong seq = new AtomicLong(590); // seed example

    public String nextRegNo() {
        return "REG" + seq.incrementAndGet();
    }
}
