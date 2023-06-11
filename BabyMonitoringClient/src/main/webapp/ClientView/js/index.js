const baseURL = "http://localhost:8080/BabyMonitoringServer_war_exploded/api/milkData/";

// Fetches data on page load
window.onload = function () {
    getMilkData().then(data => {
        renderDataInTheTable(data)
        const [earliest, latest] = findEarliestLatestDates(data)
        getLineChart({from: earliest, to: latest})
    })

    getAverageMilk({}).then(value => document.getElementById("average-milk").innerHTML = Math.round((value + Number.EPSILON) * 100) / 100 + "&nbsp;(<b>ml</b>)")
    getAverageFeedingDuration().then(value => document.getElementById("average-duration").innerHTML = Math.round((value + Number.EPSILON) * 100) / 100 + "&nbsp;<b>minutes</b>")
    // getLineChart({}).then(value => console.log(value))
    document.getElementById("edit-record-form").style.visibility = "hidden"
}

// Used to make API request to the endpoints
const makeRequest = async (url, type, params) => {
    let data;
    await fetch(url + "?" + new URLSearchParams(params), {
        method: type,
        headers: {
            "Authorization": "Basic " + btoa("admin:admin123"),
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(function (response) {
        if (response.status !== 200) {
            return [];
        } else {
            return response.json();
        }
    })
        .then(function (result) {
            data = result;
        })
        .catch(function (error) {
            console.log('Request failed', error);
        })

    return data;
}

// Adds new data and refreshes content
function addNewDataHandler(event) {
    event.preventDefault();

    let startTime = processDateForRequest(new Date(event.target.feedingStart.value))
    let endTime = processDateForRequest(new Date(event.target.feedingEnd.value))
    let milkAmount = event.target.milkConsumed.value

    // Make request for data
    makeRequest(
        baseURL + "add",
        "POST",
        {
            amount: milkAmount,
            from: startTime,
            to: endTime
        }
    ).then(() => {
            location.reload()
        }
    )
}

// Updates a specific record
function updateRecordHandler(event) {
    event.preventDefault();

    const id = document.getElementById("record-id").value
    const startTime = processDateForRequest(new Date(event.target.updateStart.value))
    const endTime = processDateForRequest(new Date(event.target.updateEnd.value))
    const milkAmount = event.target.updateMilk.value

    // Make update request
    makeRequest(
        baseURL + "update",
        "PUT",
        {
            id: id,
            amount: milkAmount,
            from: startTime,
            to: endTime
        }
    ).then(() => {
            location.reload()
        }
    )
}

// Deletes a specific record
function deleteRecord() {
    const recordID = document.getElementById("delete-id").value

    // Make delete request
    makeRequest(
        baseURL + "delete/" + recordID,
        "Delete",
        {}
    ).then(() => {
            location.reload()
        }
    )
}

// Makes API request to get all data
async function getMilkData() {
    return await makeRequest(
        baseURL + "list",
        "GET",
        {});
}

// Gets average milk consumption over a specified interval
async function getAverageMilk(intervals) {
    return await makeRequest(
        baseURL + "getAverageConsumption",
        "GET",
        intervals);
}

// Gets total average feeding duration
async function getAverageFeedingDuration() {
    return await makeRequest(
        baseURL + "getAverageFeedingSessionDuration",
        "GET",
        {});
}

// Gets line chart
function getLineChart(intervals){

    fetch(baseURL + "chart?" + new URLSearchParams(intervals), {
        method: "GET",
        headers: {
            "Authorization": "Basic " + btoa("admin:admin123")
        }
    }).then(response => {return response.blob()})
        .then(blob => {
            let img = URL.createObjectURL(blob);
            document.getElementById("chart-img").setAttribute("src", img);
        })
}

// Displays data in the form of a table
function renderDataInTheTable(data) {

    const table = document.getElementById("feeding-data-table")

    // Create table elements
    const tableHeader = document.createElement("thead")
    tableHeader.id = "table-head-row"
    table.appendChild(tableHeader)

    const tableBody = document.createElement("tbody")
    tableBody.id = "table-body"
    table.appendChild(tableBody)

    const tableHeaderRow = document.createElement("tr");
    tableHeaderRow.id = "table-head-row"

    if (data.length <= 0) {
        const header = document.createElement("th");
        header.textContent = "No data found";
        tableHeaderRow.appendChild(header);
    } else {
        const headers = ["ID", "Feeding start", "Feeding end", "Milk consumed (ml)"];
        headers.forEach(headerText => {
            const header = document.createElement("th");
            header.textContent = headerText;
            tableHeaderRow.appendChild(header);
        });
        tableHeader.appendChild(tableHeaderRow);

        const fragment = document.createDocumentFragment();

        data.forEach(record => {
            const {id, startTime, finishTime, milkConsumed} = record;

            const row = document.createElement("tr");
            row.id = "row-" + id;

            const rowHeader = document.createElement("th");
            rowHeader.textContent = id;
            rowHeader.scope = "row";
            row.appendChild(rowHeader);

            const rowData = document.createElement("td");
            rowData.textContent = processDateForView(startTime);
            row.appendChild(rowData);

            const rowData2 = document.createElement("td");
            rowData2.textContent = processDateForView(finishTime);
            row.appendChild(rowData2);

            const rowData3 = document.createElement("td");
            rowData3.textContent = milkConsumed;
            row.appendChild(rowData3);

            // Delete button
            const actionsCell = document.createElement("td");
            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.classList.add("btn", "btn-danger", "btn-sm", "rounded-pill")
            deleteButton.setAttribute("data-bs-toggle", "modal")
            deleteButton.setAttribute("data-bs-target", "#staticBackdrop")
            deleteButton.addEventListener("click", () => document.getElementById("delete-id").value = record.id);

            // Edit button
            const editButton = document.createElement("button");
            editButton.textContent = "Edit";
            editButton.classList.add("btn", "btn-link", "btn-sm",)
            editButton.style.color = "orange";
            editButton.addEventListener("click", () => editHandler(record));

            actionsCell.appendChild(deleteButton);
            actionsCell.appendChild(editButton);
            row.appendChild(actionsCell);


            fragment.appendChild(row);
        });

        tableBody.appendChild(fragment);
    }
}

// Allows the selected record to be edited (for update)
function editHandler(record) {
    document.getElementById("edit-record-form").style.visibility = "visible"

    const recordId = document.getElementById("record-id")
    recordId.value = record.id

    const label = document.getElementById("edit-label")
    label.innerText = "Edit record " + record.id + ": "

    const startDateInput = document.getElementById("updateStart")
    startDateInput.value = formatUTCtoGMT(record.startTime)

    const endDateInput = document.getElementById("updateEnd")
    endDateInput.value = formatUTCtoGMT(record.finishTime)

    const milkInput = document.getElementById("updateMilk")
    milkInput.value = record.milkConsumed
}

// Helper function that allows us to add hours to a date

Date.prototype.addHours = function (h) {
    this.setTime(this.getTime() + (h * 60 * 60 * 1000));
    return this;
}

// Helper function that formats a UTC date to GMT
function formatUTCtoGMT(dateString) {
    const date = new Date(dateString.slice(0, -5)).addHours(3);
    return date.toISOString().slice(0, 16);
}

// Helper functions that formats date (for display)
function processDateForView(date) {
    const newDate = new Date(date.slice(0, -5));
    return ((newDate.getDate()) + '/' + (newDate.getMonth() + 1) + '/' + newDate.getFullYear() + ' ' + newDate.getHours() + ':' + newDate.getMinutes() + ':' + newDate.getSeconds());
}

// Helper function that formats date (for request)
function processDateForRequest(date) {
    const padL = (nr, len = 2, chr = `0`) => `${nr}`.padStart(2, chr);

    return `${
        padL(date.getFullYear())}-${
        padL(date.getMonth() + 1)}-${
        padL(date.getDate())} ${
        padL(date.getHours())}:${
        padL(date.getMinutes())}:${
        padL(date.getSeconds())}`;
}

// Refreshes data shown at the table
function refreshDataHandler(event) {
    event.preventDefault();

    let fromInterval = event.target.from.value
    let toInterval = event.target.to.value

    let fromDate, toDate;
    if (fromInterval) {
        fromDate = processDateForRequest(new Date(fromInterval))
    } else {
        fromDate = "";
    }

    if (toInterval) {
        toDate = processDateForRequest(new Date(toInterval))
    } else {
        toDate = "";
    }

    // Make request for data
    makeRequest(
        baseURL + "list",
        "GET",
        {
            from: fromDate,
            to: toDate
        }
    ).then(data => {

        const tableHead = document.getElementById("table-head-row")
        const tableBody = document.getElementById("table-body")
        // Removing existing data (in table)
        tableHead.remove()
        tableBody.remove()
        renderDataInTheTable(data)


        const [earliest, latest] = findEarliestLatestDates(data)
        // Get new line chart
        getLineChart({
            from: earliest,
            to: latest
        })
    })

    // Make request for average milk consumed
    makeRequest(
        baseURL + "getAverageConsumption",
        "GET",
        {
            from: fromDate,
            to: toDate
        }
    ).then(value => {
        document.getElementById("average-milk").innerHTML = Math.round((value + Number.EPSILON) * 100) / 100 + "&nbsp;(<b>ml</b>)"
    })


    // Show refresh message (Toast)
    const toast = new bootstrap.Toast(document.getElementById("refresh-toast"))
    toast.show()
}

// Helper function that finds the earliest date
function findEarliestLatestDates(data){
    let startDates = [],endDates = []
    data.forEach(record => {
        startDates.push(new Date(record.startTime.slice(0, -5)));
        endDates.push(new Date(record.finishTime.slice(0, -5)));
    })

    const earliest = processDateForRequest(new Date(Math.min(...startDates)));
    const latest = processDateForRequest(new Date(Math.max(...endDates)));

    return [earliest, latest];
}


