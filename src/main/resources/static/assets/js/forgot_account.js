var emailFlag_id = false; 
var verifyFlag_id = false; 
var timer_id, endTime_id = -1;

$("#inputEmail_ID").focus(function(){
    var oMsg = $("#err_email_ID");              
    hideMsg(oMsg);
});

$("#btnEmail_ID").click(function(){
    emailFlag_id = false;
    var oMsg = $("#err_email_ID");
    var email = $("#inputEmail_ID").val();

    var isEmail = /^([0-9a-zA-Z_\.-]+)@([0-9a-zA-Z_-]+)(\.[0-9a-zA-Z_-]+){1,2}$/;
    $("#btnEmail_ID").text("다시받기");
    if (!isEmail.test(email)) {
        showErrorMsg(oMsg, "올바른 이메일 형식을 입력해주세요.");
        btnConfirm_ID_onoff();
    }else{
        checkEmail_ID();
    }
});

$("#btnConfirm_ID").click(function(){
    if(emailFlag_id && !verifyFlag_id){
        $("#btnConfirm_ID").text("다시받기");
        checkEmail_ID();
    }else if(emailFlag_id && verifyFlag_id){
        $('#modalEmail_ID').modal('toggle')
    }     
});

function btnConfirm_ID_onoff(){
    var btn = document.getElementById('btnFindId');
    if(emailFlag_id){
        btn.classList.remove('disabled', 'btn-disable');
        btn.disabled = false;
    }else{
        btn.classList.add('disabled', 'btn-disable');
        btn.disabled = 'disabled';
    }
}


