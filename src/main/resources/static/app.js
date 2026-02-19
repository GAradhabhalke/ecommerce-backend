document.addEventListener('DOMContentLoaded', () => {
    // UI Elements
    const productGrid = document.getElementById('product-grid');
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const userInfo = document.getElementById('user-info');
    const userEmailSpan = document.getElementById('user-email');
    const logoutButton = document.getElementById('logout-button');
    const viewCartButton = document.getElementById('view-cart-button');
    const reviewForm = document.getElementById('review-form');
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-input');

    // --- Authentication ---
    function decodeJwt(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

    function updateUIForAuthState() {
        const token = localStorage.getItem('jwtToken');
        const email = localStorage.getItem('userEmail');
        if (token && email) {
            loginForm.style.display = 'none';
            userInfo.style.display = 'block';
            userEmailSpan.textContent = email;

            const decodedToken = decodeJwt(token);
            if (decodedToken && decodedToken.roles.includes('ROLE_ADMIN')) {
                const adminLink = document.createElement('a');
                adminLink.href = '/admin.html';
                adminLink.textContent = 'Admin';
                userInfo.insertBefore(adminLink, viewCartButton);
            }
        } else {
            loginForm.style.display = 'block';
            userInfo.style.display = 'none';
            userEmailSpan.textContent = '';
        }
    }

    function login(email, password) {
        fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        })
        .then(response => {
            if (!response.ok) throw new Error('Login failed');
            return response.json();
        })
        .then(data => {
            localStorage.setItem('jwtToken', data.token);
            localStorage.setItem('userEmail', email);
            updateUIForAuthState();
        })
        .catch(error => {
            console.error('Login error:', error);
            alert('Invalid email or password.');
        });
    }

    function logout() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userEmail');
        window.location.reload();
    }

    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        login(emailInput.value, passwordInput.value);
    });

    logoutButton.addEventListener('click', logout);
    
    viewCartButton.addEventListener('click', () => {
        window.location.href = '/cart.html';
    });

    reviewForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('You must be logged in to submit a review.');
            return;
        }
        const productId = document.getElementById('review-product-id').value;
        const rating = document.getElementById('review-rating').value;
        const comment = document.getElementById('review-comment').value;
        fetch('http://localhost:8080/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ productId, rating, comment })
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to submit review.');
            return response.json();
        })
        .then(() => {
            alert('Review submitted successfully!');
            reviewForm.reset();
            loadProducts();
        })
        .catch(error => {
            console.error('Review submission error:', error);
            alert(error.message);
        });
    });

    // --- Cart ---
    function addToCart(productId) {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Please log in to add items to your cart.');
            return;
        }
        fetch('http://localhost:8080/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ productId, quantity: 1 })
        })
        .then(response => {
            if (!response.ok) {
                if (response.status === 403) {
                    alert("Your session has expired. Please log in again.");
                    logout();
                }
                throw new Error('Failed to add to cart');
            }
            return response.json();
        })
        .then(() => alert('Product added to cart!'))
        .catch(error => console.error('Error adding to cart:', error));
    }

    // --- Product Loading ---
    function loadProducts(category = null, searchTerm = null) {
        let url = 'http://localhost:8080/products/search?';
        if (category) {
            url += `category=${encodeURIComponent(category)}&`;
        }
        if (searchTerm) {
            url += `searchTerm=${encodeURIComponent(searchTerm)}`;
        }

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error(`Network response was not ok: ${response.statusText}`);
                return response.json();
            })
            .then(data => {
                const products = data.content;
                productGrid.innerHTML = '';
                if (!products || products.length === 0) {
                    productGrid.innerHTML = '<p>No products found.</p>';
                    return;
                }
                products.forEach(product => {
                    const productCard = document.createElement('div');
                    productCard.className = 'product-card';
                    const ratingDisplay = `
                        <div class="star-rating">
                            <span>${'★'.repeat(Math.round(product.averageRating))}${'☆'.repeat(5 - Math.round(product.averageRating))}</span>
                            <span>(${product.reviewCount} reviews)</span>
                        </div>
                    `;
                    const imageDisplay = product.imageUrl
                        ? `<img src="${product.imageUrl}" alt="${product.name}" class="product-image">`
                        : '<div class="product-image-placeholder">No Image</div>';
                    productCard.innerHTML = `
                        ${imageDisplay}
                        <h2>${product.name}</h2>
                        <p style="font-size: 0.8em; color: #888;">ID: ${product.id}</p>
                        ${ratingDisplay}
                        <p>${product.description}</p>
                        <p class="price">$${product.price.toFixed(2)}</p>
                        <p>Category: ${product.categoryName || 'N/A'}</p>
                    `;
                    const cartButton = document.createElement('button');
                    cartButton.textContent = 'Add to Cart';
                    cartButton.onclick = () => addToCart(product.id);
                    productCard.appendChild(cartButton);
                    productGrid.appendChild(productCard);
                });
            })
            .catch(error => {
                console.error('Error fetching products:', error);
                productGrid.innerHTML = '<p>Could not load products.</p>';
            });
    }

    window.loadProductsByCategory = (category) => loadProducts(category, null);

    searchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        loadProducts(null, searchInput.value);
    });

    // --- Initial Load ---
    updateUIForAuthState();
    loadProducts();
});
