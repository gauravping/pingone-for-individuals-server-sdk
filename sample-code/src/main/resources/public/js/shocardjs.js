function pollSession(id, mode) {

  const TIMEOUT = 1000;
  const session_id = id;

  setTimeout(poll, TIMEOUT);

  function poll_failed() {
    console.log("Polling failed, QR: " + session_id);
  }

  function poll() {
    console.log("Polling... QR: " + session_id);
    $.get('/sessions/' + session_id, null, poll_success)
        .fail(poll_failed);
  }

  function poll_success(data) {
    console.log(data.state);
    if (data.state !== "initial") {
      if (mode === "registrations") {
        $("#share-details").slideDown();
        $('#shocardid').val(data['_APPLICATION_INSTANCE_ID_']);
        let table = "";
        Object.keys(data).forEach((key) => {
          if (key.includes('CardImage')) {
            if (data[key].includes('svg')) {
              table += '<tr><td>' + key + '</td><td>' + data[key] + '</td></tr>';
            } else {
              table += '<tr><td>Selfie</td><td><img src="data:image/png;base64,'
                + data[key] + '" width="200"/></td></tr>';
            }
          } else {
            table += '<tr><td>' + key + '</td><td>' + data[key] + '</td></tr>';
          }
        });
        table = '<table class="table" border="1"><tr><th>Key</th><th>Value</th></tr>'
            + table + '</table>';
        $("#shared-data").html(table);
        $('#qrcode').hide();
      }
    } else {
      setTimeout(poll, TIMEOUT);
    }
  }
}

