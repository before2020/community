$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{title: $('#recipient-name').val(), content: $('#message-text').val()},
		function (data) {
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function () {
				$("#hintModal").modal("hide");
				if (data.code === 0) {
					window.location.reload();
				}
			}, 2000);
		}
	)
}