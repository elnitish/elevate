const apiSelect = document.getElementById("apiSelect");
const paramContainer = document.getElementById("paramContainer");
const paramInput = document.getElementById("paramInput");
const testBtn = document.getElementById("testBtn");
const responseBox = document.getElementById("response");
const statusBox = document.getElementById("status");

// Show input field if API needs a parameter
apiSelect.addEventListener("change", () => {
    if (apiSelect.value.includes("{id}")) {
        paramContainer.style.display = "block";
    } else {
        paramContainer.style.display = "none";
        paramInput.value = "";
    }
});

// Test API function
async function testAPI() {
    let url = apiSelect.value;

    // Replace {id} if provided
    if (url.includes("{id}")) {
        const param = paramInput.value.trim();
        if (!param) {
            alert("Please enter the required parameter!");
            return;
        }
        url = url.replace("{id}", param);
    }

    // Base URL (change this to your backend server)
    const baseUrl = "http://localhost:8080"; 
    const fullUrl = baseUrl + url;
    console.log("Testing URL:", fullUrl);

    responseBox.textContent = "Loading...";
    statusBox.textContent = "";

    try {
        const res = await fetch(fullUrl);
        const data = await res.json();
        console.log("API Response:", data);
        responseBox.textContent = JSON.stringify(data, null, 2);
        statusBox.textContent = res.status + " " + res.statusText;
    } catch (error) {
        responseBox.textContent = "Error: " + error;
        statusBox.textContent = "Failed";
    }
}

testBtn.addEventListener("click", testAPI);
