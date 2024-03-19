var vueData = { 
	noticeDetail :{
        startHour : "",				// 시작 시간
        startMinute : "",			// 시작 분
        endHour : "",				// 종료 시간
        endMinute : "",				// 종료 분
        startDt: "",				// 기간 시작일자
        endDt: "",					// 기간 종료일자
        startPeriodDt: "",			//모집기간 시작일자
        endPeriodDt: "",			//모집기간 종료일자
        startPeriodHour: "",		//모집기간 시작시간
        startPeriodMinute: "",		//모집기간 시작분
        endPeriodHour: "",			//모집기간 종료시간
        endPeriodMinute: "",		//모집기간 종료분 
        userNm: "",				//교수명
        UserSeq: "USER_00000062",	//교수이용자일련 -> 회원검색 팝업 생기면 이 부분 수정하기 
        
        noticeWeekArray: [],			//요일
        noticePlace: "",				//장소   
  },
    preview:"",
    noticeOperateList: [],
};
 
let vm;
var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: {
			/**
			 * 
			,FnstartNoticeButton: (noticeSeq, noticeSn) => {
                event.startNoticeButton(noticeSeq, noticeSn);
      			}
			 */
			
		  }
	})
	vm = app.mount('#content');
} 

let  event = {
	
    init: () => {
        $(document).on("click", "#btnList", function (e) {
            location.href = "noticeFx.html";
        });
        $(document).on("click", "#btnFileDownloadForRegister", function (e) {
            let noticeUserArray = vm.noticeData.noticeUserArray;
            let paramMap =
                {'noticeUserArray':
                    noticeUserArray};
            $.sendAjax({
                url: "/notice/getCurrentUserListExcel.api",
                data: paramMap,
                contentType: "application/json",
                xhrFields: {
                    'responseType': 'blob'
                },
                success: (res, status, xhr) => {
                    let filename = '현재 수업신청자 현황.xlsx';
                    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    let disposition = xhr.getResponseHeader('Content-Disposition');
                    let matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }

                    let link = document.createElement('a');
                    link.href = URL.createObjectURL(res);
                    link.download = decodeURI(filename);
                    link.click();
                },
                error: function (e) {
                    $.alert(e.responseJSON.message);
                },
            });
        });
        $(document).on("click", "#btnFileDownloadForParticipants", function (e) {
            let noticeParticipantUserArray = vm.noticeData.noticeParticipantUserArray;
            let paramMap =
                {
                    'noticeParticipantUserArray':
                    noticeParticipantUserArray
                };
            $.sendAjax({
                url: "/noticeController/getCurrentParticipantsListExcel.api",
                data: paramMap,
                contentType: "application/json",
                xhrFields: {
                    'responseType': 'blob'
                },
                success: (res, status, xhr) => {
                    let filename = '현재 수업참여자 현황.xlsx';
                    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    let disposition = xhr.getResponseHeader('Content-Disposition');
                    let matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }

                    let link = document.createElement('a');
                    link.href = URL.createObjectURL(res);
                    link.download = decodeURI(filename);
                    link.click();
                },
                error: function (e) {
                    $.alert(e.responseJSON.message);
                },
            });
        });
        $(document).on("click", "#btnDeleteNotice", function (e) {
            let urlParams = new URL(location.href).searchParams;
            let noticeSeq = urlParams.get('noticeSeq');
            let noticeSn = urlParams.get('noticeSn');
            let paramMap = {'noticeSeq':noticeSeq, 'noticeSn' : noticeSn};
            $.confirm("정말 삭제하시겠습니까? 자료가 모두 사라집니다.", () => {
                $.sendAjax({
                    url: "/notice/deleteNoticeOne.api",
                    data: paramMap,
                    contentType: "application/json",
                    success: (res) => {
                		let resData = res.data;
               			 console.log('deleteNoticeOne.api')
                		console.log(resData)
                        $.alert("입력한 내용이 모두 삭제되었습니다.", () => {
                            location.href = "noticeFx.html";
                        });
                    },
                    error: function (e) {
                        $.alert(e.responseJSON.message);
                    },
                });
            });
        }); 
        $(document).on("click", "#btnUpdate", function (e) {
            let urlParams = new URL(location.href).searchParams;
            let noticeSeq = urlParams.get('noticeSeq');
            location.href = "noticeUpdt.html?noticeSeq=" + noticeSeq;
        });
        $(document).on("click", "#btnCancelNoticeFx", function (e) {
            let urlParams = new URL(location.href).searchParams;
            let noticeSeq = urlParams.get('noticeSeq');
            let noticeSn = urlParams.get('noticeSn');
            location.href = "noticeFxCancel.html?noticeSeq=" + noticeSeq + "&noticeSn=" + noticeSn;
        });
        $(document).on("click", "#noticeParticipantsNum", function (e) {
            $("#noticeFxParticipantsContent").removeClass("visibil");
        });
    } 
	 
	/**
	 * 
   
    startNoticeFn: async (noticeSeq, noticeSn) => {
        let paramMap =
            {	
				"onAirAt":"Y",
                "noticeSeq": noticeSeq,
                "noticeSn": noticeSn
            };
        await $.sendAjax({
            url: "/notice/updateNoticeFx.api",
            data: paramMap,
            contentType: "application/json",
            success: async (res) => {
                await event.gitSiMeetSetting(noticeSeq, noticeSn);
                $("#meet").css({
                    "display": "block",
                    "position": "absolute",
                    "z-index": "1200",
                    "top": "0",
                    "left": "0",
                    "width": "100%",
                    "height": "100%"
                });
                $("#meet").show();
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    }
    ,gitSiMeetSetting: async(noticeSeq, noticeSn) => {
        await fetch("https://schnode.musicen.com/generate-jwt", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                context: {
                    user: {
                        id: vm.userSeq,
                        name: vm.userName,
                        email: vm.userEmail,
                        avatar: "",
                        affiliation: "owner"
                    },
                    group: ""
                },
                aud: "jitsimeetid",
                iss: "jitsimeetid",
                sub: "*",
                room: "*"
            }),
        }).then((res) => {
            res.json().then(res=>{
                vm.token = res.token;
                const TOKEN = vm.token;
                const domain = "jit-si.musicen.com";
                const roomName = "notice-" + noticeSeq + "-" + noticeSn;
                const now = new Date().toLocaleString("ko-KR", {timeZone: "Asia/Seoul"});
                const koreanDate = new Date(now);
                const timestamp = `${koreanDate.getFullYear()}${String(koreanDate.getMonth() + 1).padStart(2,"0")}${String(koreanDate.getDate()).padStart(2, "0")}${String(koreanDate.getHours()).padStart(
                    2,
                    "0"
                )}${String(koreanDate.getMinutes()).padStart(2, "0")}`;

                const encodedRoomName = encodeURIComponent(roomName);
                const fileName = `${encodedRoomName}_${timestamp}.webm`;
                const options = {
                    jwt: TOKEN,
                    roomName: roomName,
                    height: "100%",
                    width: "100%",
                    configOverwrite: {
                        defaultLanguage: "en",
                        startWithAudioMuted: true,
                        startWithVideoMuted: false,
                        disableProfile: false,
                        prejoinPageEnabled: false,
                        disableInviteFunctions: true
                        //  maxParticipants: 5
                    },
                    interfaceConfigOverwrite: {
                        PIP_ENABLED: true
                    },

                    parentNode: document.querySelector("#meet")
                };
                const api = new window.JitsiMeetExternalAPI(domain, options);

                api.addEventListener("readyToClose", (e) => {
                    let paramMap =
                        {
							"onAirAt":"N",
                            "noticeSeq": noticeSeq,
                            "noticeSn": noticeSn
                        };
                    $.sendAjax({
                        url: "/notice/updateNoticeFx.api",
                        data: paramMap,
                        contentType: "application/json",
                        success: (res) => {
                            $("#meet").hide();
                        },
                        error: function (e) {
                            $.alert(e.responseJSON.message);
                        },
                    });
                });
 
	 */
	 ,  getNoticeDetail : () =>{
        let urlParams = new URL(location.href).searchParams;
        let noticeSeq = urlParams.get('noticeSeq');
            let noticeSn = urlParams.get('noticeSn');
            let paramMap = {'noticeSeq':noticeSeq, 'noticeSn' : noticeSn};
        console.log('getNoticeDetail');
        console.log(paramMap);
        $.sendAjax({
            url: "/notice/selectNoticeDetail.api",
            data: paramMap,
            contentType: "application/json",
            success: async (res) => {
                let resData = res.data; 
                await event.setNoticeDetail(resData);
                console.log('selectNoticeDetail.api')
                console.log(resData)
                console.log(resData.noticeBeginTimeHour+resData.noticeBeginTimeMinutes)
                console.log(resData.noticeEndTimeHour+resData.noticeEndTimeMinutes)
                console.log(resData.noticeCo)
                console.log(resData.noticeBeginDate)
                
                console.log(resData.noticeEndDate)
                console.log(resData.noticeSeStr)
                console.log(resData.noticeWeekArray)
                console.log(resData.rcritBeginDt)
                console.log(resData.rcritEndDt)
                console.log(resData.userNm)
                
            }
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            },
		});
	}
	,getNoticeOperateList : () =>{
        let urlParams = new URL(location.href).searchParams;
        let noticeSeq = urlParams.get('noticeSeq');
        let paramMap = {'noticeSeq': noticeSeq};
        $.sendAjax({
            url: "/notice/selectNoticeOperateList.api",
            data: paramMap,
            contentType: "application/json",
            success: async (res) => { 
                let resOpData = res.data.list;
                vm.totalCount = res.data.totalCount;
                vm.noticeOperateList = res.data 
                console.log(typeof(vm.noticeOperateList.noticeDt))
                console.log(res.data)
                console.log(res.data.list)
                let len = vm.noticeOperateList.length;
                
                console.log(len)
                console.log('noticeOperateList')
                console.log(vm.noticeOperateList.noticeDt)     
                await event.setNoticeFx(resOpData);
				}
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            }
        });
    }  
	  
   , setNoticeDetail: (resData) => {
	   			// let noticeDetail = []
                console.log('resData=selectNoticeDetail.api')
	   			console.log(resData) 
	   			console.log(resData.userNm) 
	   			 console.log('요일값')   
	   			 console.log(resData.noticeWeekArray.split(','))	
	   			  
        let tempNoticeeWeekArray = resData.noticeWeekArray.split(',')
        for(let i=0; i<tempNoticeeWeekArray.length; i++) {
            let dayValue = tempNoticeeWeekArray[i];
	   			console.log(dayValue)    
            if(dayValue == $(".noticeDayArray").prop("checked").value){
				$(".noticeDayArray")[i].attr("checked",true);
			}  } 
	   			vm.noticeDetail.userNm = resData.userNm
	   			vm.noticeDetail.noticeSeStr = resData.noticeSeStr
	   			vm.noticeDetail.noticePlaceNm = resData.noticePlaceNm
	   			vm.noticeDetail.noticeCo = resData.noticeCo
	   			vm.noticeDetail.noticePlaceNm = resData.noticePlaceNm
	   			vm.noticeDetail.rcritBeginDt = resData.rcritBeginDt
	   			vm.noticeDetail.rcritEndDt = resData.rcritEndDt
	   			vm.noticeDetail.noticeBeginDate = resData.noticeBeginDate
	   			vm.noticeDetail.noticeEndDate = resData.noticeEndDate
	   			vm.noticeDetail.noticeBeginTimeHour = resData.noticeBeginTimeHour
	   			vm.noticeDetail.noticeBeginTimeMinutes = resData.noticeBeginTimeMinutes
	   			vm.noticeDetail.noticeEndTimeHour = resData.noticeEndTimeHour
	   			vm.noticeDetail.noticeEndTimeMinutes = resData.noticeEndTimeMinutes
	   			vm.noticeDetail.noticeWeekArray = resData.noticeWeekArray.split(',')
                vm.noticeDetail.rcritEnd = resData.rcritEndDt.substring(0,10)
                vm.noticeDetail.rcritStart = resData.rcritBeginDt.substring(0,10)
        		vm.noticeDetail.startPeriodHour = resData.rcritBeginDt.substring(11,13);		//모집기간 시작시간
       			 vm.noticeDetail.startPeriodMinute = resData.rcritBeginDt.substring(14,16);		//모집기간 시작분
        		vm.noticeDetail.endPeriodHour = resData.rcritEndDt.substring(11,13);		//모집기간 종료시간
       			 vm.noticeDetail.endPeriodMinute = resData.rcritEndDt.substring(14,16);		//모집기간 종료 분
      			// noticeDetail.push(resData)  
      			 
    } 
 	,setNoticeFx:(resOpData) => {
		 console.log("resData=selectnoticeFx.api")
		 console.log(resOpData)
		 vm.noticeOperateList.noticeDtNm = resOpData.noticeDtNm
	 }
    ,setNoticeUser: (resData) => {
        //참여자 상세 이름(기본정보) 노출
        for(let i=0; i<resData.length; i++) {
            if(resData[i].userAuthor === 'ST' || resData[i].userAuthor === 'TJ') {
             
                vm.noticeData.noticeUserArray.push(resData[i].userNm + "("+ resData[i].userInnb + ")");
            } else if (resData[i].userAuthor === 'FF') {
             
                vm.noticeData.noticeUserArray.push(resData[i].userNm + "("+ resData[i].userInnb + ")");
            } else if (resData[i].userAuthor === 'ETA' || resData[i].userAuthor === 'ETB') {
            
                vm.noticeData.noticeUserArray.push(resData[i].userNm + "("+ resData[i].psitnNm + ")");
            } else if(resData[i].userAuthor === 'ETC') {
                vm.noticeData.noticeUserArray.push(resData[i].userNm + "("+ resData[i].psitnNm + ")");
            }
        }
    }
    ,fnDel : (noticeSeq) => {
				$.confirm("정말 삭제하시겠습니까? 삭제한 자료는 다시 복구하지 못합니다.", () => { 
						$.sendAjax({
						url: "/notice/deletenoticeOne.api",
						data : vm.noticeData.noticeSeq,
						contentType: "application/json",
						success : () => {
							$.alert("삭제가 완료되었습니다.", () => {
								location.href="noticeList.html"
							});				
						}			
					})
				})
			}  
};

 



$(document).ready(() => {
	vueInit(); ;
    event.getNoticeDetail();
    event.getNoticeOperateList();
    event.init(); 
    
});




