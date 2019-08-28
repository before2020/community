function follow(entityId, hasFollowed) {
	// 关注
	if(!hasFollowed) {
		$.get(
			CONTEXT_PATH + '/follow',
			{"entityType":3, "entityId":entityId},
			function (data) {
				var json = $.parseJSON(data);
				if (json.code === 0) {
					window.location.reload();
				} else {
					alert(json.msg);
				}
			}
		)
	}
	// 取消关注
	else {
		$.get(
			CONTEXT_PATH + '/unfollow',
			{"entityType": 3, "entityId": entityId},
			function (data) {
				var json = $.parseJSON(data);
				if (json.code === 0) {
					window.location.reload();
				} else {
					alert(json.msg);
				}
			}
		)
	}
}