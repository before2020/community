function like(btn, entityType, entityId, entityUserId) {
    $.post(
        CONTEXT_PATH + '/like',
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId},
        function (data) {
            var json = $.parseJSON(data);
            if (json.code === 0) {
                $(btn).children("i").text(json.likeCount);
                $(btn).children("b").text(json.likeStatus===1?"已赞":"赞");
            } else {
                alert(data.msg);
            }
        }
    )
}