document.addEventListener('DOMContentLoaded', () => {
    const orderList = document.getElementById('order-list');
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        window.location.href = '/'; // Redirect if not logged in
        return;
    }

    fetch('http://localhost:8080/orders', {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch orders.');
        }
        return response.json();
    })
    .then(orders => {
        if (orders.length === 0) {
            orderList.innerHTML = '<p>You have no orders.</p>';
            return;
        }
        const orderTable = document.createElement('table');
        orderTable.innerHTML = `
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Date</th>
                    <th>Total</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                ${orders.map(order => `
                    <tr>
                        <td>${order.id}</td>
                        <td>${new Date(order.orderDate).toLocaleDateString()}</td>
                        <td>$${order.totalAmount.toFixed(2)}</td>
                        <td>${order.status}</td>
                    </tr>
                `).join('')}
            </tbody>
        `;
        orderList.appendChild(orderTable);
    })
    .catch(error => {
        console.error('Error fetching orders:', error);
        orderList.innerHTML = '<p>Could not load orders.</p>';
    });
});
