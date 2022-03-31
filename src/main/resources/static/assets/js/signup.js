var idFlag = false;
var pwFlag = false;
var nickNameFlag = false;
var emailFlag = false; 
var verifyFlag = false; 
var timer, endTime = -1;

$("#inputID").focusout(function() {
    idFlag = false;
    checkId();
});

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

$("#inputEmail").focus(function(){
    var oMsg = $("#err_message_email");              
    hideMsg(oMsg);
});

$("#btnEmailCode").click(function(){
    emailFlag = false;
    var oMsg = $("#err_message_email");
    var email = $("#inputEmail").val();

    var isEmail = /^([0-9a-zA-Z_\.-]+)@([0-9a-zA-Z_-]+)(\.[0-9a-zA-Z_-]+){1,2}$/;
    if (!isEmail.test(email)) {
        showErrorMsg(oMsg, "올바른 이메일 형식을 입력해주세요.");
        btnConfirm_Enable();
    }else{
        $("#btnEmailCode").text("다시받기")
        checkEmail();
    }
});

$("#inputNickName").focusout(function() {
    nickNameFlag = false;
    checkNickName();
});

$("#inputVerifyCode").focusout(function() {
    verifyFlag = false;
    var oMsg = $("#err_message_code");
    var emailCode = $("#inputVerifyCode").val();

    var isCode = /^[0-9]{6}$/;
    if (!isCode.test(emailCode)) {
        showErrorMsg(oMsg,"인증코드를 확인해 주세요");
    }else{
        verifyEmail();
    }
});

$("#btnConfirm").click(function(){
   if(emailFlag && nickNameFlag && !verifyFlag){
       if(timer==undefined)
           checkEmail();
       else
           verifyEmail();
    }else if(emailFlag && nickNameFlag && verifyFlag){
        $('#modalBIO').modal('toggle')
   }     
});


$("#btnFindId").click(function(){
    if(emailFlag && !verifyFlag){
        if(timer == undefined)
            checkEmail();
        else
            verifyEmail();
     }else if(emailFlag && verifyFlag){
         $('#modalEmail').modal('toggle')
    }     
});
 

function btnConfirm_Enable(){
    var btn = document.getElementById('btnConfirm');
    
    if(timer == undefined && emailFlag && nickNameFlag || emailFlag && nickNameFlag && verifyFlag){
        btn.classList.remove('disabled', 'btn-disable');
        btn.disabled = false;
        return true;
    }else{
        btn.classList.add('disabled', 'btn-disable');
        btn.disabled = 'disabled';
        return false;
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

function checkInput() {
    var btn = document.getElementById('btnNextPage');
    if (idFlag && pwFlag) {
        btn.classList.remove('disabled', 'btn-disable');
        btn.disabled = false;
        return true;
    } else {
        btn.classList.add('disabled', 'btn-disable');
        btn.disabled = 'disabled';
        return false;
    }
}


function checkNickName() {
    if(nickNameFlag) 
        return true;
    
    var oInput = $("#inputNickName");
    var oMsg = $("#err_message_nick");
    var nickName = oInput.val();
    
    if(nickName==""){
        hideMsg(oMsg);
        return false;   
    }
    else if (2 >= nickName.length || nickName.length >= 10){
        showErrorMsg(oMsg,"2-10자를 입력해주세요");
        return false;
    }else
        hideMsg(oMsg);
    
    nickNameFlag = false;
    $.ajax({
        type:"GET",
        url: "/client/request?mode=checkNickName&input=" + nickName ,
        error : function(){
            nickNameFlag = false;
            btnConfirm_Enable();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                nickNameFlag = true;
            } else {
                showErrorMsg(oMsg, "사용중인 닉네임 입니다.");
                nickNameFlag = false;
            }
            btnConfirm_Enable();
        }
    });
    return true;
}


function checkId() {
    if(idFlag) 
        return true;

    var id = $("#inputID").val();
    var oMsg = $("#err_message_id");
    var oInput = $("#inputID");

    var isID = /^[a-z][a-z0-9_\-]{4,19}$/;
    if(id=="")
    {
        hideMsg(oMsg);
        return false;   
    }
    else if (!isID.test(id)) {
        showErrorMsg(oMsg,"소문자, 숫자조합의 4-20자를 입력해주세요");
        return false;
    }else
        hideMsg(oMsg);
    
    idFlag = false;
    $.ajax({
        type:"GET",
        url: "/client/request?mode=checkId&input=" + id ,
        error : function(){
            idFlag = false;
            checkInput();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                idFlag = true;
            } else {
                showErrorMsg(oMsg, "사용중인 아이디 입니다.");
                idFlag = false;
            }
            checkInput();
        }
    });
    return true;
}

function checkEmail() {
    if(emailFlag) 
        return false;
    else if(timer != undefined){
        clearInterval(timer);
        timer = undefined;
    }
    var email = $("#inputEmail").val();
    var oMsg = $("#err_message_email");

    emailFlag = false;
    timer = 1;
    document.getElementById('loading_img').classList.remove('d-none');
    $.ajax({
        type:"GET",
        async: true,
        url: "/client/request?mode=checkEmail&input=" + email,
        error : function(){
            emailFlag = false;
            btnConfirm_Enable();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                emailFlag = true;
                if(timer==1){
                    $("#inputEmail").attr('readonly', 'readonly');
                    $("#btnConfirm").text('회원가입');
                    $('#modalEmail').modal('toggle')
                    document.getElementById('sectionLabel').classList.remove('d-none');
                    document.getElementById('loading_img').classList.add('d-none');
                    endTime = new Date((Date.now()) + 5*60000);
                    
                    timer = setInterval(function(){
                        $("#btnEmailCode").text("코드전송")
                        var timeRemain = Math.floor((endTime - Date.now())/1000);
                         $("#spanTime").text(
                             Math.floor(timeRemain/60) +':' + (timeRemain%60 < 10 ? '0'+timeRemain%60 : timeRemain%60)
                         );
                         
                         if(timeRemain < 0){
                             clearInterval(timer);                             
                             $("#inputEmail").attr('readonly', false);
                             document.getElementById('sectionLabel').classList.add('d-none');
                             $("#btnConfirm").text('인증하기');
                             timer = undefined;
                         }
                     },1000);
                }
            } else {
                emailFlag = false;
                document.getElementById('loading_img').classList.add('d-none');
                $("#inputEmail").removeAttr('readonly');
                if(timer==1)
                    timer = undefined;
                if(data.length > 4)
                    showErrorMsg(oMsg, "이미 사용중인 이메일 입니다.");
                else
                    showErrorMsg(oMsg, "인증코드 전송에 실패했습니다.");
            }
            btnConfirm_Enable();
        }
    });
    return true;
}

function verifyEmail() {
    var code = $("#inputVerifyCode").val();
    var oMsg = $("#err_message_code");

    if(!emailFlag) {
        showErrorMsg(oMsg, "이메일 인증 번호를 전송해주십시오.");
        return false;
    }

    verifyFlag = false;
    $.ajax({
        type:"GET",
        async: true,
        url: "/client/request?mode=verifyEmail&input=" + code,
        error : function(){
            verifyFlag = false;
            btnConfirm_Enable();
        },
        success : function(data) {
            var result = data.substr(0,4);
            if (result == "succ") {
                hideMsg(oMsg);
                verifyFlag = true;
            } else {
                showErrorMsg(oMsg, "이메일 인증에 실패했습니다.");
                verifyFlag = false;
            }
            btnConfirm_Enable();
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

$('#modalEmail').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) 
    var modal = $(this)
    modal.find('#spanEmail').text($("#inputEmail").val());
})
