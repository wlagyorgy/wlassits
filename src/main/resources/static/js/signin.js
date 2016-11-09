function onSignIn(googleUser) {
    var id_token = googleUser.getAuthResponse().id_token;

    var xhr = new XMLHttpRequest();
    // Do not send to your backend! Use an ID token instead.
    var params = "&idtoken=" + id_token;

    xhr.open('POST', 'http://localhost:8080/signin');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(params);
    xhr.onreadystatechange = function () {
        window.location.href = "main.html";
    }

}
function onSignInFailure() {
    alert("Sign in failed!");
    console.log(error);
}