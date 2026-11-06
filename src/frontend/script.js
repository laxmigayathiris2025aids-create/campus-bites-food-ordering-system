const shopMenus = {
    "REC MART": [
        { foodId: 1, shopId: 1, name: "Burger", price: 50, icon: "🍔" },
        { foodId: 2, shopId: 1, name: "Pizza", price: 100, icon: "🍕" },
        { foodId: 3, shopId: 1, name: "Sandwich", price: 30, icon: "🥪" }
    ],

    "REC CAFE": [
        { foodId: 4, shopId: 2, name: "Biscuits", price: 10, icon: "🍪" },
        { foodId: 5, shopId: 2, name: "Chips", price: 20, icon: "🍟" },
        { foodId: 6, shopId: 2, name: "Chocolate", price: 25, icon: "🍫" }
    ],

    "REC HUTCAFE": [
        { foodId: 7, shopId: 3, name: "Noodles", price: 40, icon: "🍜" },
        { foodId: 8, shopId: 3, name: "Pasta", price: 60, icon: "🍝" }
    ]
};

const shopCards = document.querySelectorAll(".shop-card");

shopCards.forEach((card) => {
    const button = card.querySelector("button");

    button.addEventListener("click", () => {
        const shopName = card.querySelector("h3").textContent.trim();
        const menuItems = shopMenus[shopName];

        const menuTitle = document.getElementById("menu-title");
        const menuDescription = document.getElementById("menu-description");
        const foodContainer = document.getElementById("food-container");

        menuTitle.textContent = shopName + " Menu";
        menuDescription.textContent = "Choose your favourite food from " + shopName;

        foodContainer.innerHTML = "";

        menuItems.forEach((item) => {
            foodContainer.innerHTML += `
                <div class="food-card">
                    <div class="food-image">${item.icon}</div>
                    <h3>${item.name}</h3>
                    <p>Fresh and delicious ${item.name}</p>
                    <h4>₹${item.price}</h4>
                    <button onclick="addToCart(${item.foodId}, ${item.shopId}, '${item.name}', ${item.price})">
    Add to Cart
</button>
                </div>
            `;
        });

        document.getElementById("food-menu").scrollIntoView({
            behavior: "smooth"
        });
    });
});
let cart = [];

function addToCart(foodId, shopId, name, price) {

    const existingItem = cart.find(item => item.foodId === foodId);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({
            foodId: foodId,
            shopId: shopId,
            name: name,
            price: price,
            quantity: 1
        });
    }

    updateCart();
}

function updateCart() {

    const cartItems = document.getElementById("cart-items");
    const cartTotal = document.getElementById("cart-total");

    cartItems.innerHTML = "";

    let total = 0;

    if (cart.length === 0) {
        cartItems.innerHTML = "<p>Your cart is empty.</p>";
    }

    cart.forEach(item => {

        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        cartItems.innerHTML += `
            <div class="cart-item">
                <p>${item.name} - ₹${item.price} x ${item.quantity}</p>
                <p>Subtotal: ₹${itemTotal}</p>
            </div>
        `;
    });

    cartTotal.textContent = "Total: ₹" + total;
}
// Open student login
function openLogin() {
    document.getElementById("login-container").style.display = "flex";
    document.getElementById("login-box").style.display = "block";
    document.getElementById("register-box").style.display = "none";
}

// Close student login
function closeLogin() {
    document.getElementById("login-container").style.display = "none";
}

// Student login
// Student Login
async function studentLogin() {

    const email = document.getElementById("student-email").value.trim();
    const password = document.getElementById("student-password").value;

    const message = document.getElementById("login-message");

    if (email === "" || password === "") {
        message.textContent = "Please enter email and password!";
        message.style.color = "red";
        return;
    }

    const params = new URLSearchParams({
        email: email,
        password: password
    });

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        const result = await response.text();

        message.textContent = result;
        message.style.color = response.ok ? "green" : "red";

    } catch (error) {
        message.textContent = "Cannot connect to backend!";
        message.style.color = "red";
    }
}
// Open registration form
function openRegister() {
    document.getElementById("login-container").style.display = "flex";
    document.getElementById("login-box").style.display = "none";
    document.getElementById("register-box").style.display = "block";
}

