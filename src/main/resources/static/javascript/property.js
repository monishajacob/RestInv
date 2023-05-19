//Cookie
const cookieArr = document.cookie.split(";")
const userId = cookieArr[0].split("=")[1].trim();
const marketId = cookieArr[1].split("=")[1].trim();

//DOM Elements

const propertyContainer = document.getElementById("property-container")// By ID
const viewPropertyDetails = document.getElementById("view-property-details")
const ltrForm = document.getElementById('ltr-form')
const strForm = document.getElementById('str-form')
//Modal Elements
let modalBody = document.getElementById('modal-content-body')
let hiddenButton = document.getElementById('btnShowPopup')
const viewPropertyModal = document.getElementById("view-property-modal");

const headers = {
    'Content-Type': 'application/json'
}

const baseUrl = 'http://localhost:8080/api/v1/restinv/market/'


console.log(marketId);


// remove last cookie
window.addEventListener('beforeunload', () => {
    document.cookie = 'marketId=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/';
});


const createLtr = async (e) => {
    e.preventDefault()
    const file = document.getElementById("property-upload-image").files[0]

    const imageUrl = await uploadImage(file);

    let bodyObj = {

        propertyType: document.getElementById("propertyType").value,
        imageLink: imageUrl,
        // imageLink:"https://cdn.pixabay.com/photo/2017/11/16/19/29/cottage-2955582_1280.jpg",
        note: document.getElementById("ltrnote").value,
        purchasePrice: parseFloat(document.getElementById("purchasePrice").value),
        percentDownPayment: parseFloat(document.getElementById("percentDownPayment").value),
        closingCosts: parseFloat(document.getElementById("closingCosts").value),
        repairsRenovationCost: parseFloat(document.getElementById("repairsRenovationCost").value),
        percentInterestRate: parseFloat(document.getElementById("percentInterestRate").value),
        yearsFinanced: parseFloat(document.getElementById("yearsFinanced").value),
        paymentsPerYear: parseFloat(document.getElementById("paymentsPerYear").value),
        propertyTaxesPerYear: parseFloat(document.getElementById("propertyTaxesPerYear").value),
        insurancePerYear: parseFloat(document.getElementById("insurancePerYear").value),
        percentPropertyManagementFee: parseFloat(document.getElementById("percentPropertyManagementFee").value),
        percentMaintenanceCost: parseFloat(document.getElementById("percentMaintenanceCost").value),
        monthlyUtilities: parseFloat(document.getElementById("monthlyUtilities").value),
        hoaPerYear: parseFloat(document.getElementById("hoaPerYear").value),
        ltrNumberOfUnits: parseFloat(document.getElementById("ltrNumberOfUnits").value),
        ltrMonthlyRentalIncome: parseFloat(document.getElementById("ltrMonthlyRentalIncome").value),
        ltrPercentVacancyRate: parseFloat(document.getElementById("ltrPercentVacancyRate").value),
        ltrLeasingCostPerUnit: parseFloat(document.getElementById("ltrLeasingCostPerUnit").value),
        ltrAverageOccupancyYears: parseFloat(document.getElementById("ltrAverageOccupancyYears").value),
        statusName: document.getElementById("statusName").value,
        addressLine: document.getElementById("addressLine").value,
        city: document.getElementById("city").value,
        zipcode: document.getElementById("zipcode").value,
        state: document.getElementById("property-state").value

    }

    await addProperty(bodyObj);

    ltrForm.reset()
}
ltrForm.addEventListener("submit", createLtr)


