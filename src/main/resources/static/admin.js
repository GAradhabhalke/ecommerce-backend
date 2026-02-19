document.addEventListener('DOMContentLoaded', () => {
    const createProductForm = document.getElementById('create-product-form');
    const createCategoryForm = document.getElementById('create-category-form');
    const updateOrderStatusForm = document.getElementById('update-order-status-form');
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        window.location.href = '/'; // Redirect if not logged in
        return;
    }

    // Create Product
    createProductForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('product-name').value;
        const description = document.getElementById('product-description').value;
        const price = document.getElementById('product-price').value;
        const categoryId = document.getElementById('product-category-id').value;
        const quantity = document.getElementById('product-quantity').value;
        const imageUrl = document.getElementById('product-image-url').value;

        fetch('http://localhost:8080/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ name, description, price, categoryId, quantity, imageUrl }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create product.');
            }
            return response.json();
        })
        .then(() => {
            alert('Product created successfully!');
            createProductForm.reset();
        })
        .catch(error => {
            console.error('Error creating product:', error);
            alert(error.message);
        });
    });

    // Create Category
    createCategoryForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('category-name').value;
        const parentId = document.getElementById('parent-category-id').value;

        fetch('http://localhost:8080/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ name, parentId: parentId ? parentId : null }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create category.');
            }
            return response.json();
        })
        .then(() => {
            alert('Category created successfully!');
            createCategoryForm.reset();
        })
        .catch(error => {
            console.error('Error creating category:', error);
            alert(error.message);
        });
    });

    // Update Order Status
    updateOrderStatusForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const orderId = document.getElementById('order-id').value;
        const status = document.getElementById('order-status').value;

        fetch(`http://localhost:8080/orders/${orderId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ status }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update order status.');
            }
            return response.json();
        })
        .then(() => {
            alert('Order status updated successfully!');
            updateOrderStatusForm.reset();
        })
        .catch(error => {
            console.error('Error updating order status:', error);
            alert(error.message);
        });
    });
});
