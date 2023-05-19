//Cookie
const cookieArr = document.cookie.split(";")
const userId = cookieArr[0].split("=")[1].trim();

//DOM Elements
const marketForm = document.getElementById("market-form")
const marketContainer = document.getElementById("market-container")// By ID
const propertyContainer = document.getElementById("property-container")// By ID
const userState = document.getElementById("market-state").value
const viewProperty = document.getElementById("view-property")
//Modal Elements
let modalBody = document.getElementById('modal-content-body')
let hiddenButton = document.getElementById('btnShowPopup')


const headers = {
    'Content-Type': 'application/json'
}

const baseUrl = 'http://localhost:8080/api/v1/restinv/market/'


const handleSubmit = async (e) => {
    e.preventDefault()


    const imageUrl = await uploadImage();

    let bodyObj = {
        imageLink: imageUrl,
        // imageLink:"https://cdn.pixabay.com/photo/2019/03/30/10/40/italy-4090933_1280.jpg",
        countyName: document.getElementById("market-county").value,
        state: document.getElementById("market-state").value,
        note: document.getElementById("market-note").value,
    }

    await addMarket(bodyObj);

}

async function addMarket(obj) {
    const response = await fetch(baseUrl + userId, {
        method: "POST",
        body: JSON.stringify(obj),
        headers: headers
    })
        .catch(err => console.error(err.message))
    const responseArr = await response.json()
    if (response.status == 200) {
        modalBody.innerHTML = responseArr[0];
        ShowPopup();
    }
    marketForm.reset();
    getMarkets(userId)
}


async function uploadImage() {
    try {
        const envResponse = await fetch('http://localhost:8080/api/v1/restinv/env');
        const envData = await envResponse.json();
        const cloudName = envData.CloudName;
        const uploadPreset = envData.CloudPreset;
        const cloudUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
        const imageBody = new FormData();
        imageBody.append('file', document.getElementById("upload-image").files[0]);
        imageBody.append('upload_preset', uploadPreset);
        const response = await fetch(cloudUrl, {
            method: 'POST',
            body: imageBody
        });

        if (response.status == 200) {
            const data = await response.json();
            return data.secure_url;
        } else {
            throw new Error(`Failed to upload image: ${response.status}`);
        }
    } catch (err) {
        console.error(err.message);
        return "https://cdn.pixabay.com/photo/2019/03/30/10/40/italy-4090933_1280.jpg";
    }
}


async function getMarkets(userId) {

    await fetch(baseUrl + userId, {
        method: "GET",
        headers: headers
    })
        .then(response => response.json())
        .then(data => createMarketCards(data))
        .catch(err => console.error(err))
}

async function handleDelete(marketId) {
    const response = await fetch(`${baseUrl}${userId}/delete/${marketId}`, {
        method: "DELETE",
        headers: headers
    })
        .catch(err => console.error(err))
    const responseArr = await response.json()
    if (response.status == 200) {
        modalBody.innerHTML = responseArr[0];
        ShowPopup();
    }
    return getMarkets(userId);
}


function seeProperty(marketId) {
    console.log("property cookie")
    document.cookie = `marketId=${marketId}`;
    console.log(document.cookie)
    const url = `http://localhost:8080/api/v1/restinv/properties.html`;
    window.location.href = url;
}

const createMarketCards = (array) => {
    marketContainer.innerHTML = ''
    array.forEach(obj => {

            let marketCard = document.createElement("div")
            marketCard.classList.add("col")
            marketCard.classList.add("market_column")
            marketCard.innerHTML = `

                    <div class="card h-100 text-start">
                    <img class="card-img-top" src="${obj.imageLink}">
                        <div class="card-body justify-content-start">
                            <p class="card-text"><b>County:</b style="font-weight: bolder;"> ${obj.countyName}</p>
                            <p class="card-text "><b style="font-weight: bolder;">State:</b> ${obj.state}</p>
                            <p class="card-text "><b style="font-weight: bolder;">Description:</b> ${obj.note}</p>
                            </div>
                            <div class="card-footer d-flex justify-content-between">
                              <button class="btn btn-delete right" onclick="handleDelete(${obj.marketId
            })">Delete</button>
                              <button class="btn right" id="view-property" onclick="seeProperty(${obj.marketId})" href="http://localhost:8080/api/v1/restinv/properties.html" >View</button>  
                        </div>
                    </div>
                   
                `


            marketContainer.append(marketCard)
        }
    )
}


