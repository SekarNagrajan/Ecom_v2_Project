package com.solverminds.booking.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility to access Spring beans from non-Spring-managed classes (e.g., JSR-303 validators).
 * This allows ConstraintValidators to access repository beans for dynamic validation.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.context = applicationContext;
    }

    /**
     * Get a Spring bean by its class type.
     * @param beanClass The class of the bean to retrieve
     * @param <T> The type of the bean
     * @return The bean instance, or null if not found or context is not initialized
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            return null;
        }
        try {
            return context.getBean(beanClass);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * Get the application context.
     * @return The ApplicationContext, or null if not initialized
     */
    public static ApplicationContext getContext() {
        return context;
    }
}

