
 const websocket = new WebSocket('ws://192.168.1.9:51502');

websocket.addEventListener('open', (event) => {
    console.log('WebSocket connection opened:', event);
});

websocket.addEventListener('message', (event) => {
    // const output = document.getElementById('output');
    const data = JSON.parse(event.data);
    console.log(data)
    displayJSON(data);
});

websocket.addEventListener('error', (error) => {
    console.error('WebSocket Error:', error);
    new WebSocket('ws://192.168.1.2:51502');

});

websocket.addEventListener('close', (event) => {
    console.log('WebSocket connection closed:', event);
});

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = {
        "sender": "SERVER",
        "cmd": messageInput.value
    }

    if (messageInput.value.trim() !== '') {
        const messageData = message;
        websocket.send(JSON.stringify(messageData));
        //messageInput.value = ''; // Clear the input field
        data ="{'Message','Loading..'}";
        displayJSON(data);

    } else {
        console.log('Message cannot be empty.');
    }
}


function displayJSON(jsonData) {
    var jsonContainer = document.getElementById("json-container");
    jsonContainer.textContent = JSON.stringify(jsonData, null, 2); // The third argument (2) is for indentation
}

