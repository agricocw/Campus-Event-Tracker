// ════════════════════════════════════════════════════════════════
// Campus Event Tracker — Client-Side JavaScript
// ════════════════════════════════════════════════════════════════

function getCsrfToken() {
    const meta = document.querySelector('meta[name="_csrf"]');
    return meta ? meta.getAttribute('content') : '';
}

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

    const notifToggle = document.getElementById('notifToggle');
    const notifDropdown = document.getElementById('notifDropdown');

    if (notifToggle && notifDropdown) {
        notifToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            notifDropdown.classList.toggle('open');
        });

        document.addEventListener('click', (e) => {
            if (!notifToggle.contains(e.target) && !notifDropdown.contains(e.target)) {
                notifDropdown.classList.remove('open');
            }
        });

        notifDropdown.addEventListener('click', (e) => {
            const btn = e.target.closest('.notif-dismiss');
            if (!btn) return;

            const item = btn.closest('.notif-item');
            const id = item.dataset.id;

            fetch(`/user/notifications/${id}/read`, { method: 'POST',
                headers: { 'X-XSRF-TOKEN': getCsrfToken() }
            }).then(() => {
                item.remove();
                const badge = document.getElementById('notifBadge');
                if (badge) {
                    const count = parseInt(badge.textContent) - 1;
                    if (count <= 0) badge.remove();
                    else badge.textContent = count;
                }
                if (!notifDropdown.querySelector('.notif-item')) {
                    notifDropdown.innerHTML = '<div class="notif-empty">No new notifications</div>';
                }
            });
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
