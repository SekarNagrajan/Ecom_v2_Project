// Manual authentication test script
// Paste this into browser console to test authentication detection

(function() {
    console.log('=== Manual Auth Test Started ===');
    
    // Function to get JWT token from Swagger UI
    function getJWTToken() {
        try {
            const ui = window.ui;
            if (ui && ui.auth && ui.auth.authorized && ui.auth.authorized.JWT) {
                return ui.auth.authorized.JWT.value;
            }
            
            // Try alternative method
            const authWrapper = ui?.getState()?.auth?.authorized?.JWT;
            if (authWrapper) {
                return authWrapper.value;
            }
            
            return null;
        } catch (e) {
            console.error('Error getting JWT token:', e);
            return null;
        }
    }
    
    // Function to check auth status
    async function checkAuthStatus() {
        const token = getJWTToken();
        console.log('Current JWT token:', token ? 'Found' : 'Not found');
        
        if (token) {
            console.log('Token preview:', token.substring(0, 50) + '...');
        }
        
        try {
            const response = await fetch('/ecomapi-service/swagger-auth/auth-status', {
                headers: token ? { 'Authorization': token } : {}
            });
            
            if (response.ok) {
                const data = await response.json();
                console.log('Auth status response:', data);
                return data;
            } else {
                console.error('Auth status request failed:', response.status, response.statusText);
                return null;
            }
        } catch (error) {
            console.error('Error checking auth status:', error);
            return null;
        }
    }
    
    // Function to show status message
    function showMessage(message, type = 'info') {
        // Remove existing message
        const existing = document.getElementById('manual-auth-message');
        if (existing) {
            existing.remove();
        }
        
        // Create new message
        const div = document.createElement('div');
        div.id = 'manual-auth-message';
        div.style.cssText = `
            position: fixed;
            top: 10px;
            right: 10px;
            z-index: 9999;
            padding: 12px 16px;
            border-radius: 4px;
            font-family: 'Segoe UI', sans-serif;
            font-size: 14px;
            max-width: 400px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            ${type === 'success' ? 'background: #e8f5e8; border: 1px solid #4caf50; color: #2e7d32;' : 
              type === 'error' ? 'background: #ffebee; border: 1px solid #f44336; color: #c62828;' :
              'background: #e3f2fd; border: 1px solid #2196f3; color: #1565c0;'}
        `;
        div.innerHTML = message;
        document.body.appendChild(div);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (div.parentNode) {
                div.parentNode.removeChild(div);
            }
        }, 5000);
    }
    
    // Function to add refresh button
    function addRefreshButton() {
        const existing = document.getElementById('manual-refresh-btn');
        if (!existing) {
            const btn = document.createElement('button');
            btn.id = 'manual-refresh-btn';
            btn.innerHTML = '🔄 Refresh API Groups (Manual)';
            btn.style.cssText = `
                position: fixed;
                top: 60px;
                right: 10px;
                z-index: 9999;
                padding: 8px 16px;
                background: #4caf50;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
            `;
            btn.onclick = () => {
                console.log('Manual refresh triggered');
                window.location.reload();
            };
            document.body.appendChild(btn);
        }
    }
    
    // Main monitoring function
    let lastAuthState = null;
    
    async function monitorAuth() {
        const authStatus = await checkAuthStatus();
        
        if (authStatus) {
            const currentState = {
                authenticated: authStatus.authenticated,
                username: authStatus.username
            };
            
            const stateChanged = !lastAuthState || 
                lastAuthState.authenticated !== currentState.authenticated ||
                lastAuthState.username !== currentState.username;
            
            if (stateChanged) {
                console.log('=== AUTH STATE CHANGED ===');
                console.log('Previous:', lastAuthState);
                console.log('Current:', currentState);
                
                lastAuthState = currentState;
                
                if (currentState.authenticated) {
                    showMessage(`✅ Authenticated as: ${currentState.username}<br/>Click the refresh button to see API groups.`, 'success');
                    addRefreshButton();
                } else {
                    showMessage('🔐 Please authenticate to view available endpoints', 'info');
                    const btn = document.getElementById('manual-refresh-btn');
                    if (btn) btn.remove();
                }
            }
        }
    }
    
    // Start monitoring
    console.log('Starting auth monitoring...');
    showMessage('🔍 Auth monitoring started. Check console for details.', 'info');
    
    // Initial check
    monitorAuth();
    
    // Set up interval
    const interval = setInterval(monitorAuth, 3000);
    
    // Global functions for manual testing
    window.manualAuthTest = {
        checkAuth: checkAuthStatus,
        getToken: getJWTToken,
        stopMonitoring: () => {
            clearInterval(interval);
            console.log('Auth monitoring stopped');
        }
    };
    
    console.log('=== Manual auth test functions available ===');
    console.log('manualAuthTest.checkAuth() - Check current auth status');
    console.log('manualAuthTest.getToken() - Get current JWT token');
    console.log('manualAuthTest.stopMonitoring() - Stop monitoring');
})();
