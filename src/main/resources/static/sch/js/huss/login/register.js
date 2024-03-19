let index = {
  init: function () {
    console.log('시작')
    // /login//userRegist.api
		let data = {
      userEmail : "sample@email.com",
      userPassword : "glasowk!@",
      mbtlnum :"01012345678", // 핸드폰번호
      userNm : "tester", //유저 이름 
      qestnCode : "가장좋아하는색은?", // 질문 
      qestnRspns : "빨강색" //답변  
		}
		console.log("JSON http body데이터"+JSON.stringify(data))
    $("#btn-save").on("click", () => { // this를 바인딩하기 위해서 , 만약 function을 사용하면 this값은 window 객체값을 가리킨다.
      console.log('가입하기버튼클릭');
   $this.save();
    })
  },
  save: function () {
    alert('user의 save함수 호출됨');
    let data = {
      userEmail: $("#userEmail").val(),
      userPassword: $("#userPassword").val(),
      userPasswordCheck: $("#userPasswordCheck").val(),
      mbtlnum: $("#mbtlnum").val(), // 핸드폰번호
      userNm: $("#userNm").val(), //유저 이름 
      qestnCode: $("#qestnCode").val(), // 질문 
      qestnRspns: $("#qestnRspns").val() //답변      
    };

    $.ajax({
      type: "POST",
      url: "/login/userRegist.api",
      data: JSON.stringify(data), // http body  데이터
      contentType: "application/json; charset=utf-8", // body데이터가 어떤타입인지 (MIME)
      dataType: "json" // 요청을 서버로 해서 응답이 왔을때 기본적으로 모든것이 문자열 (생긴게json이라면 javascript오브젝트로 변경)
    }).done(function (resp) {
      alert("회원가입이 완료되었습니다.");
      location.href = "/sch/huss/login/registerEmail.html";
    }).fail(function (error) {
      alert(JSON.stringify(error));
    });

  }
}

index.init()