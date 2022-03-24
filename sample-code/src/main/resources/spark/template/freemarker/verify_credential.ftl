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
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
          crossorigin="anonymous">


    <!-- Latest compiled and minified JavaScript -->
    <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
            integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
            integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
            crossorigin="anonymous"></script>
    <link rel="stylesheet" media="all" href="/css/application.css" data-turbolinks-track="false"/>
    <script src="/js/shocardjs.js" data-turbolinks-track="false"></script>
    <meta name="csrf-param" content="authenticity_token"/>
    <meta name="csrf-token"
          content="ExWIw4tC2nBRQtD/L3Nz8KIrfJ0CeFPpX3Q2d8fukS4Jb1alo1bDTDa6fU20J9PoY4unFZu0Blg7gy6tZmIZfg=="/>
</head>
<body>

<div class="container">

    <div class="row mt-4">
        <div class="col-md-8">
            <h2><a href="/">Credentials Demo Server</a></h2>
        </div>
    </div>

    <div class="row mt-2">
        <div class="col-md-8">
            <ul class="nav nav-tabs">
                <li class="nav-item">
                    <a class="nav-link" href="/">Issue</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="/verify">Verify</a>
                </li>
            </ul>

            <div class="card">
                <div class="card-header h5">
                    Verify Credential
                </div>
                <div class="card-body text-center">

                    <div class="col-md-12 col-xs-12 col-sm-12">
                        <div id="qrcode">
                            <img style="width: 250px;" src="${qr_url}">
                            <br/><b>Scan this QR code to share your Employment Credential for verification</b>
                        </div>
                    </div>
                    <div class="col-md-12 col-sm-12 col-xs-12" id="share-details" style="display: none;">
                        <div id="shared-data">
                        </div>
                        <button class="btn btn-danger">Revoke Credential</button>
                    </div>

                </div>
                <div class="card-footer text-muted text-right small">
                    Service ApplicationInstance ID: ${shocard_id}
                </div>
            </div>

            <script>
                $(document).ready(function () {
                    pollSession("${web_session_id}", "registrations");
                });
            </script>

        </div>
    </div>

</div>

</body>
</html>