function isValidPasswd(str) {
    var cnt = 0;
    if (str == "")
        return false;

    var retVal = checkSpace(str);
    if (retVal)
        return false;

    if (str.length < 8)
        return false;

    for (var i = 0; i < str.length; ++i) {
        if (str.charAt(0) == str.substring(i, i + 1))
            ++cnt;
    }
    if (cnt == str.length) {
        return false;
    }

    var isPW = /^[A-Za-z0-9`\-=\\\[\];',\./~!@#\$%\^&\*\(\)_\+|\{\}:"<>\?]{8,16}$/;
    if (!isPW.test(str)) {
        return false;
    }

    return true;
}

function checkSpace(str) {
    if (str.search(/\s/) != -1) {
        return true;
    } else {
        return false;
    }
}

function showErrorMsg(obj, msg) {
    obj.html(msg);
    obj.parent().css("visibility","visible")
}

function hideMsg(obj) {
    obj.parent().css("visibility","hidden")
}

function checkEmail_ID() {
    if(emailFlag_id) 
        return false;
    else if(timer_id != undefined){
        clearInterval(timer_id);
        timer_id = undefined;
    }
    var email = $("#inputEmail_ID").val();
    var oMsg = $("#err_email_ID");

    emailFlag_id = false;
    timer_id = 1;
    document.getElementById('loading_img').classList.remove('d-none');
    $.ajax({
        type:"GET",
        async: true,
        url: "/client/request?mode=findId&input=" + email,
        error : function(){
            emailFlag_id = false;
            btnConfirm_ID_onoff();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                emailFlag_id = true;
                if(timer_id==1){
                    $("#inputEmail_ID").attr('readonly', 'readonly');
                    $('#spanEmail').text($("#inputEmail_ID").val() + " 으");
                    $('#modalEmail_ID').modal('toggle')
                    document.getElementById('sectionFindID').classList.remove('d-none');
                    document.getElementById('loading_img').classList.add('d-none');
                    endTime_id = new Date((Date.now()) + 5*60000);
                    
                    timer_id = setInterval(function(){
                        var timeRemain = Math.floor((endTime_id - Date.now())/1000);                        
                         if(timeRemain <= 0){
                             clearInterval(timer_id);                             
                             $("#inputEmail_ID").attr('readonly', false);
                             $("#btnConfirm_ID").text('인증하기');
                             document.getElementById('sectionFindID').classList.add('d-none');
                             timer_id = undefined;
                             emailFlag_id = false;
                             btnConfirm_ID_onoff();
                         }
                         $("#spanTime_ID").text(
                            Math.floor(timeRemain/60) +':' + (timeRemain%60 < 10 ? '0'+timeRemain%60 : timeRemain%60)
                        );
                     },1000);
                }
            } else {
                emailFlag_id = false;
                document.getElementById('loading_img').classList.add('d-none');
                $("#inputEmail_ID").removeAttr('readonly');
                if(timer_id==1)
                    timer_id = undefined;
                if(result == "fail_invaild")
                    showErrorMsg(oMsg, "등록되지 않은 이메일입니다.");
                else
                    showErrorMsg(oMsg, "인증코드 전송에 실패했습니다.");
            }
            btnConfirm_ID_onoff();
        }
    });
    return true;
}

function showErrorMsg(obj, msg) {
    obj.html(msg);
    obj.parent().css("visibility","visible")
}

function hideMsg(obj) {
    obj.parent().css("visibility","hidden")
}

// $('#modalEmail_ID').on('show.bs.modal', function (event) {
//     var button = $(event.relatedTarget) 
//     var modal = $(this)
//     modal.find('#spanEmail').text($("#inputEmail_ID").val() + " 으");
// })

/////////////////////////////////////////////////////////////////////////////////
var pwFlag = false;
var emailFlag_pwd = false;
var verifyFlag_pwd = false; 
var timer_pwd, endTime_pwd = -1;
$("#btnConfirm_ID").click(function(){
    emailFlag_pwd = false;
    var oMsg = $("#err_id_Pwd");
    var id = $("#inputID_Pwd").val();

    var isID = /^[a-z][a-z0-9_\-]{4,19}$/;
    if (!isID.test(id)) {
        showErrorMsg(oMsg, "올바른 아이디 형식을 입력해주세요.");
        btnConfirm_Pwd_onoff();
    }else{
        checkEmail_Pwd(id);
    }
});

function btnConfirm_Pwd_onoff(){
    var btn = document.getElementById('btnConfirm_Pwd');
    if(emailFlag_pwd){
        btn.classList.remove('disabled', 'btn-disable');
        btn.disabled = false;
    }else{
        btn.classList.add('disabled', 'btn-disable');
        btn.disabled = 'disabled';
    }
}

function checkEmail_Pwd(id) {
    if(emailFlag_pwd) 
        return false;
    else if(timer_pwd != undefined){
        clearInterval(timer_pwd);
        timer_pwd = undefined;
    }
    var oMsg = $("#err_id_Pwd");

    emailFlag_pwd = false;
    timer_pwd = 1;
    document.getElementById('loading_img').classList.remove('d-none');
    $.ajax({
        type:"GET",
        async: true,
        url: "/client/request?mode=changePassword&input=" + id,
        error : function(){
            emailFlag_pwd = false;
            btnConfirm_Pwd_onoff();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                emailFlag_pwd = true;
                if(timer_pwd==1){
                    var inputID_Pwd = $("#inputID_Pwd");
                    var sectionFindPwd = document.getElementById('sectionFindPwd');

                    inputID_Pwd.attr('readonly', 'readonly');
                    $('#spanEmail').text(id + " 계정과 연동된 이메일");
                    $('#modalEmail_ID').modal('toggle')
                    sectionFindPwd.classList.remove('d-none');
                    document.getElementById('loading_img').classList.add('d-none');
                    endTime_pwd = new Date((Date.now()) + 5*60000);
                    
                    timer_pwd = setInterval(function(){
                        var timeRemain = Math.floor((endTime_pwd - Date.now())/1000);                        
                        if(timeRemain <= 0){
                             clearInterval(timer_pwd);                             
                             inputID_Pwd.attr('readonly', false);
                             sectionFindPwd.classList.add('d-none');
                             timer_pwd = undefined;
                             emailFlag_pwd = false;
                             btnConfirm_Pwd_onoff();
                         }
                         $("#spanTime_Pwd").text(
                            Math.floor(timeRemain/60) +':' + (timeRemain%60 < 10 ? '0'+timeRemain%60 : timeRemain%60)
                        );
                     },1000);
                }
            } else {
                emailFlag_pwd = false;
                document.getElementById('loading_img').classList.add('d-none');
                $("#inputID_Pwd").removeAttr('readonly');
                if(timer_pwd==1)
                    timer_pwd = undefined;
                if(result == "fail_invaild")
                    showErrorMsg(oMsg, "등록되지 않은 아이디입니다.");
                else
                    showErrorMsg(oMsg, "인증코드 전송에 실패했습니다.");
            }
            btnConfirm_Pwd_onoff();
        }
    });
    return true;
}

$("#inputPwd1").keyup(function() {
    var oMsg = $("#err_message_pwd");
    var password1 = $("#inputPwd1").val();
    var password2 = $("#inputPwd2").val();
    
    if(password1 == ""){
        hideMsg(oMsg);
    }
    else if (!isValidPasswd(password1)) {
        showErrorMsg(oMsg,"8~16 글자의 숫자, 영문자, 특수문자를 조합한 비밀번호를 사용하세요");
        pwFlag = false;
    }else{
        hideMsg(oMsg);
        pwFlag = (password1 == password2);
    }
    checkInput();
});

$("#inputPwd2").keyup(function() {
    var oMsg = $("#err_message_pwd2");
    var password1 = $("#inputPwd1").val();
    var password2 = $("#inputPwd2").val();
    
    if (!(password1 == password2)) {
        pwFlag = false;
        showErrorMsg(oMsg,"비밀번호가 일치하지 않습니다.");
    }else if(isValidPasswd(password1)){
        pwFlag = true;
        hideMsg(oMsg);
    }else{
        hideMsg(oMsg);
        pwFlag = false;
    }
    checkInput();
});

function checkInput() {
    var btn = document.getElementById('btnConfirm_change');
    if (emailFlag_pwd && verifyFlag_pwd && pwFlag) {
        btn.classList.remove('disabled', 'btn-disable');
        btn.disabled = false;
        return true;
    } else {
        btn.classList.add('disabled', 'btn-disable');
        btn.disabled = 'disabled';
        return false;
    }
}