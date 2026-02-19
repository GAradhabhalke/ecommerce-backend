document.addEventListener('DOMContentLoaded', () => {
    const registrationForm = document.getElementById('registration-form');
    const messageContainer = document.getElementById('message-container');

    registrationForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const fullName = document.getElementById('fullName').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        fetch('http://localhost:8080/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ fullName, email, password }),
        })
        .then(response => {
            if (!response.ok) {
                // If the email is already taken, the backend might return a 4xx error
                throw new Error('Registration failed. The email might already be in use.');
            }
            return response.json();
        })
        .then(() => {
            messageContainer.textContent = 'Registration successful! Redirecting to login...';
            setTimeout(() => {
                window.location.href = '/'; // Redirect to the home/login page
            }, 2000);
        })
        .catch(error => {
            console.error('Registration error:', error);
            messageContainer.textContent = error.message;
        });
    });
});
