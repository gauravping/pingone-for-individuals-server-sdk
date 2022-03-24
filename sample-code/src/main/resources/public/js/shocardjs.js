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
        let lastRow = "";
        Object.keys(data).forEach((key) => {
          if (key.includes("Verification Result")) {
            lastRow = '<tr><td colspan="3">' + key + ': ' + data[key] + '</td></tr>';
          } else if (!key.includes('__APPLICATION_INSTANCE_ID__')) {
            const parts = key.split('->');
            if (key.includes('CardImage')) {
              if (data[key].includes('svg')) {
                table += '<tr><td>' + parts[0] + '</td><td>' + parts[1] + '</td><td>' + data[key]
                  + '</td></tr>';
              } else {
                table += '<tr><td>' + parts[0] + '</td><td>Selfie</td><td><img src="data:image/png;base64,'
                  + data[key] + '" width="200"/></td></tr>';
              }
            } else {
              table += '<tr><td>' + parts[0] + '</td><td>' + parts[1] + '</td><td>' + data[key]
                + '</td></tr>';
            }
          }
        });
        table = '<table class="table" style="border: 1px"><tr><th>Card</th><th>Key</th><th>Value</th></tr>'
            + table + lastRow + '</table>';
        $("#shared-data").html(table);
        $('#appInstanceId').val(data['__APPLICATION_INSTANCE_ID__']);
        $('#qrcode').hide();
      }
    } else {
      setTimeout(poll, TIMEOUT);
    }
  }
}

