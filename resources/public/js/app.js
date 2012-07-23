
$(document).ready(function() {
    var dropper = $(".dropzone");
    var container = $(".container");
    $(".dropzone").filedrop({
	url: "/parse",
	paramname: "csvfile",
	data: {
	    colname: function() {
		return $("input[name=colname]").attr("value");
	    }
	},
	maxfiles: 1,
	dragOver: function() { dropper.addClass("hover"); },
	dragLeave: function() { dropper.removeClass("hover"); },
	uploadStarted: function(i, file, len) {
	    //console.log(file);
	    container.html("");
	    $(".dropzone").html("Parsing file "+file.name);
	}
    });

    // Websocket connection
    
    var ws = $.websocket("ws://127.0.0.1:8843/socket-server", {
        open: function() { console.log("opened") },
        close: function() { console.log("closed") },
        events: {
	    helo: function(e) {
		//console.log("SOCKET: said Hi! to me...");
	    },
            append_result: function(e) {
		//console.log("SOCKET: need to add some data ...");
                //console.log(e.data)
		var res = "<div class=\"row\">";
		$.each(e.data, function(i,d) {
		    res+= "<div class=\"column\">" + d + "</div>";
		});
		res+= "</div>";
		container.append(res);
		$("div.row:last", container).hide(0).fadeIn(600);
            },
	    complete: function(e) {
		$.noticeAdd({
		    text: "Parsing complete.",
		    stay: true
		});
	    }
        }
    });
    window.ws = ws;
});