const createStr = async (e) => {
    e.preventDefault()

    const file = document.getElementById("strproperty-upload-image").files[0]

    const imageUrl = await uploadImage(file);

    let bodyObj = {

        propertyType: document.getElementById("strpropertyType").value,
        imageLink: imageUrl,
        // imageLink:"https://cdn.pixabay.com/photo/2017/11/16/19/29/cottage-2955582_1280.jpg",
        note: document.getElementById("strnote").value,
        purchasePrice: parseFloat(document.getElementById("strpurchasePrice").value),
        percentDownPayment: parseFloat(document.getElementById("strpercentDownPayment").value),
        closingCosts: parseFloat(document.getElementById("strclosingCosts").value),
        repairsRenovationCost: parseFloat(document.getElementById("strrepairsRenovationCost").value),
        percentInterestRate: parseFloat(document.getElementById("strpercentInterestRate").value),
        yearsFinanced: parseFloat(document.getElementById("stryearsFinanced").value),
        paymentsPerYear: parseFloat(document.getElementById("strpaymentsPerYear").value),
        propertyTaxesPerYear: parseFloat(document.getElementById("strpropertyTaxesPerYear").value),
        insurancePerYear: parseFloat(document.getElementById("strinsurancePerYear").value),
        percentPropertyManagementFee: parseFloat(document.getElementById("strpercentPropertyManagementFee").value),
        percentMaintenanceCost: parseFloat(document.getElementById("strpercentMaintenanceCost").value),
        monthlyUtilities: parseFloat(document.getElementById("strmonthlyUtilities").value),
        hoaPerYear: parseFloat(document.getElementById("strhoaPerYear").value),
        strAverageDailyRate: parseFloat(document.getElementById("strAverageDailyRate").value),
        strPercentOccupancyRate: parseFloat(document.getElementById("strPercentOccupancyRate").value),
        strStartupCosts: parseFloat(document.getElementById("strStartupCosts").value),
        strPercentBookingFees: parseFloat(document.getElementById("strPercentBookingFees").value),
        strPercentLodgingTaxOther: parseFloat(document.getElementById("strPercentLodgingTaxOther").value),
        strMonthlySupplies: parseFloat(document.getElementById("strMonthlySupplies").value),
        strMonthlyCableInternet: parseFloat(document.getElementById("strMonthlyCableInternet").value),
        strOtherMonthlyCosts: parseFloat(document.getElementById("strOtherMonthlyCosts").value),
        statusName: document.getElementById("strstatusName").value,
        addressLine: document.getElementById("straddressLine").value,
        city: document.getElementById("strcity").value,
        zipcode: document.getElementById("strzipcode").value,
        state: document.getElementById("strproperty-state").value

    }
    await addProperty(bodyObj);
    strForm.reset()
}

strForm.addEventListener("submit", createStr)

