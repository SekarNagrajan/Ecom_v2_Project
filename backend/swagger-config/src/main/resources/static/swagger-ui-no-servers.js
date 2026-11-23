// Aggressive JavaScript to remove servers dropdown from Swagger UI
(function() {
    'use strict';
    
    console.log('Aggressive servers removal script loading...');
    
    function aggressiveServerRemoval() {
        // Comprehensive list of selectors for server-related elements
        const serverSelectors = [
            '.swagger-ui .servers',
            '.swagger-ui .servers-wrapper', 
            '.swagger-ui .scheme-container',
            '.swagger-ui .servers-title',
            '.swagger-ui .servers select',
            '.swagger-ui div[class*="servers"]',
            '.swagger-ui label[for*="server"]',
            '.swagger-ui [data-name*="server"]',
            '.swagger-ui .server-container',
            '.swagger-ui .server-wrapper',
            '.swagger-ui select[name*="server"]',
            '.swagger-ui .topbar-wrapper .servers',
            'select[aria-label*="server"]',
            '[class*="servers-container"]',
            '[data-testid*="server"]'
        ];
        
        let removedCount = 0;
        
        serverSelectors.forEach(selector => {
            try {
                const elements = document.querySelectorAll(selector);
                elements.forEach(element => {
                    if (element) {
                        // Multiple removal methods for redundancy
                        element.style.display = 'none';
                        element.style.visibility = 'hidden';
                        element.style.height = '0px';
                        element.style.overflow = 'hidden';
                        element.style.opacity = '0';
                        element.remove(); // Complete removal from DOM
                        removedCount++;
                    }
                });
            } catch (e) {
                // Ignore selector errors
            }
        });
        
        // Also remove any element containing "server" text that looks like a dropdown
        const allElements = document.querySelectorAll('*');
        allElements.forEach(el => {
            if (el.tagName === 'SELECT' || el.tagName === 'OPTION') {
                const text = el.textContent || el.innerText || '';
                if (text.toLowerCase().includes('server') && text.toLowerCase().includes('localhost')) {
                    el.style.display = 'none';
                    el.remove();
                    removedCount++;
                }
            }
        });
        
        if (removedCount > 0) {
            console.log(`Removed ${removedCount} server-related elements`);
        }
        
        return removedCount;
    }
    
    // Run immediately
    aggressiveServerRemoval();
    
    // Run multiple times with different delays
    const delays = [50, 100, 200, 500, 1000, 2000, 3000, 5000];
    delays.forEach(delay => {
        setTimeout(aggressiveServerRemoval, delay);
    });
    
    // Set up MutationObserver for continuous monitoring
    if (typeof MutationObserver !== 'undefined') {
        const observer = new MutationObserver(function(mutations) {
            let needsCheck = false;
            
            mutations.forEach(function(mutation) {
                if (mutation.type === 'childList') {
                    for (let node of mutation.addedNodes) {
                        if (node.nodeType === Node.ELEMENT_NODE) {
                            const className = node.className || '';
                            const textContent = node.textContent || '';
                            
                            if (className.includes('server') || 
                                textContent.includes('server') || 
                                textContent.includes('localhost') ||
                                node.tagName === 'SELECT') {
                                needsCheck = true;
                                break;
                            }
                        }
                    }
                }
            });
            
            if (needsCheck) {
                setTimeout(aggressiveServerRemoval, 10);
            }
        });
        
        // Start observing with comprehensive options
        observer.observe(document.body, {
            childList: true,
            subtree: true,
            attributes: true,
            attributeFilter: ['class', 'style', 'id'],
            characterData: true
        });
    }
    
    // Override Swagger UI configuration if possible
    if (typeof window !== 'undefined') {
        const originalSwaggerUIBundle = window.SwaggerUIBundle;
        if (originalSwaggerUIBundle) {
            window.SwaggerUIBundle = function(config) {
                // Modify config to remove servers
                if (config) {
                    config.validatorUrl = null;
                    if (config.spec) {
                        delete config.spec.servers;
                    }
                    
                    // Add onComplete callback to remove servers
                    const originalOnComplete = config.onComplete;
                    config.onComplete = function() {
                        if (originalOnComplete) originalOnComplete();
                        setTimeout(aggressiveServerRemoval, 100);
                        setTimeout(aggressiveServerRemoval, 500);
                        setTimeout(aggressiveServerRemoval, 1000);
                    };
                }
                
                return originalSwaggerUIBundle.call(this, config);
            };
            
            // Copy all original properties
            Object.keys(originalSwaggerUIBundle).forEach(key => {
                window.SwaggerUIBundle[key] = originalSwaggerUIBundle[key];
            });
        }
    }
    
    console.log('Aggressive servers removal script loaded');
})();
