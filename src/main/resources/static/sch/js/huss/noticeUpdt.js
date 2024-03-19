
 var vueData = { 
	noticeOperateList : [],
	step: "master",
	totalCount:'', 
	profsrUserList: {},
	noticeWeekArray : [],
	inputText: '',
	
	searchData: {
		searchText: '', 	},
	getNoticeTime: '',
	userSeq: '',
	profsrUserNum: [],
	noticeFxList: [],
	noticeList: { //fx
		noticeDt: [],   // 샘플'2023-09-11' 
	 	noticeBeginTime: [],
	 	noticeEndTime: [],   
	},
	
	noticeData :{
		noticeWeekArray : [], 
		noticeDtArray: [],		 
		noticeSeqArray: [],
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
		methods: {   
            hourValidation: function(e) {
                e.target.value = util.formmater.hourFormatter(e.target.value);
            }, minuteValidation: function(e) {
                e.target.value = util.formmater.minuteFormatter(e.target.value);
            }, fnselectNoticeSe: ()=>{
				$(document).on("click", "#noticeSe", function (e) { 
				if(vm.noticeData.noticeSe == 'P'){
					vm.noticeData.noticeCo = 1
					  $("#noticeCo").val("1").prop("selected", true);	 
						 $("#noticeCo").attr("disabled", true); 
				 }else if(vm.noticeData.noticeSe == 'G') {
					 $("#noticeCo").attr("disabled", false);     
					} 
				}) 
			},
            fnSave : event.fnSave
			,fnGetdate: () => {
						let diffDate = '';
						let noticeSdate = new Date(vm.noticeData.noticeBeginDe);
						let noticeEdate = new Date(vm.noticeData.noticeEndDe);
						let regex = RegExp(/^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/);
						let noticeSeqArray = [];
						let noticeDtArray = []; 
						let sortday = [];
						let sortDate = [];  
				  		vm.noticeData.noticeDtArray = [];
						// 요일변환 numToDay(obj)
						for (var i = 0; i < vm.noticeData.noticeWeekArray.length; i++) {
							
							let	daynum = vm.noticeData.noticeWeekArray[i].split(',');// ['3'] 
 
							if (vm.noticeData.noticeWeekArray[i] !== "") {
								
		    				if(!(noticeSdate instanceof Date && noticeEdate instanceof Date)) return "Not Date Object";
							if(vm.noticeData.noticeSeqArray.includes(noticeSdate.getDay().toString())){
								vm.noticeData.noticeDtArray.push(vm.noticeData.noticeSdate);
							}
							while(noticeSdate <= new Date(noticeEdate)) {  
								noticeDtArray.push(noticeSdate.toISOString().split("T")[0]);
								noticeSeqArray.push(noticeSdate.getDay());  // object result getDay 나타나는 숫자  
								noticeSdate.setDate(noticeSdate.getDate() + 1); 
								}     
			  for (var t= 0; t < noticeDtArray.length; t++) {
					 if(noticeSeqArray[t] == daynum){ 
						sortDate.push(noticeDtArray[t]) 
					}  
					sortDate =vm.noticeList.noticeDt //Object 
					 vm.noticeData.noticeDtArray.push(sortDate);
				}
			  }				
			} 
	
				var noticeList = [] 
 		for (var t = 0; t < vm.noticeList.noticeDt.length; t++) { 			 
 			for (var d = 0; d < vm.noticeList.noticeBeginTime.length; d++) {  
				
				a={
					noticeSn :noticeList.length+1,
					noticeDt : util.formmater.removeDash(vm.noticeList.noticeDt[t].toString()) ,
					noticeBeginTime : vm.noticeList.noticeBeginTime[d],
					noticeEndTime :  vm.noticeList.noticeEndTime[d] 
					}	
				noticeList.push(a)
				vm.noticeData.noticeList = noticeList 
			  	}		
			  }	 
			  vm.fnSave() 
		}
         	, fnprofsrUserSeqModal: (uaerSeq) => {
				$('.profsrUserSeq').show();
				// 여기에 ajasx로 user_Seq 갖고오기 
			 $.sendAjax({
					url: "/notice/selectProfsrUserSeq.api", 
					data: vm.searchData,
					contentType: "application/json",
					success: (res) => {
						vm.totalCount = res.data.length;
						vm.profsrUserList = res.data;
						fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
						vm.searchData.pageNo = selectPage;
				})}
						
			, error: function(e) {
				$.alert(e.responseJSON.message);
			}
			}); 
				let profsrUserNum = $("input[name='profsrUserNum']");
				let profsrUserName = '';
				for (let i = 0; i < profsrUserNum.length; i++) {
				if (profsrUserNum[i].checked === true) {
						profsrUserName = vm.profsrUserList[i].userNm 
					}}
				} 
			, fnprofsrUserSeqInput: (event) => {
				let profsrUserNum = $("input[name='profsrUserNum']");

				let profsrUserName = '';
				let userSeqStr = [];


				for (let i = 0; i < profsrUserNum.length; i++) {
					if (profsrUserNum[i].checked === true) {
						profsrUserName = vm.profsrUserList[i].userNm
						userSeqStr = vm.profsrUserList[i].userSeq
					}
					 
					var updatedText = event.target.value;
						this.inputText = updatedText;
			 
						vm.profsrUserList.profsrUserSeq = updatedText;
						let profsrUserSeqtxtest = $("input[name='profsrUserSeq']");
						profsrUserSeqtxtest = updatedText;
				
					$('#modal').modal("hide"); //닫기  
					$(document).ready(function() {
						$("input#profsrUserSeqText").attr("placeholder", profsrUserName);
					}); 
				}  
			}
			, updateProfsrUserSeq: (event) => {
				var updatedText = event.target.value;
				this.inputText = updatedText;
				console.log(updatedText);
				vm.profsrUserList.profsrUserSeq = updatedText;
				let profsrUserSeqtxtest = $("input[name='profsrUserSeq']");
				profsrUserSeqtxtest = updatedText;

			}
			,fnCancel: () => {
				$.confirm("변경사항을 취소 하시겠습니까?", () => {
					vm.noticeList = {};
					vm.noticeData = {};
					$.alert("취소되었습니다.  목록으로 이동합니다.", () => {
							location.href = "noticeList.html";
							sessionStorage.clear();
					})
				})
			}   
		 }
	})
	vm = app.mount('#content');
} 

