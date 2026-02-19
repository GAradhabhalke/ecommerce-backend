document.addEventListener('DOMContentLoaded', () => {
    const profileForm = document.getElementById('profile-form');
    const messageContainer = document.getElementById('message-container');
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        window.location.href = '/'; // Redirect if not logged in
        return;
    }

    profileForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const fullName = document.getElementById('fullName').value;
        const password = document.getElementById('password').value;

        const updateData = {};
        if (fullName) {
            updateData.fullName = fullName;
        }
        if (password) {
            updateData.password = password;
        }

        fetch('http://localhost:8080/users/me', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(updateData),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update profile.');
            }
            return response.json();
        })
        .then(data => {
            messageContainer.textContent = 'Profile updated successfully!';
            // If name was updated, you might want to update localStorage if you store it there
        })
        .catch(error => {
            console.error('Profile update error:', error);
            messageContainer.textContent = error.message;
        });
    });
});
