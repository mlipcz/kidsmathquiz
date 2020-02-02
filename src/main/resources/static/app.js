var stompClient = null;
var socket = null;
var shortName = "";
var interval = -1;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#shortName").prop("disabled", !connected);
    $("#frequentUser1").prop("disabled", connected);
    $("#frequentUser2").prop("disabled", connected);
/*
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    */
}

function connect() {

    // create the SockJS WebSocket-like object
	socket = new SockJS('/kidsmathquiz-stomp-chat');

	console.log("socket: "+socket);

	// specify that we're using the STOMP protocol on the socket
    stompClient = Stomp.over(socket);

    // implement the behavior we want whenever the client connects to the server (-or- user connects to chat app client by joining a group)
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        // subscribe to topic and create the callback function that handles updates from the server
        stompClient.subscribe("/topic/names", function (greeting) {
        	showJoinedName(JSON.parse(greeting.body).content);
        });

        stompClient.subscribe("/topic/chats", function (greeting) {
            showMessage(JSON.parse(greeting.body).content);
        });

        stompClient.subscribe('/topic/questions', function (greeting) {
            showQuestion(JSON.parse(greeting.body));
        });

        stompClient.subscribe('/topic/errors', function (greeting) {
            showErrors(JSON.parse(greeting.body).content);
        });

        stompClient.subscribe("/topic/left", function (greeting) {
            showLeftName(JSON.parse(greeting.body).content);
        });

        sendName();
    });

}

function disconnect() {
    if (stompClient !== null) {
        sendLeftName();
    	$("#membersChat").append("<tr><td>" + shortName + " odeszła</td></tr>");
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showQuestion(message) {
    $("#question").html(message.question);
    for (i=0; i<5; i++) {
        $("#answer"+i).html(message.answers[i]);
        $("#answer"+i).prop("disabled", false);
    }
    $( "#progressbar" ).css({width: "100%"});
    if (interval !== -1) {
        clearInterval(interval);
    }
    interval = setInterval(frame, 100);
    var width = 100;
    function frame() {
      if (width <= 0) {
        clearInterval(interval);
      } else {
        width = width-2;
        $( "#progressbar" ).css({width: width+"%"});
      }
    }
}

function showMessage(message) {
    console.log("message='"+message+"'");
    if (message === "Start") {
        $("#main-content").hide(500);
        $("#game").show(500);
    } else if (message === "GOOD") {
        $("#completed").append("<div class='box box-good'></div>&nbsp;");
    } else if (message === "WRONG") {
        $("#completed").append("<div class='box box-wrong'></div>&nbsp;");
    } else {
        //$("#message").val("");
    }
}

function sendMessage() {
  console.log("shortName="+shortName+", message="+$("#message").val());
  stompClient.send("/app/chat", {}, JSON.stringify({'senderName': $("#shortName").val(), 'message': $("#message").val()}));
}

function sendSpecialMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({'senderName': $("#shortName").val(), 'message': "Start"})); // special message
}

function sendName() {
    shortName =  $("#shortName").val();
    stompClient.send("/app/join", {}, JSON.stringify({'senderName': $("#shortName").val(), 'message': $("#shortName").val()}));
}

function sendLeftName() {
    stompClient.send("/app/left", {}, JSON.stringify({'senderName': $("#shortName").val(), 'message': $("#shortName").val()}));
}

function sendAnswer(i) {
    console.log("shortName="+shortName);
    stompClient.send("/app/chat", {}, JSON.stringify({'senderName': shortName, 'message': i}));
    for (i=0; i<5; i++)
        $( "#answer"+i ).prop("disabled", true);
}



function showJoinedName(message) {
    $("#membersChat").append("<tr><td>" + message + " just joined</td></tr>");
    loadUsers();
}

function showLeftName(message) {
    $("#membersChat").append("<tr><td>" + message + " just left3</td></tr>");
    loadUsers();
}

function showErrors(message) {
	$("#errorMessages").html("<tr><td>" + message + "</td></tr>");
}

function loadUsers(){
    $.ajax({
      url: "rest/users"
    }).done(function(a,b,c) {
        d = JSON.parse(a);
        $("#membersList").html(d.users.join(", "));
        if (d.users.length == 0) {
            $("#sendSpecialMessage").addClass("disabled");
        } else {
            $("#sendSpecialMessage").removeClass("disabled");
        }
    });
}

$(function () {
    $("form").on('submit', function (e) { e.preventDefault(); });

    $( "#connect" ).click(function() { connect(); });

    $( "#disconnect" ).click(function() { disconnect(); });

    $( "#send" ).click(function() { sendMessage(); });

    $( "#sendSpecialMessage" ).click(function() { sendSpecialMessage(); });

    $( "#frequentUser1" ).click(function() { $("#shortName").val("Klara"); connect(); });
    $( "#frequentUser2" ).click(function() { $("#shortName").val("Lila"); connect(); });

    for (i=0; i<5; i++)
        $( "#answer"+i ).click(function() { console.log(this.innerHTML); sendAnswer(this.innerHTML); });

	loadUsers();
});

