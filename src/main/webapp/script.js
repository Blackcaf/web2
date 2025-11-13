document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('coordinateForm');
    const rHiddenInput = document.getElementById('r-hidden');
    let selectedR = null;

    function showModal(title, message) {
        const modal = document.getElementById('error-modal');
        if (!modal) return;

        const errorText = document.getElementById('error-text');
        if (errorText) errorText.textContent = message;

        modal.style.display = 'flex';

        const close = () => modal.style.display = 'none';
        modal.querySelector('.close-btn').onclick = close;
        modal.querySelector('.modal-backdrop').onclick = close;
    }

    function validate(field, showError = false) {
        const errorEl = document.getElementById(`${field}-error`);
        if (!errorEl) return true;

        let isValid = true;
        let errorMessage = '';

        if (field === 'x') {
            const xRadios = document.querySelectorAll('input[name="x"]');
            isValid = Array.from(xRadios).some(r => r.checked);
            errorMessage = 'Выберите значение X';
        } else if (field === 'y') {
            const yInput = document.getElementById('y-input');
            const yValue = yInput.value.trim().replace(',', '.');
            isValid = yValue && /^-?\d*\.?\d+$/.test(yValue);
            errorMessage = 'Y должен быть числом в интервале (-3; 3)';
        } else if (field === 'r') {
            isValid = rHiddenInput && rHiddenInput.value !== '';
            errorMessage = 'Выберите значение R';
        }

        if (showError) {
            errorEl.textContent = isValid ? '' : errorMessage;
            errorEl.classList.toggle('show', !isValid);
            errorEl.closest('.form-group')?.classList.toggle('error', !isValid);
        } else {
            errorEl.textContent = '';
            errorEl.classList.remove('show');
            errorEl.closest('.form-group')?.classList.remove('error');
        }

        return isValid;
    }

    function updateCurrentR() {
        const currentRDisplay = document.getElementById('current-r');

        if (currentRDisplay) {
            currentRDisplay.textContent = selectedR !== null ? selectedR : '-';
        }

        if (selectedR) {
            sessionStorage.setItem('selectedR', selectedR);
        }
    }

    function restoreSelectedR() {
        const savedR = sessionStorage.getItem('selectedR');
        if (savedR) {
            const rButtons = document.querySelectorAll('.r-btn');
            rButtons.forEach(btn => {
                if (btn.dataset.value === savedR) {
                    btn.classList.add('active');
                    selectedR = parseFloat(savedR);
                    if (rHiddenInput) {
                        rHiddenInput.value = savedR;
                    }
                }
            });
            updateCurrentR();
        }
    }

    function initializeRButtons() {
        const rButtons = document.querySelectorAll('.r-btn');

        rButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                rButtons.forEach(b => b.classList.remove('active'));

                this.classList.add('active');

                selectedR = parseFloat(this.dataset.value);
                if (rHiddenInput) {
                    rHiddenInput.value = this.dataset.value;
                }

                updateCurrentR();

                setTimeout(() => window.drawCoordinatePlane?.(), 50);
            });
        });
    }

    function initializeForm() {
        const yInput = document.getElementById('y-input');

        if (yInput) {
            yInput.onkeypress = e => {
                const char = String.fromCharCode(e.which);
                if (!/[\d.,\-]/.test(char) || (char === '-' && e.target.value) ||
                    ((char === '.' || char === ',') && /[.,]/.test(e.target.value))) {
                    e.preventDefault();
                }
            };

            yInput.oninput = function() {
                this.value = this.value.replace(',', '.').substring(0, 100);
            };

            yInput.onpaste = e => {
                e.preventDefault();
                const cleaned = (e.clipboardData || window.clipboardData)
                    .getData('text').replace(',', '.').trim();
                if (/^-?\d*\.?\d*$/.test(cleaned)) {
                    yInput.value = cleaned.substring(0, 100);
                }
            };
        }

        initializeRButtons();

        if (form) {
            form.onsubmit = e => {
                const isXValid = validate('x', true);
                const isYValid = validate('y', true);
                const isRValid = validate('r', true);

                if (!isXValid || !isYValid || !isRValid) {
                    e.preventDefault();
                    showModal('Ошибка', 'Пожалуйста, исправьте ошибки в форме');
                    return false;
                }

                if (yInput) {
                    yInput.value = yInput.value.replace(',', '.');
                }
            };
        }

        document.addEventListener('keydown', e => {
            if (e.key === 'Escape') {
                const modal = document.getElementById('error-modal');
                if (modal) modal.style.display = 'none';
            }
        });

        updateCurrentR();
        restoreSelectedR();
    }

    window.currentR = () => selectedR;
    window.showModal = showModal;

    function formatDateTime(isoString) {
        const date = new Date(isoString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${day}.${month}.${year} ${hours}:${minutes}:${seconds}`;
    }

    setTimeout(() => {
        document.querySelectorAll('.time-cell').forEach(cell => {
            const isoTime = cell.textContent.trim();
            if (isoTime && isoTime.includes('T')) {
                cell.textContent = formatDateTime(isoTime);
            }
        });
    }, 100);

    initializeForm();
});
