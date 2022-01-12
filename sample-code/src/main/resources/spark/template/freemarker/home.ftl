<!DOCTYPE html>
<!--
##  ShoCard
##
## SHOCARD CONFIDENTIAL
## __________________
## (C) COPYRIGHT 2016 ShoCard, Inc. All Rights Reserved.
## NOTICE: All information contained herein is the property of ShoCard, Inc.
## The intellectual and technical concepts contained herein are proprietary to
## ShoCard, Inc., and may be covered by U.S. and Foreign Patents, patents
## in process, and are protected by trade secret or copyright law.
## Dissemination or reproduction of this material is strictly forbidden unless
## prior written permission is obtained from ShoCard, Inc.
##
-->
<html>
<head>
  <title>ShoCard Demo</title>

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>

  <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
        integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
        crossorigin="anonymous">

  <!-- Optional theme -->
  <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
        integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
        crossorigin="anonymous">

  <!-- Latest compiled and minified JavaScript -->
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
          integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
          crossorigin="anonymous"></script>
  <link rel="stylesheet" media="all" href="/css/application.css" data-turbolinks-track="false"/>
  <script src="/js/shocardjs.js" data-turbolinks-track="false"></script>
  <meta name="csrf-param" content="authenticity_token"/>
  <meta name="csrf-token"
        content="ExWIw4tC2nBRQtD/L3Nz8KIrfJ0CeFPpX3Q2d8fukS4Jb1alo1bDTDa6fU20J9PoY4unFZu0Blg7gy6tZmIZfg=="/>
</head>
<body>

<div class="container">
  <div class="row header">
    <div class="col-md-8 col-md-offset-2">
    </div>
  </div>

  <div class="row header">
    <div class="col-md-8 col-md-offset-2">
      <h2><a href="/">ShoCard Demo Server</a></h2>
    </div>
  </div>

  <div class="row">
    <div class="col-md-8 col-md-offset-2">

      <div class="panel panel-default">
        <div class="panel-heading">
          <h4>Please scan the QR code to exchange data with the demo server.</h4>
        </div>
        <div class="panel-body">

          <div class="col-md-12 col-xs-12 col-sm-12">
            <div id="qrcode">
              <img style="width: 250px;" src="${qr_url}">
            </div>
          </div>
          <div class="col-md-12 col-sm-12 col-xs-12" id="share-details" style="display: none;">
            <div class="panel panel-default">
              <div class="panel-heading">
                Shared Data
              </div>
              <div class="panel-body" id="shared-data">
              </div>
            </div>
          </div>

        </div>
      </div>

      <script>
        $(document).ready(function () {
          pollSession("${web_session_id}", "registrations");
        });
      </script>

    </div>
  </div>

  <div class="row footer">
    <div class="col-md-8 col-md-offset-2">
      <h5 class="pull-right">Powered by ShoCard</h5>
    </div>
    <div class="col-md-8 col-md-offset-2">
      <div class="pull-right">ApplicationInstance ID: ${shocard_id}</div>
    </div>
  </div>
</div>

</body>
</html>