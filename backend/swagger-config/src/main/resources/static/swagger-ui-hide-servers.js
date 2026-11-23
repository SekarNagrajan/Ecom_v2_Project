// JavaScript to completely remove servers dropdown from Swagger UI
(function() {
    'use strict';
    
    console.log('Swagger UI servers removal script loaded');
    
    function hideServersElements() {
        // Target all possible server-related elements
        const selectors = [
            '.swagger-ui .servers',
            '.swagger-ui .servers-wrapper', 
            '.swagger-ui .scheme-container',
            '.swagger-ui .servers-title',
            '.swagger-ui .servers select',
            '.swagger-ui div[class*="servers"]',
            '.swagger-ui label[for*="server"]',
            '.swagger-ui [data-name*="server"]',
            // Additional selectors for different Swagger UI versions
            '.swagger-ui .server-container',
            '.swagger-ui .server-wrapper',
            '.swagger-ui .opblock-section-header h4:contains("Servers")',
            '.swagger-ui .opblock-section-header h4[data-name*="server"]'
        ];
        
        let hiddenCount = 0;
        
        selectors.forEach(selector => {
            try {
                const elements = document.querySelectorAll(selector);
                elements.forEach(element => {
                    if (element && element.style.display !== 'none') {
                        element.style.display = 'none';
                        element.style.visibility = 'hidden';
                        element.remove(); // Completely remove from DOM
                        hiddenCount++;
                    }
                });
            } catch (e) {
                console.debug('Selector not supported:', selector);
            }
        });
        
        if (hiddenCount > 0) {
            console.log(`Hidden/removed ${hiddenCount} server-related elements`);
        }
        
        return hiddenCount;
    }
    
    // Run immediately when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', hideServersElements);
    } else {
        hideServersElements();
    }
    
    // Run periodically to catch dynamically loaded content
    const intervals = [100, 500, 1000, 2000, 5000];
    intervals.forEach(delay => {
        setTimeout(hideServersElements, delay);
    });
    
    // Set up MutationObserver to watch for new elements
    if (typeof MutationObserver !== 'undefined') {
        const observer = new MutationObserver(function(mutations) {
            let shouldCheck = false;
            
            mutations.forEach(function(mutation) {
                if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                    // Check if any added nodes might contain server elements
                    for (let node of mutation.addedNodes) {
                        if (node.nodeType === Node.ELEMENT_NODE) {
                            const nodeClass = node.className || '';
                            const nodeText = node.textContent || '';
                            
                            if (nodeClass.includes('server') || 
                                nodeText.toLowerCase().includes('server') ||
                                nodeClass.includes('swagger-ui')) {
                                shouldCheck = true;
                                break;
                            }
                        }
                    }
                }
            });
            
            if (shouldCheck) {
                setTimeout(hideServersElements, 50);
            }
        });
        
        // Start observing
        observer.observe(document.body, {
            childList: true,
            subtree: true,
            attributes: true,
            attributeFilter: ['class', 'style']
        });
        
        console.log('MutationObserver set up to monitor for server elements');
    }
    
    // Also try to intercept any server-related configurations
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        return originalFetch.apply(this, args).then(response => {
            if (response.url && response.url.includes('/v3/api-docs')) {
                return response.clone().json().then(data => {
                    if (data.servers) {
                        console.log('Removing servers from OpenAPI response');
                        delete data.servers;
                    }
                    
                    // Create a new response with modified data
                    return new Response(JSON.stringify(data), {
                        status: response.status,
                        statusText: response.statusText,
                        headers: response.headers
                    });
                }).catch(() => response);
            }
            return response;
        });
    };
    
})();
