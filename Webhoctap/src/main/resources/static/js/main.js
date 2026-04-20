/**
 * Main JavaScript - Webhoctap
 * Global functions and utilities
 */

// =========================================
// DOCUMENT READY
// =========================================

document.addEventListener('DOMContentLoaded', function() {
    initializePopovers();
    initializeTooltips();
    initializeAnimations();
    initializeFormValidation();
});

// =========================================
// BOOTSTRAP COMPONENTS INITIALIZATION
// =========================================

/**
 * Initialize Bootstrap Popovers
 */
function initializePopovers() {
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

/**
 * Initialize Bootstrap Tooltips
 */
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// =========================================
// ANIMATIONS
// =========================================

/**
 * Initialize scroll animations
 */
function initializeAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -100px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in', 'slide-up');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.card, .feature-card, .stat-card').forEach(function(el) {
        observer.observe(el);
    });
}

// =========================================
// FORM VALIDATION
// =========================================

/**
 * Initialize Bootstrap form validation
 */
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');

    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}

/**
 * Validate email format
 */
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

/**
 * Validate password strength
 */
function validatePasswordStrength(password) {
    const minLength = 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumbers = /\d/.test(password);
    const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

    return {
        isValid: password.length >= minLength && hasUpperCase && hasLowerCase && hasNumbers,
        strength: {
            length: password.length >= minLength,
            uppercase: hasUpperCase,
            lowercase: hasLowerCase,
            numbers: hasNumbers,
            special: hasSpecialChar
        }
    };
}

// =========================================
// ALERTS & NOTIFICATIONS
// =========================================

/**
 * Show success alert
 */
function showSuccessAlert(message, duration = 5000) {
    const alertHtml = `
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    prependAlert(alertHtml, duration);
}

/**
 * Show error alert
 */
function showErrorAlert(message, duration = 5000) {
    const alertHtml = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    prependAlert(alertHtml, duration);
}

/**
 * Show warning alert
 */
function showWarningAlert(message, duration = 5000) {
    const alertHtml = `
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    prependAlert(alertHtml, duration);
}

/**
 * Helper function to prepend alert to page
 */
function prependAlert(alertHtml, duration) {
    const alertContainer = document.querySelector('main') || document.body;
    const alertElement = document.createElement('div');
    alertElement.innerHTML = alertHtml;
    alertContainer.insertBefore(alertElement.firstElementChild, alertContainer.firstChild);

    if (duration > 0) {
        setTimeout(() => {
            const alert = alertContainer.querySelector('.alert');
            if (alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, duration);
    }
}

// =========================================
// CONFIRMATION DIALOGS
// =========================================

/**
 * Show confirmation dialog
 */
function showConfirmDialog(title, message, confirmCallback, cancelCallback) {
    const modalElement = document.createElement('div');
    modalElement.className = 'modal fade';
    modalElement.innerHTML = `
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header border-0">
                    <h5 class="modal-title">${title}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    ${message}
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="confirmBtn">Xác nhận</button>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modalElement);
    const modal = new bootstrap.Modal(modalElement);

    document.getElementById('confirmBtn').addEventListener('click', () => {
        if (confirmCallback) confirmCallback();
        modal.hide();
        setTimeout(() => modalElement.remove(), 200);
    });

    modalElement.addEventListener('hidden.bs.modal', () => {
        if (cancelCallback) cancelCallback();
        modalElement.remove();
    });

    modal.show();
}

// =========================================
// UTILITY FUNCTIONS
// =========================================

/**
 * Format number as currency
 */
function formatCurrency(amount, currency = 'VND') {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

/**
 * Format date
 */
function formatDate(date, format = 'dd/MM/yyyy') {
    if (typeof date === 'string') {
        date = new Date(date);
    }

    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');

    return format
        .replace('dd', day)
        .replace('MM', month)
        .replace('yyyy', year)
        .replace('HH', hours)
        .replace('mm', minutes);
}

/**
 * Truncate text with ellipsis
 */
function truncateText(text, length = 100) {
    if (text.length > length) {
        return text.substring(0, length) + '...';
    }
    return text;
}

/**
 * Debounce function for search/resize events
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Throttle function for scroll events
 */
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// =========================================
// LOCAL STORAGE HELPERS
// =========================================

/**
 * Save to localStorage
 */
function saveToStorage(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
        return true;
    } catch (e) {
        console.error('Error saving to localStorage:', e);
        return false;
    }
}

/**
 * Get from localStorage
 */
function getFromStorage(key, defaultValue = null) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (e) {
        console.error('Error reading from localStorage:', e);
        return defaultValue;
    }
}

/**
 * Remove from localStorage
 */
function removeFromStorage(key) {
    try {
        localStorage.removeItem(key);
        return true;
    } catch (e) {
        console.error('Error removing from localStorage:', e);
        return false;
    }
}

// =========================================
// API HELPERS
// =========================================

/**
 * Make API request
 */
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
        ...options
    };

    try {
        const response = await fetch(url, defaultOptions);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('API request error:', error);
        throw error;
    }
}

// =========================================
// EXPORT FUNCTIONS FOR GLOBAL USE
// =========================================

window.Webhoctap = {
    showSuccessAlert,
    showErrorAlert,
    showWarningAlert,
    showConfirmDialog,
    formatCurrency,
    formatDate,
    truncateText,
    debounce,
    throttle,
    saveToStorage,
    getFromStorage,
    removeFromStorage,
    apiRequest,
    validateEmail,
    validatePasswordStrength
};
