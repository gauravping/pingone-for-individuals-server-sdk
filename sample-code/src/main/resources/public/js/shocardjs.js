function pollSession(id, mode) {

    var TIMEOUT = 1000;
    var session_id = id;
    var mode = mode;

    setTimeout(poll, TIMEOUT);

    function poll_failed(){
        console.log("Polling failed, QR: " + session_id);
    }

    function poll() {
        console.log("Polling... QR: " + session_id);
        $.get('/sessions/' + session_id, null, poll_success)
            .fail(poll_failed);
    }

    function poll_success(data){
        console.log(data.state);
        if (data.state != "initial")
        {
            if (mode == "registrations") {
              $("#share-details").slideDown();
              $('#shocardid').val(data['_APPLICATION_INSTANCE_ID_']);
              var table = "";
              if (data['CardImage'])
                table += '<tr><td>Selfie</td><td><img src="data:image/png;base64,' + data["CardImage"] + '" width="200"/></td></tr>';
              if (data['First Name'])
                table += '<tr><td>First Name</td><td>' + data["First Name"] + '</td></tr>';
              if (data['Last Name'])
                table += '<tr><td>Last Name</td><td>' + data["Last Name"] + '</td></tr>';
              if (data['Birth Date'])
                table += '<tr><td>Date of Birth</td><td>' + data["Birth Date"] + '</td></tr>';
              if (data['_SERVER_CERTIFICATION_ID_']) {
                table += '<tr><td colspan="2">Cancel Server Issued Certification?&nbsp;<a href="/certifications/cancel/'
                  + data['_SERVER_CERTIFICATION_ID_']
                  + '"><button>Yes</button></td></tr>';
              }
              table = '<table class="table" border="1"><tr><th>Key</th><th>Value</th></tr>' + table + '</table>';
              $("#shared-data").html(table);
              $('#qrcode').hide();
            }
            else {
                window.location.replace("/" + mode + "/ok");
            }
        }
        else
        {
            setTimeout(poll, TIMEOUT);
        }
    }
}

function buttonClick(session_id) {
    username = $("#user_email").val();
    console.log("Notifying " + username + " phone");

    data = {"username": username, "session_id": session_id}
    $.post('/logins/new/', data, null)
}
