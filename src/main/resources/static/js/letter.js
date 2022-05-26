$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	// 点击发送按钮后，隐藏发送框页面
	$("#sendModal").modal("hide");
	// 处理完数据后
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			if (data.code === 0) {
				$("#hintBody").text("发送成功!");
			} else {
				$("#hintBody").text(data.msg);
			}
			// 显示提示框页面，并在2s后关闭
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	var letterId = $("#hiddenLetterId").val();
	$.post(
		CONTEXT_PATH + "/letter/del",
		{"letterId":letterId},
		function (data) {
			data = $.parseJSON(data);
			if (data.code === 0) {
				$(this).parents(".media").remove();
				location.reload();
			} else {
				alert(data.msg);
			}
		}
	);

}