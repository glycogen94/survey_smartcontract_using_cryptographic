$('#modalEmail').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) 
    var modal = $(this)
    modal.find('#spanEmail').text($("#inputEmail").val());
})

function toastMessage(msg){
    var toastForm = document.getElementById('frmToast')
    var message = document.getElementById('toastMessage')
    var toast = new bootstrap.Toast(toastForm)

    message.textContent = msg;
    toast.show()
}

function showErrorMsg(id, msg){
    obj = $(id);
    obj.html(msg);
    obj.parent().css("visibility","visible")
    setTimeout(() => obj.parent().css("visibility","hidden"), 5000);
}

$("#inputVerifyCode").keyup(function() {
    var code = $("#inputVerifyCode").val();
    if(code.length == 6){
        document.getElementById('btnVerifyCode').classList.remove('d-none');
        document.getElementById('spanTime').classList.add('d-none');
    }else{
        document.getElementById('spanTime').classList.remove('d-none');
        document.getElementById('btnVerifyCode').classList.add('d-none');
    }
});
        
var timer = undefined, endTime = -1;
function postMessage(jsonData, postData, message){
    var res;
    if(jsonData.mode == "change_email"){
        document.getElementById('loading_img').style.visibility = 'visible';
    }
    
    $.ajax({
        type: "POST",
        url: "/client/user/profile",
        data: postData,
        dataType: 'text',
        error : function(){
            location.href = "/client/login";
        },
        success: function (data) {
            result = JSON.parse(data);
            if(jsonData.mode == "change_email"){
                document.getElementById('loading_img').style.visibility = 'hidden';
                res = (result.res).substr(0,4);
                if(res == "succ"){
                    if(timer != undefined){
                        clearInterval(timer);
                        timer = undefined;
                    }
                    
                    $("#inputEmail").attr('readonly', 'readonly');
                    $("#btnEmailCode").text(message.btnEmail_re)
                    $('#modalEmail').modal('toggle')
                    
                    document.getElementById('sectionLabel').classList.remove('d-none');                
                    endTime = new Date((Date.now()) + 5*60000);                
                    timer = setInterval(function(){
                        var timeRemain = Math.floor((endTime - Date.now())/1000);
                        $("#spanTime").text(
                            Math.floor(timeRemain/60) + ':' + (timeRemain%60 < 10 ? '0' + timeRemain%60 : timeRemain%60)
                        );
                        
                        if(timeRemain < 0){
                            clearInterval(timer);
                            $("#btnEmailCode").text(message.btnEmail)
                            $("#inputEmail").attr('readonly', false);
                            document.getElementById('sectionLabel').classList.add('d-none');
                            timer = undefined;
                        }
                    },1000);
                }
                else if(res =="fail"){                           
                    if(result.res =="failexist")
                        showErrorMsg("#err_message_email", message.emailExist);
                    else
                        showErrorMsg("#err_message_email", message.emailFail);
                }
            }
            else if(jsonData.mode == "verify_email")
            {
                if(result.res == "succ"){
                    document.getElementById('userEmail').textContent = result.email
                    toastMessage(message.emailSucc);
                }else if(result.res == "exist")
                    showErrorMsg("#err_message_email", message.emailExist);
                else if(result.res == "fail")
                    showErrorMsg("#err_message_email", message.emailVerifyFail);
                else
                    showErrorMsg("#err_message_email", message.emailFail);
            }
            else if(jsonData.mode == "change_nickname")
            {
                if (result.res == "succ") {
                    document.getElementById('userName').textContent = jsonData.name;
                    toastMessage(message.nameSucc);
                    res = true;
                }else if(result.res == "sessionExpired"){
                    toastMessage(message.sessionExpire);
                    res = false;
                }else if(result.res == "exist"){
                    showErrorMsg("#err_message_nick", message.nameExist);
                    res = false;
                }else{
                    showErrorMsg("#err_message_nick", message.nameFail);
                    res = false;
                }
            }      
        }
    });

    return res;
}