// Close registration form
function closeRegister() {
   document.getElementById("login-container").style.display = "none";
}

// Student registration
async function studentRegister() {
    const registerNumber = document.getElementById("register-number").value.trim();
    const name = document.getElementById("register-name").value.trim();
    const email = document.getElementById("register-email").value.trim();
    const password = document.getElementById("register-password").value;

    const message = document.getElementById("register-message");

    if (registerNumber === "" || name === "" ||
        email === "" || password === "") {
        message.textContent = "Please fill all the fields!";
        message.style.color = "red";
        return;
    }

    const params = new URLSearchParams({
        registerNumber: registerNumber,
        name: name,
        email: email,
        password: password
    });

    try {
        const response = await fetch("http://localhost:8080/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        const result = await response.text();

        message.textContent = result;
        message.style.color = response.ok ? "green" : "red";

    } catch (error) {
        message.textContent = "Cannot connect to backend. Start Java server.";
        message.style.color = "red";
    }
}
document.getElementById("checkout-btn").addEventListener("click", async function () {

    const registerNumber = document.getElementById("checkout-register").value.trim();

    if (registerNumber === "") {
        alert("Please enter your register number!");
        return;
    }

    if (cart.length === 0) {
        alert("Your cart is empty!");
        return;
    }

    // Keep cart details before clearing
    const orderedItems = [...cart];

    const items = cart.map(item => {
        return `${item.foodId},${item.shopId},${item.price},${item.quantity}`;
    }).join(";");

    const params = new URLSearchParams({
        registerNumber: registerNumber,
        items: items
    });

    try {
        const response = await fetch("http://localhost:8080/checkout", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            alert(errorMessage);
            return;
        }

        const result = await response.json();

        const shopNames = {
            1: "REC MART",
            2: "REC CAFE",
            3: "REC HUTCAFE"
        };

        let billHTML = `
            <div class="bill-card">
                <h2>🧾 CAMPUS BITES</h2>
                <p class="bill-success">✓ Order Placed Successfully!</p>

                <hr>

                <p><strong>Register Number:</strong> ${result.registerNumber}</p>

                <h3>Order Details</h3>
        `;

        result.orders.forEach(order => {
            billHTML += `
                <p><strong>Shop:</strong> ${shopNames[order.shopId]}</p>
                <p><strong>Reference:</strong> ${order.referenceCode}</p>
            `;
        });

        billHTML += `<hr><h3>Food Items</h3>`;

        orderedItems.forEach(item => {
            billHTML += `
                <p>${item.name} × ${item.quantity}
                = ₹${item.price * item.quantity}</p>
            `;
        });

        billHTML += `
                <p class="bill-total">
                    Grand Total: ₹${result.grandTotal}
                </p>
                <p class="bill-success">Thank you for ordering! ❤️</p>
            </div>
        `;

        document.getElementById("bill-container").innerHTML = billHTML;

        cart = [];
        updateCart();

        document.getElementById("checkout-register").value = "";

        document.getElementById("bill-container").scrollIntoView({
            behavior: "smooth"
        });

    } catch (error) {
        console.error(error);
        alert("Cannot connect to backend!");
    }
});
async function loadMyOrders() {

    const registerNumber = document
        .getElementById("myorders-register").value.trim();

    const ordersList = document.getElementById("orders-list");

    if (registerNumber === "") {
        ordersList.textContent = "Please enter your register number!";
        return;
    }

    const params = new URLSearchParams({
        registerNumber: registerNumber
    });

    try {
        const response = await fetch("http://localhost:8080/myorders", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        const result = await response.text();

        ordersList.textContent = result;

    } catch (error) {
        ordersList.textContent = "Cannot connect to backend!";
    }
}