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
        profsrName: "",				//교수명
        profsrUserSeq: "USER_00000062",	//교수이용자일련 -> 회원검색 팝업 생기면 이 부분 수정하기 
        
        noticeWeekArray: [],			//요일
        noticePlace: "",				//장소   
  },
    preview:"",
};
 
let vm;
var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: {  }
	})
	vm = app.mount('#content');
} 

let  event = {
	init: () => {
  
        $(document).on("click", "#btnDeleteNotice", function (e) {
            let urlParams = new URL(location.href).searchParams;
            let noticeSeq = urlParams.get('noticeSeq');
            let paramMap = {'noticeSeq':noticeSeq};
            $.confirm("정말 삭제하시겠습니까? 자료가 모두 사라집니다.", () => {
                $.sendAjax({
                    url: "/notice/deleteNoticeOne.api",
                    data: paramMap,
                    contentType: "application/json",
                    success: (res) => {
                		let resData = res.data;  
                        $.alert("입력한 내용이 모두 삭제되었습니다.", () => {
                            location.href = "noticeList.html";
                        });
                    },
                    error: function (e) {
                        $.alert(e.responseJSON.message);
                    },
                });
            });
        });
          
        $(document).on("click", "#btnUpdateNotice", function (e) {
            let urlParams = new URL(location.href).searchParams;
            let noticeSeq = urlParams.get('noticeSeq');
			location.href="noticeUpdt.html?noticeSeq=" + noticeSeq; 
        });
	}
	 ,  getNoticeDetail : () =>{
        let urlParams = new URL(location.href).searchParams;
        let noticeSeq = urlParams.get('noticeSeq');
        let paramMap = {'noticeSeq': noticeSeq};
        $.sendAjax({
            url: "/notice/selectNoticeDetail.api",
            data: paramMap,
            contentType: "application/json",
            success: async (res) => {
                let resData = res.data; 
            
                await event.setNoticeDetail(resData);
                
            }
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            },
		});
	}
	  
   , setNoticeDetail: (resData) => { 
        let tempNoticeeWeekArray = resData.noticeWeekArray.split(',')
        for(let i=0; i<tempNoticeeWeekArray.length; i++) {
            let dayValue = tempNoticeeWeekArray[i];
	   			console.log(dayValue)    
            if(dayValue == $(".noticeDayArray").prop("checked").value){
				$(".noticeDayArray")[i].attr("checked",true);
			} 
        } 
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
 
    } 
 	,setNoticeFx:(resData) => { 
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
    event.init(); 
    
});




