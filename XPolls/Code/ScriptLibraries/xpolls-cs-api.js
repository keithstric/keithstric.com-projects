var XPolls = function() {
	
	var xpolls = {
		selectedQuestion: null,
		selectedAnswer: null,
		getNsfUrl: function() {
			var url = location.href;
			var nsfUrl = url.substring(0,url.indexOf(".nsf")+4);
			return nsfUrl;
		},
		getRelativeUrl: function() {
			var path = location.pathname;
			var relativeUrl = path.substring(0, path.indexOf(".nsf")+4);
			return relativeUrl;
		},
		getUrlParameter: function(paramName) {
			var strReturn = "";
			var strHref = window.location.href;
			if (strHref.indexOf("?") > -1 ){
				var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
				var aQueryString = strQueryString.split("&");
				for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
					if ( 
						aQueryString[iParam].indexOf(paramName.toLowerCase() + "=") > -1 ){
						var aParam = aQueryString[iParam].split("=");
						strReturn = aParam[1];
						break;
					}
				}
			}
			return unescape(strReturn);
		},
		/**
		 * @arguments[] - An array of arguments as listed below with their respective array index
		 * 		[0] - The event handler ID
		 * 		[1] - The client side id you want to partially refresh or "@none" to not refresh anything or "@full" to do a full refresh
		 * 		[2] - Options object with functions like onStart, onComplete and onError
		 */
		executeOnServer: function () {
		    // must supply event handler id or we're outta here....
		    if (!arguments[0])
		        return false;
		    // the ID of the event handler we want to execute
		    var functionName = arguments[0];

		    // OPTIONAL - The Client Side ID that you want to partial refresh after executing the event handler
		    var refreshId;
		    if (arguments[1]) {
		    	if (arguments[1] == "@full") {
		    		refreshId = XSP.findForm(arguments[1]);
		    	}else if (arguments[1] == "@none"){
		    		refreshId = "@none";
		    	}else{
		    		refreshId = arguments[1];
		    	}
		    }else{
		    	refreshId = "@none";
		    }
		    var form = (arguments[1]) ? XSP.findForm(arguments[1]) : dojo.query('form')[0];

		    // OPTIONAL - Options object contianing onStart, onComplete and onError functions for the call to the 
		    // handler and subsequent partial refresh
		    var options = (arguments[2]) ? arguments[2] : {};

		    // Set the ID in $$xspsubmitid of the event handler to execute
		    dojo.query('[name="$$xspsubmitid"]')[0].value = form.id + ':' + functionName;
		    XSP._partialRefresh("post", form, refreshId, options);
		}
	}
	return xpolls;
}();