function handleLogout() {
    var cookies = document.cookie.split("; ");
    for (var c = 0; c < cookies.length; c++) {
        var d = window.location.hostname.split(".");
        while (d.length > 0) {
            var cookieBase = encodeURIComponent(cookies[c].split(";")[0].split("=")[0]) + '=; expires=Thu, 01-Jan-1970 00:00:01 GMT; domain=' + d.join('.') + ' ;path=';
            var p = location.pathname.split('/');
            document.cookie = cookieBase + '/';
            while (p.length > 0) {
                document.cookie = cookieBase + p.join('/');
                p.pop();
            }
            ;
            d.shift();
        }
    }
}

function ShowPopup() {
    hiddenButton.click();
}


function resetIfInvalid(element) {
    //if no value selected, nothing is done.
    if (element.value == "")
        return;
    let options = element.list.options;
    for (let i = 0; i < options.length; i++) {
        if (element.value == options[i].value)
            //option is matched
            return;
    }
    //reset value, no match found
    element.value = "";
}


const states = new Map([
    ['Alabama', '01'],
    ['Alaska', '02'],
    ['Arizona', '04'],
    ['Arkansas', '05'],
    ['California', '06'],
    ['Colorado', '08'],
    ['Connecticut', '09'],
    ['Delaware', '10'],
    ['District of Columbia', '11'],
    ['Florida', '12'],
    ['Georgia', '13'],
    ['Hawaii', '15'],
    ['Idaho', '16'],
    ['Illinois', '17'],
    ['Indiana', '18'],
    ['Iowa', '19'],
    ['Kansas', '20'],
    ['Kentucky', '21'],
    ['Louisiana', '22'],
    ['Maine', '23'],
    ['Maryland', '24'],
    ['Massachusetts', '25'],
    ['Michigan', '26'],
    ['Minnesota', '27'],
    ['Mississippi', '28'],
    ['Missouri', '29'],
    ['Montana', '30'],
    ['Nebraska', '31'],
    ['Nevada', '32'],
    ['New Hampshire', '33'],
    ['New Jersey', '34'],
    ['New Mexico', '35'],
    ['New York', '36'],
    ['North Carolina', '37'],
    ['North Dakota', '38'],
    ['Ohio', '39'],
    ['Oklahoma', '40'],
    ['Oregon', '41'],
    ['Pennsylvania', '42'],
    ['Puerto Rico', '72'],
    ['Rhode Island', '44'],
    ['South Carolina', '45'],
    ['South Dakota', '46'],
    ['Tennessee', '47'],
    ['Texas', '48'],
    ['Utah', '49'],
    ['Vermont', '50'],
    ['Virgin Islands', '78'],
    ['Virginia', '51'],
    ['Washington', '53'],
    ['West Virginia', '54'],
    ['Wisconsin', '55'],
    ['Wyoming', '56'],
]);


function populateCounty() {
    console.log('populateCounty called');
    const userState = document.getElementById("market-state").value;
    const countyList = document.getElementById("countylist");
    countyList.innerHTML = ''; // clear options

    fetch('https://api.census.gov/data/2020/dec/responserate?get=NAME&for=county:*&in=state:' + states.get(userState))
        .then(response => response.json())
        .then(json => populateCountyOptions(json.slice(1)))
        .catch(err => console.log('Request Failed', err));
}

function populateCountyOptions(array) {
    console.log('populateCountyOptions called');
    const countyList = document.getElementById("countylist");
    array.forEach(item => {
        let county = item[0].split(",")[0];
        let option = document.createElement('option');
        option.value = county;
        countyList.appendChild(option);
    });
}

window.onpopstate = function (event) {
    if (location.pathname === '/api/v1/restinv/market.html') {
        // If the URL is market.html, reload the page to its original state
        location.reload();
    } else if (location.pathname === '/api/v1/restinv/market/property') {
        // If the URL is property.html, load the page using AJAX
        loadPage('/api/v1/restinv/market/property');
    }
};


marketForm.addEventListener("submit", handleSubmit)
window.onload = getMarkets(userId)