async function addProperty(obj) {
    const response = await fetch(`${baseUrl}${userId}/${marketId}/create/property`, {
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
    ltrForm.reset()
    strForm.reset();
    getProperties(marketId)
}

async function getProperties(marketId) {
    const response = await fetch(`${baseUrl}${userId}/${marketId}/property`, {
        method: "GET",
        headers: headers
    }).then(response => response.json())
        .then(data => {
            createPropertyCards(data);
        })
        .catch(err => console.error(err))

}

const createPropertyCards = (array) => {
    propertyContainer.innerHTML = ''
    array.forEach(obj => {

            let propertyCard = document.createElement("div")
            propertyCard.classList.add("col")
            propertyCard.classList.add("property_column")
            propertyCard.innerHTML = `
        <div class="card h-100 text-start" >
        <img class="card-img-top"
                    src="${obj.imageLink}">
            <div class="card-body justify-content-start" >
                <p class="card-text"><b style="font-weight: bolder;">Property Type:</b> ${obj.propertyType}</p>
                <p class="card-text "><b style="font-weight: bolder;">Address:</b> ${obj.addressLine}</p>
                <p class="card-text "><b style="font-weight: bolder;">Purchase Price:</b> $ ${obj.purchasePrice}</p>
                </div>
                <div class="card-footer d-flex justify-content-between">
                    <button class="btn right" style="padding: 3px; margin: 5px; margin-top: 10px;"
                        onclick="handleDelete(${obj.userPropertyId})">Delete</button>
                    <button class="btn right" style="padding: 3px; margin: 5px; margin-top: 10px;"
                        id="view-property" data-bs-toggle="modal"
                        data-bs-target="#view-property-modal" onclick="seeProperty(${obj.userPropertyId})">View
                        </button>
                </div>
            
            </div>
        </div>
                `

            propertyContainer.append(propertyCard)
        }
    )
}

async function seeProperty(propertyId) {

    const response = await fetch(`${baseUrl}${userId}/${marketId}/property/${propertyId}`, {
        method: "GET",
        headers: headers
    }).then(response => response.json())
        .then(data => {
            createPropertyDetails(data, propertyId);
        })
        .catch(err => console.error(err))
}

const createPropertyDetails = (array, propertyId) => {
    array.forEach(obj => {
        if (obj.propertyType === "LTR") {
            createPropertyDetailsLtr(obj, propertyId)
        } else {
            createPropertyDetailsStr(obj, propertyId)
        }
    })

}

async function createPropertyDetailsLtr(obj, propertyId) {


    viewPropertyDetails.innerHTML = `
    <div class="modal-header ">
        <h4 class="modal-title ">Long Term Rental Cash On Cash Return Calculator</h4>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body" style="background-color:rgb(240, 243, 248);">
        <div class="row">
            <div class="col">
                <table class="table table-bordered table-hover table-striped-columns "
                    style="background-color: whitesmoke;">
                    <thead>
                        <tr class="fw-bolder" style="background-color: #7fa7db;">
                            <th scope="row">Cash On Cash Return : </th>
                            <td id="cashOnCashPercent">${obj.cashOnCashReturn}</td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th scope="row">Address </th>
                            <td id="viewAddress">${obj.address}</td>
                        </tr>
                        <tr>
                            <th scope="row">Status </th>
                            <td>
                                <select class="form-select" id="viewStatus"
                                    aria-label="Default select example" required>
                                    <option selected>${obj.status}</option>
                                    <option value="Active">Active</option>
                                    <option value="Coming-Soon">Coming-Soon</option>
                                    <option value="Accepting-Backup-Offers">
                                        Accepting-Backup-Offers</option>
                                    <option value="Under-Contract/Pending">
                                        Under-Contract/Pending</option>
                                    <option value="Sold">Sold</option>
                                </select>


                            </td>
                        </tr>
                        <tr>
                            <th scope="row">Property Type</th>
                            <td id="viewPropertyType">${obj.propertyType}</td>
                        </tr>
                        <tr class="table-primary">
                            <th colspan="2">Purchase data</th>
                        </tr>
                        <tr>
                            <th scope="row">Purchase Price ($)</th>
                            <td id="viewPurchasePrice">${obj.purchaseData["Purchase Price ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Down Payment (%)</th>
                            <td id="viewDownPayment">${obj.purchaseData["Down Payment (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Closing Costs ($)</th>
                            <td id="viewClosingCost">${obj.purchaseData["Closing Costs ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Repairs/Renovation ($)</th>
                            <td id="viewRepairsRenovation">${obj.purchaseData["Repairs/Renovation ($)"]}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Loan data</th>
                        </tr>
                        <tr>
                            <th scope="row">Amount Financed ($)</th>
                            <td id="viewAmountFinanced">${obj.loanData["Amount Financed ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Interest Rate (%)</th>
                            <td id="viewInterestRate">${obj.loanData["Interest Rate (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Years</th>
                            <td id="viewYears">${obj.loanData["Years"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Payments per Year</th>
                            <td id="viewPaymentYearly">${obj.loanData["Payments per Year ($)"]}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Property data</th>
                        </tr>
                        <tr>
                            <th scope="row">Number of Units</th>
                            <td id="viewNoUnits">${obj.propertyData["Number of Units"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Property Taxes/Year ($)</th>
                            <td id="viewPropertyTaxYearly">${obj.propertyData["Property Taxes/Year ($)"]}</td>
                        </tr>

                        <tr>
                            <th scope="row">Insurance/Year ($)</th>
                            <td id="viewInsuranceYearly">${obj.propertyData["Insurance/Year ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">HOA/Year ($)</th>
                            <td id="viewHoaYearly">${obj.propertyData["HOA per year ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Monthly Gross Rental Income ($)</th>
                            <td id="viewMonthlyRentalIncome">${obj.propertyData["Monthly Gross Rental Income ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Vacancy Rate (%)</th>
                            <td id="viewVacancyRate">${obj.propertyData["Vacancy Rate (%)"]}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Property management data</th>
                        </tr>
                        <tr>
                            <th scope="row">Property Management Fee (%)</th>
                            <td id="viewPercentPropertyManagementFee">${obj.propertyManagementData["Property Management Fee (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Leasing Cost per Unit ($)</th>
                            <td id="viewLeasingCost">${obj.propertyManagementData["Leasing Cost per Unit ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Average Occupancy (years)</th>
                            <td id="viewAverageOccupancy">${obj.propertyManagementData["Average Occupancy (years)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Maintenance Costs (%)</th>
                            <td id="viewManagementCost">${obj.propertyManagementData["Maintenance Costs (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Monthly Utilities/Other ($)</th>
                            <td id="viewMonthlyUtilities">${obj.propertyManagementData["Monthly Utilities/Other ($)"]}</td>
                        </tr>

                    </tbody>
                </table>
            </div>
            <div class="col">
                <table class="table table-bordered table-hover table-striped-columns "
                    style="background-color: whitesmoke;">
                    <thead>
                        <tr>
                            <th class="table-primary fw-bolder" colspan="2">Monthly Net Operating Income
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th scope="row">Gross Rental Income </th>
                            <td id="viewMonthlyGrossIncome">${obj.monthlyNetOperatingIncome["Monthly Gross Rental Income ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Average Vacancy</th>
                            <td id="viewMonthlyAvgVacancy">${obj.monthlyNetOperatingIncome["Average Vacancy ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Net Rental Income </th>
                            <td id="viewNetIncome">${obj.monthlyNetOperatingIncome["Net Rental Income ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Property Management</th>
                            <td id="viewMonthlyPropertyManagement">${obj.monthlyNetOperatingIncome["Property Management ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Leasing</th>
                            <td id="viewLeasing">${obj.monthlyNetOperatingIncome["Leasing ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Maintenance</th>
                            <td id="viewMonthlyMaintenece">${obj.monthlyNetOperatingIncome["Maintenance ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Utilities</th>
                            <td id="viewUtilities">${obj.monthlyNetOperatingIncome["Monthly Utilities/Other ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Property Taxes </th>
                            <td id="viewMonthlyPropertyTax">${obj.monthlyNetOperatingIncome["Property Taxes ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Insurance</th>
                            <td id="viewMonthlyInsurance">${obj.monthlyNetOperatingIncome["Insurance ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">HOA</th>
                            <td id="viewMonthlyHoa">${obj.monthlyNetOperatingIncome["Monthly HOA ($)"]}</td>
                        </tr>
                        <tr class="tablecell">
                            <th scope="row">Monthly Expenses </th>
                            <td id="viewMonthlyExpenses">${obj.monthlyNetOperatingIncome["Monthly Expenses ($)"]}</td>
                        </tr>
                        <tr class="tablecell fw-bolder">
                            <th scope="row">Net Operating Income</th>
                            <td id="viewMonthlyNetIncome">${obj.monthlyNetOperatingIncome["Monthly Net Operating Income ($)"]}</td>
                        </tr>
                    </tbody>
                </table>


                <table class="table table-bordered table-hover table-striped-columns "
                    style="background-color: whitesmoke;">
                    <thead>
                        <tr>
                            <th class="table-primary fw-bolder" colspan="3"> Monthly/Annual Cashflow</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th scope="row"></th>
                            <td>Monthly</td>
                            <td>Annual</td>
                        </tr>
                        <tr>
                            <th scope="row">Net Operating Income </th>
                            <td id="viewMonthlyOpIncome">${obj.monthlyAndAnnualCashflow["Monthly Net Operating Income ($)"]}</td>
                            <td id="viewAnualOpIncome">${obj.monthlyAndAnnualCashflow["Yearly Net Operating Income ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Principal and Interest </th>
                            <td id="viewMonthlyPI">${obj.monthlyAndAnnualCashflow["Monthly Principal and Interest ($)"]}</td>
                            <td id="viewAnualPI">${obj.monthlyAndAnnualCashflow["Yearly Principal and Interest ($)"]}</td>
                        </tr>

                        <tr class="tablecell">
                            <th scope="row">Cashflow </th>
                            <td id="viewMonthlyCashflow">${obj.monthlyAndAnnualCashflow["Monthly Cashflow ($)"]}</td>
                            <td id="viewAnualCashflow">${obj.monthlyAndAnnualCashflow["Yearly Cashflow ($)"]}</td>
                        </tr>

                    </tbody>
                </table>

                <table class="table table-bordered table-hover table-striped-columns "
                    style="background-color: whitesmoke;">
                    <thead>
                        <tr>
                            <th class="table-primary fw-bolder" colspan="2">Total Investment</th>
                        </tr>
                    </thead>
                    <tbody>

                        <tr>
                            <th scope="row">Down Payment</th>
                            <td id="viewDownPaymentAmt">${obj.totalInvestment["Down Payment ($)"]}</td>

                        </tr>
                        <tr>
                            <th scope="row">Closing Costs</th>
                            <td id="viewClosingCostAmt">${obj.totalInvestment["Closing Costs ($)"]}</td>

                        </tr>
                        <tr>
                            <th scope="row">Repairs/Renovation</th>
                            <td id="viewRepairsRenovationAmt">${obj.totalInvestment["Repairs/Renovation ($)"]}</td>

                        </tr>
                        <tr class="tablecell">
                            <th scope="row">Total Investment Amount </th>
                            <td id="viewTotalInvestmentAmt">${obj.totalInvestment["Total Investment ($)"]}</td>
                        </tr>

                    </tbody>
                </table>

                <table class="table table-bordered table-hover table-striped-columns "
                    style="background-color: whitesmoke;">
                    <thead>
                        <tr>
                            <th class="table-primary fw-bolder" colspan="1">Note</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="tablecell">
                            <td id="viewNote"> <textarea class="form-control" id="note-details" rows="2">${obj.note}</textarea>
                            </td>
                        </tr>

                    </tbody>
                </table>

            </div>
            <div class="d-flex justify-content-center">
                <button class="btn right" style="padding: 3px; margin: 5px; margin-top: 10px;"
                    onclick="handleUpdateProperty(${propertyId})">Update</button>

            </div>
        </div>
    </div>


        `

    bootstrapModal.show();
}

async function createPropertyDetailsStr(obj, propertyId) {
    viewPropertyDetails.innerHTML = `




    <div class="modal-header ">
        <h4 class="modal-title ">Short Term Rental Cash On Cash Return Calculator</h4>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body" style="background-color:rgb(240, 243, 248);">
        <div class="row">
            <div class="col">
                <table
                    class="table table-bordered table-hover table-striped-columns " style="background-color: whitesmoke;">
                    <thead >
                        <tr class ="fw-bolder" style="background-color: #7fa7db;">
                            <th scope="row">Cash On Cash Return : </th>
                            <td id="cashOnCashPercent" >${obj.cashOnCashReturn}</td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr >
                            <th scope="row" >Address </th>
                            <td id="viewAddress" >${obj.address}</td>    
                        </tr>
                        <tr >
                            <th scope="row" >Status </th>
                            <td  >
                                <select class="form-select" id="viewStatus"
                                    aria-label="Default select example" required>
                                    <option selected>${obj.status}</option>
                                    <option value="Active">Active</option>
                                    <option value="Coming-Soon">Coming-Soon</option>
                                    <option value="Accepting-Backup-Offers">
                                        Accepting-Backup-Offers</option>
                                    <option value="Under-Contract/Pending">
                                        Under-Contract/Pending</option>
                                    <option value="Sold">Sold</option>
                                </select>


                            </td>    
                        </tr>
                        <tr>
                            <th scope="row">Property Type</th>
                            <td id="viewPropertyType" >${obj.propertyType}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Rental income data</th>
                        </tr>
                        <tr>
                            <th scope="row">Average Daily Rate ($)</th>
                            <td id="viewAverageDailyRate" >${obj.rentalIncomeData["Average Daily Rate ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Occupancy Rate (%)</th>
                            <td id="viewOccupancyRate" >${obj.rentalIncomeData["Occupancy Rate (%)"]}</td>
                        </tr>
                        <tr class="table-primary">
                            <th colspan="2">Purchase data</th>
                        </tr>
                        <tr>
                            <th scope="row">Purchase Price ($)</th>
                            <td id="viewPurchasePrice" >${obj.purchaseData["Purchase Price ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Down Payment (%)</th>
                            <td id="viewDownPayment" >${obj.purchaseData["Down Payment ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Closing Costs ($)</th>
                            <td id="viewClosingCost" >${obj.purchaseData["Closing Costs ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Repairs/Renovation ($)</th>
                            <td id="viewRepairsRenovation" >${obj.purchaseData["Repairs/Renovation ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Furniture/Other Startup Costs</th>
                            <td id="viewFurnitureStartupCost" >${obj.purchaseData["Furniture/Other Startup Costs ($)"]}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Loan data</th>
                        </tr>
                        <tr>
                            <th scope="row">Amount Financed ($)</th>
                            <td id="viewAmountFinanced" >${obj.loanData["Amount Financed ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Interest Rate (%)</th>
                            <td id="viewInterestRate" >${obj.loanData["Interest Rate (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Years</th>
                            <td id="viewYears" >${obj.loanData["Years"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Payments per Year</th>
                            <td id="viewPaymentYearly" >${obj.loanData["Payments per Year"]}</td>
                        </tr>
                        <tr class="table-primary fw-bolder">
                            <th colspan="2">Expenses data</th>
                        </tr>
                        <tr>
                            <th scope="row">Property Taxes/Year ($)</th>
                            <td id="viewPropertyTaxYearly" >${obj.expensesData["Property Taxes/Year ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Insurance/Year ($)</th>
                            <td id="viewInsuranceYearly" >${obj.expensesData["Insurance/Year ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">HOA/Year ($)</th>
                            <td id="viewHoaYearly" >${obj.expensesData["HOA per year"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Property Management Fee (%)</th>
                            <td id="viewPercentPropertyManagementFee" >${obj.expensesData["Property Management Fee (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Maintenance Costs (%)</th>
                            <td id="viewManagementCost" >${obj.expensesData["Maintenance Costs (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Booking Fees (%)</th>
                            <td id="viewBookingFees" >${obj.expensesData["Booking Fees (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Lodging Tax/Other (%)</th>
                            <td id="viewLodgingTax" >${obj.expensesData["Lodging Tax/Other (%)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Monthly Supplies ($)</th>
                            <td id="viewMonthlySupplies" >${obj.expensesData["Monthly Supplies ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Monthly Utilities ($)</th>
                            <td id="viewMonthlyUtilities" >${obj.expensesData["Monthly Utilities/Other ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Monthly Cable/Internet ($)</th>
                            <td id="viewMonthlyCable" >${obj.expensesData["Monthly Cable/Internet ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Other Monthly Costs ($)</th>
                            <td id="viewMonthlyOther" >${obj.expensesData["Other Monthly Costs ($)"]}</td>
                        </tr>
                        
                    </tbody>
                </table>
            </div>
            <div class="col">
                <table
                    class="table table-bordered table-hover table-striped-columns " style="background-color: whitesmoke;">
                    <thead >
                        <tr >
                            <th class="table-primary fw-bolder" colspan="2">Monthly Net Operating Income </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr >
                            <th scope="row" >Monthly Income </th>
                            <td id="viewMonthlyIncome" >${obj.monthlyNetOperatingIncome["Monthly Income ($)"]}</td>    
                        </tr>
                        <tr >
                            <th scope="row" >Property Taxes </th>
                            <td  id="viewMonthlyPropertyTax" >${obj.monthlyNetOperatingIncome["Monthly Property Taxes ($)"]}</td>    
                        </tr>
                        <tr>
                            <th scope="row">Insurance</th>
                            <td id="viewMonthlyInsurance" >${obj.monthlyNetOperatingIncome["Monthly Insurance ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">HOA</th>
                            <td id="viewMonthlyHoa" >${obj.monthlyNetOperatingIncome["Monthly HOA ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Property Management</th>
                            <td id="viewMonthlyPropertyManagement" >${obj.monthlyNetOperatingIncome["Monthly Property Management ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Maintenance</th>
                            <td id="viewMonthlyMaintenece" >${obj.monthlyNetOperatingIncome["Monthly Maintenance ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Booking</th>
                            <td id="viewMonthlyBooking" >${obj.monthlyNetOperatingIncome["Monthly Booking ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Lodging Tax/Other</th>
                            <td id="viewMonthlyLodgingTax" >${obj.monthlyNetOperatingIncome["Monthly Lodging Tax/Other ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Supplies</th>
                            <td id="viewSupplies" >${obj.monthlyNetOperatingIncome["Monthly Supplies ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Utilities</th>
                            <td id="viewUtilities" >${obj.monthlyNetOperatingIncome["Monthly Utilities ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Cable/Internet</th>
                            <td id="viewCableInternet" >${obj.monthlyNetOperatingIncome["Monthly Cable/Internet ($)"]}</td>
                        </tr>
                        <tr>
                            <th scope="row">Other</th>
                            <td id="viewOther" >${obj.monthlyNetOperatingIncome["Monthly Other ($)"]}</td>
                        </tr>
                        <tr class="tablecell">
                            <th scope="row">Monthly Expenses </th>
                            <td id="viewMonthlyExpenses" >${obj.monthlyNetOperatingIncome["Monthly Expenses ($)"]}</td>
                        </tr>
                        <tr class="tablecell fw-bolder">
                            <th scope="row">Net Operating Income</th>
                            <td id="viewMonthlyNetIncome" >${obj.monthlyNetOperatingIncome["Monthly Net Operating Income ($)"]}</td>
                        </tr>
                    </tbody>
                </table>


                <table
                    class="table table-bordered table-hover table-striped-columns " style="background-color: whitesmoke;">
                    <thead >
                        <tr >
                            <th class="table-primary fw-bolder" colspan="3"> Monthly/Annual Cashflow</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr >
                            <th scope="row" ></th>
                            <td >Monthly</td> 
                            <td >Annual</td>    
                        </tr>
                        <tr >
                            <th scope="row" >Net Operating Income </th>
                            <td  id="viewMonthlyOpIncome" >${obj.monthlyAndAnnualCashflow["Monthly Net Operating Income ($)"]}</td>    
                            <td  id="viewAnualOpIncome" >${obj.monthlyAndAnnualCashflow["Yearly Net Operating Income ($)"]}</td>  
                        </tr>
                        <tr >
                            <th scope="row" >Principal and Interest </th>
                            <td  id="viewMonthlyPI" >${obj.monthlyAndAnnualCashflow["Monthly Principal and Interest ($)"]}</td>    
                            <td  id="viewAnualPI" >${obj.monthlyAndAnnualCashflow["Yearly Principal and Interest ($)"]}</td>  
                        </tr>
                        
                        <tr class="tablecell">
                            <th scope="row">Cashflow </th>
                            <td id="viewMonthlyCashflow" >${obj.monthlyAndAnnualCashflow["Monthly Cashflow ($)"]}</td> 
                            <td id="viewAnualCashflow" >${obj.monthlyAndAnnualCashflow["Yearly Cashflow ($)"]}</td>  
                        </tr>
                       
                    </tbody>
                </table>

                <table
                    class="table table-bordered table-hover table-striped-columns " style="background-color: whitesmoke;">
                    <thead >
                        <tr >
                            <th class="table-primary fw-bolder" colspan="2">Total Investment</th>
                        </tr>
                    </thead>
                    <tbody>
                        
                        <tr >
                            <th scope="row" >Down Payment</th>
                            <td  id="viewDownPaymentAmt" >${obj.totalInvestment["Down Payment ($)"]}</td>    
                        
                        </tr>
                        <tr >
                            <th scope="row" >Closing Costs</th>
                            <td  id="viewClosingCostAmt" >${obj.totalInvestment["Closing Costs ($)"]}</td>    
                        
                        </tr>
                        <tr >
                            <th scope="row" >Repairs/Renovation</th>
                            <td  id="viewRepairsRenovationAmt" >${obj.totalInvestment["Repairs/Renovation ($)"]}</td>    
                        
                        </tr>
                        <tr >
                            <th scope="row" >Furniture/Other</th>
                            <td  id="viewFurnitureOtherAmt" >${obj.totalInvestment["Furniture/Other Startup Cost ($)"]}</td>    
                        
                        </tr>
                        
                        <tr class="tablecell">
                            <th scope="row">Total Invesment Amount </th>
                            <td id="viewTotalInvestmentAmt" >${obj.totalInvestment["Total Investment ($)"]}</td>  
                        </tr>
                       
                    </tbody>
                </table>

                <table
                    class="table table-bordered table-hover table-striped-columns " style="background-color: whitesmoke;">
                    <thead >
                        <tr >
                            <th class="table-primary fw-bolder" colspan="1">Note</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="tablecell">
                            <td id="viewNote">   <textarea class="form-control" id="note-details" rows="2">${obj.note}</textarea></td> 
                        </tr>
                       
                    </tbody>
                </table>

            </div>
            <div class="d-flex justify-content-center">
                <button class="btn right" style="padding: 3px; margin: 5px; margin-top: 10px;"
                    onclick="handleUpdateProperty(${propertyId})">Update</button>
            </div>
        </div>
    </div>



        `

    bootstrapModal.show();

}

// Show the modal
const bootstrapModal = new bootstrap.Modal(viewPropertyModal);


async function handleUpdateProperty(propertyId) {

    let bodyObj = {
        statusName: document.getElementById('viewStatus').value,
        note: document.getElementById('note-details').value
    }

    await fetch(`${baseUrl}${userId}/${marketId}/property/${propertyId}`, {
        method: "PUT",
        body: JSON.stringify(bodyObj),
        headers: headers
    })
        .catch(err => console.error(err))

    return seeProperty(propertyId);
}


async function handleDelete(propertyId) {
    const response = await fetch(`${baseUrl}${userId}/${marketId}/property/delete/${propertyId}`, {
        method: "DELETE",
        headers: headers
    })
        .catch(err => console.error(err))
    const responseArr = await response.json()
    if (response.status == 200) {
        modalBody.innerHTML = responseArr[0];
        ShowPopup();
    }
    return getProperties(marketId);
}


async function uploadImage(file) {
    try {
        const envResponse = await fetch('http://localhost:8080/api/v1/restinv/env');
        const envData = await envResponse.json();
        const cloudName = envData.CloudName;
        const uploadPreset = envData.CloudPreset;
        const cloudUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
        const imageBody = new FormData();
        imageBody.append('file', file);
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
        return "https://cdn.pixabay.com/photo/2017/11/16/19/29/cottage-2955582_1280.jpg";
    }
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

// modal pop-up
function ShowPopup() {
    hiddenButton.click();
}

// reset datalist if invalid entry
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

window.onload = getProperties(marketId)