let  event = {
	init: () => {   
        /* 모집기간 datepicker는 레벨이 달라서 수동으로 minDate, maxDate설정함 */
        $(document).on("change", "#startPeriodDt", function (e) {
            $("#endPeriodDt").datepicker("option", "minDate", this.value);
        });
        $(document).on("change", "#endPeriodDt", function (e) {
            $("#endPeriodDt").datepicker("option", "maxDate", this.value);
        }); 
        $(document).on("click", "#deleteProfessor", function (e) {
            $("#professor").children().remove();
        });
       	
        $("input[name='noticeDayArray']").on('change', function (e) {
            if(vm.noticeData.startDt !== "" && vm.noticeData.endDt !== "" && vm.noticeData.noticeDayArray.length > 0){ 
                $(".noticeDayArray").removeAttr("disabled");
            } else { 
                $(".noticeDayArray").attr("disabled", "true");
            }
        }); 
	}
	,getNoticeOperateList : () =>{
        $.sendAjax({
            url: "/notice/selectNoticeOperateList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: async(res) => {  
                vm.totalCount = res.data.totalCount;
                vm.noticeOperateList = res.data.list;  
                console.log( vm.noticeOperateList)
                let len = vm.noticeOperateList.length;
                  for(i=0; i<len; i++){
					  // vm.noticeOperateList[i].noticeWeekArray = res.data.list[i].noticeWeekArray
                		let noticeDayArray =[]
                		 vm.noticeOperateList[i].noticeWeekArray 
				  } 
                let resDataFx = res.data;
				console.log(resDataFx)  
                    
                await event.setNoticeFx(resDataFx);
                  
				}
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            }
        });
    }  
	 ,fnSave: (data) => {  
        //필수 입력 validation
        if(!event.requireValidation()) {
            return false;
        } 
				vm.noticeData.noticeWeekArray = Object.values(vm.noticeData.noticeWeekArray).join() 
				vm.noticeData.noticeBeginDe = util.formmater.removeDash(vm.noticeData.noticeBeginDe)
				vm.noticeData.noticeEndDe = util.formmater.removeDash(vm.noticeData.noticeEndDe) 
		  		vm.noticeData.rcritBeginDt = vm.noticeData.rcritBeginDt+' ' + vm.noticeData.startPeriodHour + ':' +vm.noticeData.startPeriodMinute
			    vm.noticeData.rcritEndDt = vm.noticeData.rcritEndDt+' ' + vm.noticeData.endPeriodHour + ':' +vm.noticeData.endPeriodMinute
        $.sendAjax({
            url: "/notice/updateNoticeFx.api",
            data: vm.noticeData,
            contentType: "application/json",
            success: (res) => {
                $.alert("상담 개설 내용이 수정되었습니다. 상담 개설 목록으로 이동합니다.", () => {
                    location.href = "noticeList.html";
					sessionStorage.clear();
                });
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    }
	,requireValidation: () => {  
		console.log(`*noticeBeginTimeHour  : ${vm.noticeList.noticeBeginTimeHour}`+`*noticeEndTimeHour : ${vm.noticeList.noticeEndTimeHour}`+`* : ${vm.noticeList.noticeBeginTimeHour == vm.noticeList.noticeEndTimeHour}`)
		console.log(`* : ${vm.noticeList.noticeBeginTimeMinute == vm.noticeList.noticeEndTimeMinute}`)
		if(vm.noticeData.profsrUserSeq === undefined) {
			$.alert("교수명을  선택하세요.");
			return false;
		}else if(vm.noticeData.noticeBeginDe < vm.noticeData.rcritEndDt) {
			$.alert("모집기간 종료일자를 상담시작일자보다 전 날짜로 잡으세요.");
			return false;
		}else if(vm.noticeData.noticeEndDe < vm.noticeData.noticeBeginDe) {
			$.alert("상담종료일자를 상담시작일자 이후로 정해주세요.");
			return false;
		}else if(vm.noticeData.rcritEndDt < vm.noticeData.rcritBeginDt) {
			$.alert("모집기간 종료일자를 모집기간 시작일자 이후로 정해주세요.");
			return false;
		}
		  else if(vm.noticeData.noticeSe  === undefined) {
			$.alert("상담 형태를 선택해주세요.");
			return false;
		} else if(vm.noticeData.noticePlaceNm === undefined) {
			$.alert("상담 장소를 입력해주세요.");
			return false;
		}else if(vm.noticeData.noticeCo  === undefined) {
			$.alert("상담생 수를 선택해주세요.");
			return false;
		} else if(vm.noticeData.noticeBeginDe  === undefined) {
			$.alert("상담 기간 시작일자를 입력해주세요.");
			return false; 
		} else if(vm.noticeData.noticeEndDe === undefined) {
			$.alert("상담 기간 종료일자를 입력해주세요.");
			return false;
		}else if(vm.noticeData.noticeWeekArray.length ===  0) {
			$.alert("상담 요일을 선택하세요.");
			return false;
		} else if(vm.noticeList.noticeDt.length < 0) {
			$.alert("상담 요일이나 기간을 선택하세요.");
			return false;
		}  else if(vm.noticeList.noticeBeginTimeHour === undefined) {
			$.alert("상담  시작 시간을 입력해주세요.");
			return false;  
		} else if(vm.noticeList.noticeBeginTimeMinute  === undefined) {
			$.alert("상담 시작 분을 입력해주세요.");
			return false;
		}else if(vm.noticeList.noticeEndTimeHour === undefined) {
			$.alert("상담 종료 시간을 입력해주세요.");
			return false;
		} else if(vm.noticeList.noticeEndTimeMinute === undefined) {
			$.alert("상담 종료 분을 입력해주세요.");
			return false;
		} else if(vm.noticeList.noticeBeginTime.length === 0) {
			$.alert("상담 시간을 추가해주세요");
			return false;  
		}  else if(vm.noticeList.noticeEndTime.length === 0) {
			$.alert("상담 시작을 추가해주세요");
			return false;  
		}  else if(vm.noticeData.rcritBeginDt === undefined) {
			$.alert("모집기간 시작일자를 입력해주세요.");
			return false; 
		} else if(vm.noticeData.rcritEndDt === undefined) {
			$.alert("모집기간 종료일자를 입력해주세요.");
			return false;
		}
		return true;
	} 
    ,getDayIndex: (startDtDay) => {
        return util.date.getDayIndex(startDtDay);
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
					//	alert('성공')
                let resData = res.data;
				console.log(resData) 
                await event.setNoticeDetail(resData);
                
            }
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            },
		});
	}
     , setNoticeDetail: (resData) => {
		console.log(resData) 
      let tempNoticeeWeekArray = resData.noticeWeekArray
		console.log(tempNoticeeWeekArray)  
           
	   			vm.noticeData.userNm = resData.userNm
	   			vm.noticeData.noticeSe = resData.noticeSe
	   			vm.noticeData.noticeSeStr = resData.noticeSeStr
	   			vm.noticeData.noticePlaceNm = resData.noticePlaceNm
	   			vm.noticeData.noticeCo = resData.noticeCo
	   			vm.noticeData.noticePlaceNm = resData.noticePlaceNm
	   			//vm.noticeData.rcritBeginDt = resData.rcritBeginDt
	   			//vm.noticeData.rcritEndDt = resData.rcritEndDt
	   			vm.noticeData.noticeBeginDate = resData.noticeBeginDate
	   			vm.noticeData.noticeEndDate = resData.noticeEndDate
	   			vm.noticeData.noticeBeginTimeHour = resData.noticeBeginTimeHour
	   			vm.noticeData.noticeBeginTimeMinutes = resData.noticeBeginTimeMinutes
	   			vm.noticeData.noticeEndTimeHour = resData.noticeEndTimeHour
	   			vm.noticeData.noticeEndTimeMinutes = resData.noticeEndTimeMinutes
		vm.noticeData.rcritstartPeriodDt = resData.rcritBeginDt.substring(0,10);			//모집기간 시작일자
		vm.noticeData.rcritendPeriodDt = resData.rcritEndDt.substring(0,10);				//모집기간 종료일자	
      		 	vm.noticeData.rcritBeginTimeHour = resData.rcritBeginDt.substring(11,13);		//모집기간 시작시간
				vm.noticeData.rcritBeginTimeMinute = resData.rcritBeginDt.substring(14,16);		//모집기간 시작분
				vm.noticeData.rcritEndTimeHour = resData.rcritEndDt.substring(11,13);			//모집기간 종료시간
				vm.noticeData.rcritEndTimeMinute = resData.rcritEndDt.substring(14,16);		//모집기간 종료분  
 				vm.noticeData.noticeWeekArray = resData.noticeWeekArray.split(',') 
      			// noticeData.push(resData)  
 
    } 
 	,setNoticeFx:(resDataFx) => {
		console.log(resDataFx) 
        let today = new Date();
	 }
  
};


var datepicker = function() {
	$.datepicker.setDefaults({
		dateFormat: "yy-mm-dd",
		yearSuffix: "년",
		monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
		monthNamesShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
		dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"],
		showOtherMonths: true,
		showMonthAfterYear: true,
		changeMonth: true,
		changeYear: true,
		nextText: "다음 달",
		prevText: "이전 달",
		viewMode: "months",
		minViewMode: "months",
	});
	$(".datepicker")
		.datepicker()
		.on("change", function() {
			this.dispatchEvent(new Event("input"));
			if ($(this).hasClass("datepicker_from")) {
				$(this).closest(".bx_calendar").find(".datepicker_to").datepicker("option", "minDate", this.value);
			} else if ($(this).hasClass("datepicker_to")) {
				$(this).closest(".bx_calendar").find(".datepicker_from").datepicker("option", "maxDate", this.value);
			}
		});
};
 



$(document).ready(() => {
	vueInit(); 
    datepicker(); 
    $.datepicker.setDefaults({
        changeMonth: false,
        changeYear: false,
    }); 
    event.getNoticeDetail();
	event.getNoticeOperateList();
    event.init(); 
    
});


