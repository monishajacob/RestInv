const registerForm = document.getElementById('register-form')
const registerFirstname = document.getElementById('register-firstname')
const registerLastname = document.getElementById('register-lastname')
const registerEmail = document.getElementById('register-email')
const registerPhone = document.getElementById('register-phone')
const registerPassword = document.getElementById('register-password')
let loginForm = document.getElementById('login-form')
let loginEmail = document.getElementById('login-email')
let loginPassword = document.getElementById('login-password')
let modalBody = document.getElementById('modal-content-body')
let hiddenButton = document.getElementById('btnShowPopup')
const headers = {
    'Content-Type': 'application/json'
}

const baseUrl = 'http://localhost:8080/api/v1/restinv'

function ShowPopup() {
    hiddenButton.click();
}

const handleSignUp = async (e) => {
    e.preventDefault()


    let bodyObj = {
        firstName: registerFirstname.value,
        lastName: registerLastname.value,
        email: registerEmail.value,
        phone: registerPhone.value,
        password: registerPassword.value
    }

    const response = await fetch(`${baseUrl}/register`, {
        method: "POST",
        body: JSON.stringify(bodyObj),
        headers: headers
    })
        .catch(err => console.error(err.message))

    const responseArr = await response.json()


    if (response.status === 200) {
        modalBody.innerHTML = responseArr[0];
        ShowPopup();
    }
    registerForm.reset();
}

registerForm.addEventListener("submit", handleSignUp)

const handleLogin = async (e) => {
    e.preventDefault()

    let bodyObj = {
        email: loginEmail.value,
        password: loginPassword.value
    }

    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        body: JSON.stringify(bodyObj),
        headers: headers
    })
        .catch(err => console.error(err.message))

    console.log(response)

    const responseArr = await response.json()

    if (response.status === 200) {
        console.log("cookie set")
        document.cookie = `userId=${responseArr[1]}`
        if (responseArr[0].startsWith("http")) {
            window.location.replace(responseArr[0])
        } else {
            modalBody.innerHTML = responseArr[0];
            ShowPopup();
        }

    }
    loginForm.reset();
}

loginForm.addEventListener("submit", handleLogin)