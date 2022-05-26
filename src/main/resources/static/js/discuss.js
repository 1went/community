$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
})

function like(current,entityType,entityId,entityUserId,postId) {  // current是当前点击事件触发的对象
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $(current).children("i").text(data.likeCount);
                $(current).children("b").text(data.likeStatus===1?'已赞':'赞');
            } else {
                alert(data.msg);
            }
        }
    );
}
// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg)
            }
        }
    );
}
// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg)
            }
        }
    );
}
// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg)
            }
        }
    );
}