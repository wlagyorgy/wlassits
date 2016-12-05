$(document).on('change', ':file', function() {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);
});

$(document).ready( function() {
    $(':file').on('fileselect', function(event, numFiles, label) {
        console.log(numFiles);
        console.log(label);
        var filename = document.createElement("LABEL");
        filename.innerHTML = label;
        filename.className += "label";
        filename.style = "font-size: 18px; position: absolute; margin-top: auto; color: black; padding-top:10px;";
        var span = document.getElementById("uploadFileName");
        if (span.firstChild) {
            span.removeChild(span.firstChild);
        }
        span.appendChild(filename);
    });
});