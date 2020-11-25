function validateCookie() {
    var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 401) {
                    console.log("nah fam");
                    window.location.href = "../login.htm";
                    return false;
            } else if (this.readyState == 4 && this.status == 200) {
                console.log("youre good to go");
                return true;
            }
        };
        xhttp.open("GET", "/controller/cookie", false);
        xhttp.send();
}

function removeCookie() {
    var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                    window.location.href = "../login.htm";
            }
        };
        xhttp.open("GET", "/controller/remove", false);
        xhttp.send();
}