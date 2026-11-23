package com.svm.ecom.schedule.service;

import com.svm.ecom.schedule.util.DateConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared cut-off calculation for Port and Vessel schedule APIs: fallback chain
 * {@code port+terminal → port+All → All+All}, then subtract configured hours from arrival time.
 */
@Component
public class CutoffCalculator {

    private static final Logger logger = LoggerFactory.getLogger(CutoffCalculator.class);

    public Map<String, String> calculateCutoffDates(String arrDate, String port, String terminal,
                                                     Map<String, Map<String, String>> cutoffConfigMap) {
        Map<String, String> cutoffDates = new HashMap<>();
        if (cutoffConfigMap == null || cutoffConfigMap.isEmpty()) {
            return cutoffDates;
        }
        if (!StringUtils.hasText(port)) {
            return cutoffDates;
        }

        String p = port.trim();
        String t = StringUtils.hasText(terminal) ? terminal.trim() : "";

        Map<String, String> cutoffConfig = cutoffConfigMap.get(p + "&&" + t);
        if (cutoffConfig == null) {
            cutoffConfig = cutoffConfigMap.get(p + "&&All");
        }
        if (cutoffConfig == null) {
            cutoffConfig = cutoffConfigMap.get("All&&All");
        }

        if (cutoffConfig != null && StringUtils.hasText(arrDate)) {
            applyIfPresent(cutoffConfig, "docclosing", "docClosing", arrDate, cutoffDates);
            applyIfPresent(cutoffConfig, "vgmclosing", "vgmClosing", arrDate, cutoffDates);
            applyIfPresent(cutoffConfig, "fullcntrclosing", "contClosing", arrDate, cutoffDates);
        }
        return cutoffDates;
    }

    private void applyIfPresent(Map<String, String> config, String configKey, String outputKey, String arrDate,
                                Map<String, String> cutoffDates) {
        String hours = config.get(configKey);
        if (!StringUtils.hasText(hours)) {
            return;
        }
        try {
            int h = Integer.parseInt(hours.trim());
            String value = DateConversion.convertsubHours(arrDate, h);
            if (StringUtils.hasText(value)) {
                cutoffDates.put(outputKey, value);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid cutoff hours for key {}: {}", configKey, hours);
        }
    }
}
