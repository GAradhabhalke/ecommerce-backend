document.addEventListener('DOMContentLoaded', async () => {
    // IMPORTANT: Replace with your actual Stripe publishable key
    const stripe = Stripe('pk_test_51T0btODXpH5uVXTU39xdJzBUMtzM55IVu2oWLWE6v2qSBGRh3M0RjC0jDn8imhgzSNFVIZuB4daPxkslz85Wzyku00p1DtmdEX');

    const checkoutButton = document.getElementById('checkout-button');
    const paymentForm = document.getElementById('payment-form');
    const submitButton = document.getElementById('submit-button');
    const messageContainer = document.getElementById('payment-message');

    let elements;
    let clientSecret;

    // 1. Create Payment Intent and get client secret from your backend
    async function initialize() {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            window.location.href = '/';
            return;
        }

        // For now, we assume the order is created right before checkout.
        // In a real app, you might pass an orderId from the cart page.
        const order = await createOrder(token);
        if (!order) {
            messageContainer.textContent = "Could not create order. Please try again.";
            return;
        }

        const response = await fetch('http://localhost:8080/payments/create-payment-intent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ orderId: order.id })
        });

        const { clientSecret: secret } = await response.json();
        clientSecret = secret;

        elements = stripe.elements({ clientSecret });
        const paymentElement = elements.create("payment");
        paymentElement.mount("#payment-element");
    }

    // Helper function to create an order from the cart before checkout
    async function createOrder(token) {
        try {
            const response = await fetch('http://localhost:8080/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ shippingAddress: "123 Checkout Lane" })
            });
            if (!response.ok) throw new Error('Order creation failed');
            return await response.json();
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    // 2. Handle the payment submission
    paymentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        setLoading(true);

        const { error } = await stripe.confirmPayment({
            elements,
            confirmParams: {
                // URL to redirect to on successful payment
                return_url: window.location.origin + '/order-success.html',
            },
        });

        // This point will only be reached if there is an immediate error
        if (error.type === "card_error" || error.type === "validation_error") {
            showMessage(error.message);
        } else {
            showMessage("An unexpected error occurred.");
        }

        setLoading(false);
    });

    // --- UI helpers ---
    function showMessage(messageText) {
        messageContainer.classList.remove("hidden");
        messageContainer.textContent = messageText;
        setTimeout(() => {
            messageContainer.classList.add("hidden");
            messageContainer.textContent = "";
        }, 4000);
    }

    function setLoading(isLoading) {
        if (isLoading) {
            submitButton.disabled = true;
        } else {
            submitButton.disabled = false;
        }
    }

    initialize();
});
