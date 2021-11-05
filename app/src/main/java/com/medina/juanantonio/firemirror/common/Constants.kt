package com.medina.juanantonio.firemirror.common

object Constants {

    object PreferencesKey {
        const val SPOTIFY_CODE = "spotifyCode"
        const val SPOTIFY_ACCESS_TOKEN = "spotifyAccessToken"
        const val SPOTIFY_REFRESH_TOKEN = "spotifyRefreshToken"
    }

    object Server {
        private const val BLUE_BUTT_HTML_FORM =
            "<html> <head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> " +
                    "<style> * { box-sizing: border-box; } input[type=text], select, textarea { width: 100%; padding: " +
                    "12px; border: 1px solid #ccc; border-radius: 4px; resize: vertical; } label { padding: 12px 12px " +
                    "12px 0; display: inline-block; } input[type=submit] { background-color: #04AA6D; color: white; " +
                    "padding: 12px 20px; border: none; border-radius: 4px; cursor: pointer; float: right; } " +
                    "input[type=submit]:hover { background-color: #45a049; } .container { border-radius: 5px; " +
                    "background-color: #f2f2f2; padding: 20px; } .col-25 { float: left; width: 25%; margin-top: 6px; } " +
                    ".col-75 { float: left; width: 75%; margin-top: 6px; } .row:after { content: \"\"; display: table; " +
                    "clear: both; } @media screen and (max-width: 600px) { .col-25, .col-75, input[type=submit] { " +
                    "width: 100%; margin-top: 0; } } </style> </head> <body> <div class=\"container\"> <form " +
                    "action=\"?\" method=\"get\"> <div class=\"row\"> <h2>DEVICE_NAME</h2> <div class=\"col-25\"> <label " +
                    "for=\"fname\">Alias</label> </div> <div class=\"col-75\"> <input type=\"text\" id=\"alias\" " +
                    "name=\"alias\" placeholder=\"Alias\" value=\"ALIAS_VALUE\"> </div> </div> <div class=\"row\"> <div " +
                    "class=\"col-25\"> <label for=\"lname\">Trigger URL Off</label> </div> <div class=\"col-75\"> " +
                    "<input type=\"text\" id=\"turloff\" name=\"triggerurloff\" placeholder=\"Trigger URL Off\" " +
                    "value=\"TRIGGER_URL_OFF_VALUE\"> </div> </div> <div class=\"row\"> <div class=\"col-25\"> <label for=\"lname\">" +
                    "Trigger URL On</label> </div> <div class=\"col-75\"> <input type=\"text\" id=\"turlon\" " +
                    "name=\"triggerurlon\" placeholder=\"Trigger URL On\" value=\"TRIGGER_URL_ON_VALUE\"> </div> </div> <div class=\"row\" " +
                    "style=\"margin-top: 12px\"> <input type=\"submit\" value=\"Save\"> </div> </form> </div> </body> </html>"

        private const val BLEDOM_HTML_FORM =
            "<html> <head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> " +
                    "<style> * { box-sizing: border-box; } input[type=text], select, textarea { width: 100%; padding: " +
                    "12px; border: 1px solid #ccc; border-radius: 4px; resize: vertical; } label { padding: 12px 12px " +
                    "12px 0; display: inline-block; } input[type=submit] { background-color: #04AA6D; color: white; " +
                    "padding: 12px 20px; border: none; border-radius: 4px; cursor: pointer; float: right; } " +
                    "input[type=submit]:hover { background-color: #45a049; } .container { border-radius: 5px; " +
                    "background-color: #f2f2f2; padding: 20px; } .col-25 { float: left; width: 25%; margin-top: 6px; } " +
                    ".col-75 { float: left; width: 75%; margin-top: 6px; } .row:after { content: \"\"; display: table; " +
                    "clear: both; } @media screen and (max-width: 600px) { .col-25, .col-75, input[type=submit] { " +
                    "width: 100%; margin-top: 0; } } </style> </head> <body> <div class=\"container\"> <form " +
                    "action=\"?\" method=\"get\"> <div class=\"row\"> <h2>DEVICE_NAME</h2> <div class=\"col-25\"> <label " +
                    "for=\"alias\">Alias</label> </div> <div class=\"col-75\"> <input type=\"text\" id=\"alias\" " +
                    "name=\"alias\" placeholder=\"Alias\" value=\"ALIAS_VALUE\"> </div> </div> <div class=\"row\" " +
                    "style=\"margin-top: 12px\"> <input type=\"submit\" value=\"Save\"> </div> </form> </div> </body> </html>"

        fun getHtmlForm(
            deviceName: String,
            alias: String,
            triggerUrlOff: String,
            triggerUrlOn: String
        ): String {
            return BLUE_BUTT_HTML_FORM
                .replace("DEVICE_NAME", deviceName)
                .replace("ALIAS_VALUE", alias)
                .replace("TRIGGER_URL_OFF_VALUE", triggerUrlOff)
                .replace("TRIGGER_URL_ON_VALUE", triggerUrlOn)
        }

        fun getHtmlForm(
            deviceName: String,
            alias: String
        ): String {
            return BLEDOM_HTML_FORM
                .replace("DEVICE_NAME", deviceName)
                .replace("ALIAS_VALUE", alias)
        }
    }
}