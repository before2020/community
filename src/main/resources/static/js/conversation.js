$(function(){
	$("#sendBtn").click(send_message);
});

function send_message() {
	$('#sendModal').modal("hide");
	var toName = $('#recipient-name').val();
	var content = $('#message-text').val();
	$.post(
		CONTEXT_PATH + '/message/send',
		{toName: toName, content: content},
		function (data) {
			var json = $.parseJSON(data);
			if (json.code === 0) {
				$('#hintBody').text('发送成功');
			} else {
				$('#hintBody').text('发送失败');
			}
			$('#hintModal').modal("show");
			setTimeout(function () {
				$('#hintModal').modal("hide");
				window.location.reload();
			}, 2000);
		}
	);
}

function delete_msg(id) {
	if (window.confirm('确定要删除吗？')) {
		$.get(
			CONTEXT_PATH + '/message/delete',
			{id: id},
			function (data) {
				var json = $.parseJSON(data);
				if (json.code === 0) {
					$('#li-' + id).remove();
					alert('删除成功！');
				} else {
					alert('删除失败！');
				}
			}
		)
	}

}

function goBack() {
	window.location.href = CONTEXT_PATH + "/conversations/all";
}