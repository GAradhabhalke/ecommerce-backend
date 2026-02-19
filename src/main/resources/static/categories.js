function loadCategories() {
    const categoryList = document.getElementById('category-list');

    fetch('http://localhost:8080/categories')
        .then(response => response.json())
        .then(categories => {
            categoryList.innerHTML = '';
            const ul = document.createElement('ul');
            categories.forEach(category => {
                ul.appendChild(createCategoryListItem(category));
            });
            categoryList.appendChild(ul);
        })
        .catch(error => {
            console.error('Error fetching categories:', error);
            categoryList.innerHTML = 'Could not load categories.';
        });
}

function createCategoryListItem(category) {
    const li = document.createElement('li');
    
    const link = document.createElement('a');
    link.href = '#';
    link.textContent = category.name;
    link.onclick = (e) => {
        e.preventDefault();
        window.loadProductsByCategory(category.name);
    };
    
    li.appendChild(link);

    if (category.children && category.children.length > 0) {
        const childrenUl = document.createElement('ul');
        category.children.forEach(child => {
            childrenUl.appendChild(createCategoryListItem(child));
        });
        li.appendChild(childrenUl);
    }

    return li;
}

document.addEventListener('DOMContentLoaded', loadCategories);
