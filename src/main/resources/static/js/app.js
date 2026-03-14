// ════════════════════════════════════════════════════════════════
// Campus Event Tracker — Client-Side JavaScript
// ════════════════════════════════════════════════════════════════

document.addEventListener('DOMContentLoaded', () => {

    // ── Auto-dismiss flash messages after 5 seconds ──────────
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // ── Form validation — end time must be after start time ──
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');

    if (startTimeInput && endTimeInput) {
        startTimeInput.addEventListener('change', () => {
            endTimeInput.min = startTimeInput.value;
            if (endTimeInput.value && endTimeInput.value < startTimeInput.value) {
                endTimeInput.value = startTimeInput.value;
            }
        });
    }

    // ── Confirm before publishing an event ────────────────────
    const publishForms = document.querySelectorAll('form[action*="/publish"]');
    publishForms.forEach(form => {
        form.addEventListener('submit', (e) => {
            if (!confirm('Are you sure you want to publish this event? It will be visible to all users.')) {
                e.preventDefault();
            }
        });
    });
});
