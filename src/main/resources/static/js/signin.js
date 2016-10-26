function onSignIn(googleUser) {
    var id_token = googleUser.getAuthResponse().id_token;
    var profile = googleUser.getBasicProfile();

    var xhr = new XMLHttpRequest();
    var params = "username=" + profile.getName() + "&imageURL=" + profile.getImageUrl() + "&idtoken=" + id_token;

    xhr.open('POST', 'http://localhost:8080/signin');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        console.log('Signed in as: ' + profile.getName());
    };


    xhr.send(params);
    xhr.onreadystatechange = function () {
        window.location.href = "upload.html";
    }

}
function onSignInFailure() {
    alert("Sign in failed!");
    console.log(error);
}