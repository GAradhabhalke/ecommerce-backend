document.addEventListener('DOMContentLoaded', () => {
    const cartItemsContainer = document.getElementById('cart-items-container');
    const cartTotalSpan = document.getElementById('cart-total');
    const checkoutButton = document.getElementById('checkout-button');

    const token = localStorage.getItem('jwtToken');

    if (!token) {
        window.location.href = '/'; // Redirect to home if not logged in
        return;
    }

    checkoutButton.addEventListener('click', () => {
        window.location.href = '/checkout.html';
    });

    function loadCart() {
        fetch('http://localhost:8080/cart', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Could not fetch cart.');
            }
            return response.json();
        })
        .then(cart => {
            cartItemsContainer.innerHTML = ''; // Clear previous items
            let total = 0;

            if (!cart.items || cart.items.length === 0) {
                cartItemsContainer.innerHTML = '<p>Your cart is empty.</p>';
                checkoutButton.disabled = true;
                return;
            }

            cart.items.forEach(item => {
                const itemTotal = item.product.price * item.quantity;
                total += itemTotal;

                const cartItemElement = document.createElement('div');
                cartItemElement.className = 'cart-item';
                cartItemElement.innerHTML = `
                    <p><strong>${item.product.name}</strong></p>
                    <p>Quantity: ${item.quantity}</p>
                    <p>Price: $${itemTotal.toFixed(2)}</p>
                `;
                cartItemsContainer.appendChild(cartItemElement);
            });

            cartTotalSpan.textContent = total.toFixed(2);
        })
        .catch(error => {
            console.error('Error loading cart:', error);
            cartItemsContainer.innerHTML = '<p>Error loading cart. Please try again.</p>';
        });
    }

    loadCart();
});
