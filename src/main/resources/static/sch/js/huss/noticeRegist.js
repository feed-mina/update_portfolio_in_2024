var vueData = {
	step: "master",
	totalCount:'', 
	profsrUserList: {},
	noticeWeekArray : [],
	inputText: '',
	noticeData: { 
			noticeWeekArray : [], 
			noticeDtArray: [],		//강좌일정
			noticeSeqArray: []
	}, // tb_notice 
	noticeList: { //fx
		noticeDt: [],   // 샘플'2023-09-11' 
	 	noticeBeginTime: [],
	 	noticeEndTime: [],   
	},
	searchData: {
		searchText: '', 	
		},
	getNoticeTime: '',
	userSeq: '',
	profsrUserNum: [],
	noticeFxList: [],
	noticeUserSeq: 0,
	noticeModalList : []
};

let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
let noticeListArray = [];
var vm;
var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: { 
			fnselectNoticeSe: ()=>{
				$(document).on("click", "#noticeSe", function (e) { 
				if(vm.noticeData.noticeSe == 'P'){
					vm.noticeData.noticeCo = 1
					  $("#noticeCo").val("1").prop("selected", true);	 
						 $("#noticeCo").attr("disabled", true); 
				 }else if(vm.noticeData.noticeSe == 'G') {
					 $("#noticeCo").attr("disabled", false);     
					} 
			 	}) 
			}
			,fnModalSave: function() {
				let noticeSeq = $("input:radio[name='noticeSeqNum']:checked").attr('id');
				let paramMap = {'noticeSeq': noticeSeq};
				$.alert("해당 항목이 복사되었습니다.", () => {
					$.sendAjax({
						url: "/notice/selectNotice.api",
						data: paramMap,
						contentType: "application/json",
						success: async (res) => {
							let resData = res.data;
							await event.setCopyNotice(resData);
							$.sendAjax({
								url: "/notice/selectNoticeFx.api",
								data: paramMap,
								contentType: "application/json",
								success: async (noticeFxRes) => {
									await event.setNoticeFx(noticeFxRes.data);
								console.log(noticeFxRes);  
								},
								error: function (e) {
									$.alert(e.responseJSON.message);
								},
							});
						},
						error: function (e) {
							$.alert(e.responseJSON.message);
						},
					}); 
				});
					$('#noticeCopyModal').modal('hide');
			}
			, fnCopyNotice: () => {
				 	event.detailModal();
			} 
			,fnSearch: function (userAuthor) {
				this.searchData.pageNo = 1;
				if(userAuthor !== ''){
					vm.searchData.authorOne = userAuthor;
				}
				event.detailModal();
			}
			,fnModalCancel: function() {
				$("input:radio[name='noticeSeqNum']").prop('checked', false);
			}
			, hourValidation: function() {
				vm.noticeList.startHour = util.formmater.hourFormatter(vm.noticeList.noticeBeginTimeHour);
				vm.noticeList.endHour = util.formmater.hourFormatter(vm.noticeList.noticeEndTimeHour);
			}
			,minuteValidation: function() {
				vm.noticeList.startMinute = util.formmater.minuteFormatter(vm.noticeList.noticeBeginTimeMinute);
				vm.noticeList.endMinute = util.formmater.minuteFormatter(vm.noticeList.noticeEndTimeHour);
			}
			, fnGetNoticeTime: () => {
				let noticeTimeSet_arr = [];
				let noticeTimeSetStorage = '';
				if (vm.noticeList.noticeSeq == null) {
					vm.noticeList.noticeSeq = [];
				}
				vm.noticeList.noticeBeginTime = vm.noticeList.noticeBeginTimeHour + vm.noticeList.noticeBeginTimeMinute  
				vm.noticeList.noticeEndTime = vm.noticeList.noticeEndTimeHour + vm.noticeList.noticeEndTimeMinute
				 
				 event.plusNoticeTime();
			} 
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
							 // 	result.push(noticeSdate.toISOString().split("T")[0]); // object
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
					console.log('교수님UserSeq: ' + userSeqStr)  // 라디오버튼 userSeq 받아짐 
			}
			, updateProfsrUserSeq: (event) => {
				var updatedText = event.target.value;
				this.inputText = updatedText; 
				vm.profsrUserList.profsrUserSeq = updatedText;
				let profsrUserSeqtxtest = $("input[name='profsrUserSeq']");
				profsrUserSeqtxtest = updatedText;

			}   
			, fnSave: (data) => {  
				if(!event.requireValidation()) {
					return false;
				} 
				vm.noticeData.noticeWeekArray = Object.values(vm.noticeData.noticeWeekArray).join() 
				vm.noticeData.noticeBeginDe = util.formmater.removeDash(vm.noticeData.noticeBeginDe)
				vm.noticeData.noticeEndDe = util.formmater.removeDash(vm.noticeData.noticeEndDe) 
		  		vm.noticeData.rcritBeginDt = vm.noticeData.rcritBeginDt+' ' + vm.noticeData.startPeriodHour + ':' +vm.noticeData.startPeriodMinute
			    vm.noticeData.rcritEndDt = vm.noticeData.rcritEndDt+' ' + vm.noticeData.endPeriodHour + ':' +vm.noticeData.endPeriodMinute
		  		$.sendAjax({
					url: "/notice/insertnotice.api", 
					data: vm.noticeData,
					contentType: "application/json",
					success: (res) => {
						$.alert("noticeList 등록이 완료되었습니다. noticeList 목록으로 이동합니다.", () => {
							location.href = "noticeList.html";
							sessionStorage.clear();
						});
					}
				})

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
		    $(document).on("click", "#deleteNoticeTimeHour", function (e) { 
				if (e.target.tagName === 'INPUT') {
					e.target.parentElement.remove();
				} else if (e.target.tagName === 'IMG') {
					e.target.parentElement.parentElement.remove();
				}  
				console.log('삭제시도');
				event.fndeleteNoticeTimeHour(); 
			})  
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
	,detailModal: () => {
		$.sendAjax({
		url: "/notice/selectNoticeList.api",
			data: vm.searchData,
			contentType: "application/json",
			success: (res) => {
				$('.CopyNotice').show();
				// alert('modal확인'); 
			 	vm.noticeModalList = res.data.list; // 개설 리스트 전체 
			 
				console.log(res.data.list)
				
					for(let i=0; i<vm.noticeModalList.length; i++){
						let modelNoticeArrayStr = vm.noticeModalList[i].noticeWeekArray;
						modelNoticeArrayStr = modelNoticeArrayStr.replace("1","월")
											.replace("2","화")
											.replace("3","수")
											.replace("4", "목")
											.replace("5", "금")
											.replace("6", "토")
											.replaceAll(",", "/");
							vm.noticeModalList[i].noticeWeekArray = modelNoticeArrayStr;
											
					}
				
				fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
					vm.searchData.pageNo = selectPage;
					event.detailModal();
				});
			}
			,error: function (e) {
				$.alert(e.responseJSON.message);
			}
		});
	}  
	,setCopyNotice: (resData) => {
		console.log(resData)
		vm.noticeModalList = []; 
		vm.noticeModalList.noticeSeqArray = []; 
		//$("#noticeTimeContainer").children().remove();
		
		/**
		 * 
		let noticeSeqNum = $("input[name='noticeSeqNum']"); // Html 태그
		for (let i = 0; i < noticeSeqNum.length; i++) {
			if(noticeSeqNum[i].checked === true){
				console.log(`noticeSeqNum[i].id: ${noticeSeqNum[i].id}`) // ex) CNSLT_00000244 
				vm.noticeModalList.noticeSeqArray.push(noticeSeqNum[i].id);
				console.log(`vm.noticeModalList.noticeSeqArray : ${vm.noticeModalList.noticeSeqArray}`)
				}
			
		}
		 * 
		 */
		vm.noticeModalList.noticeWeekArray = resData.noticeWeekArray;
		vm.noticeModalList.noticeBeginDate =resData.noticeBeginDate;			//상담 기간 시작일자
		vm.noticeModalList.noticeEndDate = resData.noticeEndDate;				//상담 기간 종료일자
		vm.noticeModalList.noticeSeStr = resData.noticeSeStr;							//상담 종류
 		vm.noticeModalList.useAt = resData.useAt		
 		vm.noticeModalList.userNm = resData.userNm		
 		vm.noticeModalList.noticeSeq = resData.noticeSeq		
		vm.noticeModalList.rcritstartPeriodDt = resData.rcritBeginDt.substring(0,10);			//모집기간 시작일자
		vm.noticeModalList.rcritendPeriodDt = resData.rcritEndDt.substring(0,10);				//모집기간 종료일자	
		vm.noticeModalList.rcritBeginTimeHour = resData.rcritBeginDt.substring(11,13);		//모집기간 시작시간
		vm.noticeModalList.rcritBeginTimeMinute = resData.rcritBeginDt.substring(14,16);		//모집기간 시작분
		vm.noticeModalList.rcritEndTimeHour = resData.rcritEndDt.substring(11,13);			//모집기간 종료시간
		vm.noticeModalList.rcritEndTimeMinute = resData.rcritEndDt.substring(14,16);		//모집기간 종료분  
		vm.noticeModalList.noticePlaceNm = resData.noticePlaceNm;								//상담장소 
		vm.noticeModalList.noticeCo = resData.noticeCo; 								//수강생 수 
		
		$('#noticeCo').on('change', function (e) { 
			$('select option[value=' + resData.noticeCo + ']').attr('selected', 'selected'); 
		});
	} 
	,setNoticeFx: (resData) => { 
		vm.noticeModalList.noticeTimeContainer =  [];		
		vm.noticeModalList.noticeDtArray = [];
		vm.noticeModalList.noticeTimeContainerSet =  [];		
		vm.noticeModalList.noticeDtArraySet = []; 	
		/**
				vm.noticeData.noticeWeekArray = Object.values(vm.noticeData.noticeWeekArray).join() 
				vm.noticeData.noticeBeginDe = util.formmater.removeDash(vm.noticeData.noticeBeginDe)
				vm.noticeData.noticeEndDe = util.formmater.removeDash(vm.noticeData.noticeEndDe) 
		  		vm.noticeData.rcritBeginDt = vm.noticeData.rcritBeginDt+' ' + vm.noticeData.startPeriodHour + ':' +vm.noticeData.startPeriodMinute
			    vm.noticeData.rcritEndDt = vm.noticeData.rcritEndDt+' ' + vm.noticeData.endPeriodHour + ':' +vm.noticeData.endPeriodMinute
		 */
		for(let i= 0; i<resData.length; i++){ 
		// console.log(resData[i])
		vm.noticeModalList.noticeBeginTime = resData[i].noticeBeginTime;			//상담 시작 시간 
		vm.noticeModalList.noticeEndTime = resData[i].noticeEndTime;					//상담 종료 시간  
		let noticeTimeSet = util.cnTime(resData[i].noticeBeginTime)+ '-' +util.cnTime(resData[i].noticeEndTime)  
		vm.noticeModalList.noticeDt = resData[i].noticeDt;			//상담 날짜 
		vm.noticeModalList.noticeTimeContainer.push(noticeTimeSet);
		vm.noticeModalList.noticeDtArray.push(util.date.addDateDash(resData[i].noticeDt));
		}
		   
 		let noticeTimeContainer = new Set(vm.noticeModalList.noticeTimeContainer)
		let noticeTimeContainerSet = [...noticeTimeContainer]; 
		let noticeDtArray = new Set(vm.noticeModalList.noticeDtArray)
		let noticeDtArraySet = [...noticeDtArray] 
		for(t=0; t<noticeTimeContainerSet.length;t++ ){
			let noticeTimeSet = noticeTimeContainerSet[t] 
		vm.noticeModalList.noticeTimeContainerSet.push(noticeTimeContainerSet)
		vm.noticeModalList.noticeDtArraySet.push(noticeDtArraySet) 
		let startTime = vm.noticeModalList.noticeTimeContainerSet[0][t].split("-")[0].split(' ').join('').replace(/:/g, "")
		let endTime = vm.noticeModalList.noticeTimeContainerSet[0][t].split("-")[1].split(' ').join('').replace(/:/g, "")
		let noticeTimeSetSession = [startTime,endTime]
		sessionStorage.setItem(noticeTimeSet, noticeTimeSetSession);
		noticeTimeGetStorage = window.sessionStorage.getItem(noticeTimeSet);   
		noticeTimeGetStorageKey =  sessionStorage.getItem(sessionStorage.key(t))
	 	let noticeTimeHourTag = "<div class='noticeTimeHour dp_center side-by-side'>" + noticeTimeSet + "<input id='deleteNoticeTimeHour'   v-model='noticeList.noticeTimeSet' type='button' @click='deleteNoticeTimeHour'><img src='/image/close.png' class='delete-noticeTimeHour-btn'/></div>";
			$("#noticeTimesetContainer").append(noticeTimeHourTag);
	//	deleteNoticeTimeHourButton = document.getElementById("deleteNoticeTimeHour");
	//	deleteNoticeTimeHourButton.setAttribute("onClick","fndeleteNoticeTimeHour()")
		console.log(`noticeTimeSetSession : ${noticeTimeSetSession}` + `\\ type : ${typeof(noticeTimeSet)}`)
 		console.log(`${vm.noticeModalList.noticeTimeContainerSet[0][t].split("-")}`+`${vm.noticeModalList.noticeTimeContainerSet[0][t].split("-")[0].split(' ').join('').replace(/:/g, "")}`+`${vm.noticeModalList.noticeTimeContainerSet[0][t].split("-")[1].split(' ').join('').replace(/:/g, "")}`) 
 		 
		}
	console.log(`noticeTimeContainerSet : ${noticeTimeContainerSet}`+`/ type : ${typeof(noticeTimeContainerSet)}`+`/noticeDtArraySet : ${noticeDtArraySet}`+`/ type : ${typeof(noticeDtArraySet)}`)
		console.log()
	} ,plusNoticeTime: (e) => {
    	let listIndex = 0; 
		let noticeTimeSet = util.cnTime(vm.noticeList.noticeBeginTime) +'-'+ util.cnTime(vm.noticeList.noticeEndTime);
		let noticeTimeSetSession = [vm.noticeList.noticeBeginTime, vm.noticeList.noticeEndTime]
		sessionStorage.setItem(noticeTimeSet, noticeTimeSetSession);
	 	let noticeTimeHourTag = "<div class='noticeTimeHour dp_center side-by-side'>" + noticeTimeSet + "<input id='deleteNoticeTimeHour'   v-model='noticeList.noticeTimeSet' type='button' @click='deleteNoticeTimeHour'><img src='/image/close.png' class='delete-noticeTimeHour-btn'/></div>";
		$("#noticeTimeContainer2").append(noticeTimeHourTag);  
		
		noticeTimeGetStorage = window.sessionStorage.getItem(noticeTimeSet);  
		let noticeGetTimeArr = []
		let noticeGetBeginTimeArr = []
		let noticeGetEndTimeArr = []
		for (var i = 0; i < sessionStorage.length; i++) {
			noticeGetTime = sessionStorage.getItem(sessionStorage.key(i))
			noticeGetTimeArr.push(noticeGetTime)
			noticeGetTimeArrEntries = noticeGetTimeArr.entries()

		}
		for (let [index, noticeGetTimeArrelement] of noticeGetTimeArr.entries()) {
			console.log(index, noticeGetTimeArrelement);
			
			let endTime = noticeGetTimeArrelement.split(',')[1]
			let startTime = noticeGetTimeArrelement.split(',')[0]
			noticeGetBeginTimeArr.push(startTime)
			noticeGetEndTimeArr.push(endTime)
		} 
 			vm.noticeList.noticeEndTime = noticeGetEndTimeArr 
 			vm.noticeList.noticeBeginTime = noticeGetBeginTimeArr 
 			console.log( `* noticeGetEndTimeArr: ${noticeGetEndTimeArr}`+ `*noticeGetBeginTimeArr : ${noticeGetBeginTimeArr}`+ `* : ${typeof(noticeGetBeginTimeArr)}` )
	} 
	,fndeleteNoticeTimeHour: ()=>{ 
		let cnt = 0
			let TimeNum = $("button[id='deleteNoticeTimeHour']") ;
			for (var i = 0; i < sessionStorage.length; i++) { 
			console.log(`${sessionStorage.getItem(sessionStorage.key(i))}`+ `${sessionStorage.key(i)}` + `${sessionStorage.length}`) 
			console.log(sessionStorage.getItem(sessionStorage.key(i))+`=====================================================${i}`) 
				cnt += 1 
			} 
		let noticeTimeSet = util.cnTime(vm.noticeModalList.noticeBeginTime) +'-'+ util.cnTime(vm.noticeModalList.noticeEndTime);
		console.log(noticeTimeSet)
		let keys = Object.keys(sessionStorage);
		for(let key of keys) {
		    console.log(`${key}: ${sessionStorage.getItem(key)}`);
				console.log(sessionStorage.getItem(key))
		} 
		let sessionObject = Object.fromEntries(Object.entries(sessionStorage).map(([key, value]) =>[key, value]))
		let sessionValue = Object.values(sessionObject)
		console.log(`${sessionObject}`+`${sessionValue}`) 
			console.log(sessionObject)  
			console.log(sessionValue)  
			// sessionStorage.removeItem(sessionStorage.key(i)) 
			console.log(`${TimeNum.length}`  )  
			// sessionStorage.removeItem(sessionStorage.key(cnt))  
			//noticeRemoveTime = sessionStorage.removeItem(sessionStorage.key(i))
				console.log(cnt+`-------------------------------------------`)
				
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
	util.tableSetting();
	event.init()
})



