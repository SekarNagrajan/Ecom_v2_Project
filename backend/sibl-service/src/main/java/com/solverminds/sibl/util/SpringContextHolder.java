package com.solverminds.sibl.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility to access Spring beans from non-Spring-managed classes (e.g., JSR-303 validators).
 * Allows ConstraintValidators to access service beans for dynamic validation (same pattern as booking-service).
 * Explicit bean name avoids conflict with booking-service's SpringContextHolder when both packages are scanned.
 */
@Component("siSpringContextHolder")
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) return null;
        try {
            return context.getBean(beanClass);
        } catch (BeansException e) {
            return null;
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
