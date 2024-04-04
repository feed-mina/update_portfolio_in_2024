/*****************************************************************************
 *
 * common.js
 * 공통 스크립트
 *
 */
const gServerApiUrl = ""; 
// function setGnb(type, el) {
// 	$("#gnbCtgry > button").removeClass("active");
// 	$(el).addClass("active");
// 	$(`#accordionSidebar > li`).show();
// 	type != "gp-all" && $(`#accordionSidebar > li:not([data-group='${type}'])`).hide();
// }

$(document).ready(function() {

	// 헤더가 있는 페이지는 헤더, 사이드바 공통 세팅
	if ($("#header_wrap").length) {
		var path = window.location.pathname; 
		$.when(
			$.get("/include/header.html", function(data) {
				$("#header_wrap").html(data); 
			}),
		).done(function() {
			// sidebar, 헤더 부분 css Active 추가
			const locNm = location.pathname + location.search; // ex '/sch/admin/lctreSemina/lctreSemina.html?name=hi'
			let aTag = $("a[href='" + locNm + "']");
			aTag.closest(".nav-item").addClass("active"); // 사이드바 active추가
			$(".navbar").find("[data-category=" + sidebarCategory + "]").find("a").addClass("active")  // 헤더 active추가 url기준(sidebarCategory)으로 header.html 안의 data-category에 활성화될 카테고리 입력해놓음

		//	initUserAuth(); 
		});
	}
// 헤더가 있는 페이지는 헤더, 사이드바 공통 세팅
	if ($("#footer_wrap").length) {
		var path = window.location.pathname;
		var pathArray = path.split("/");
		var sidePath = "/include/sidebar.html";
		var sidebarCategory = pathArray[3]; // board, dashboard, notice, sysManage, userManage 등등
		$.when(
			$.get("/include/footer.html", function(data) {
				$("#footer_wrap").html(data); 
			}),
		).done(function() {
			// sidebar, 헤더 부분 css Active 추가
			const locNm = location.pathname + location.search; // ex '/sch/admin/lctreSemina/lctreSemina.html?name=hi'
			let aTag = $("a[href='" + locNm + "']");
			aTag.closest(".nav-item").addClass("active"); // 사이드바 active추가
			$(".navbar").find("[data-category=" + sidebarCategory + "]").find("a").addClass("active")  // 헤더 active추가 url기준(sidebarCategory)으로 header.html 안의 data-category에 활성화될 카테고리 입력해놓음

		//	initUserAuth(); 
		});
	}
	$("#contents").prepend(
		//공통 Toast
		`
		<div style="position: fixed; top: 20px; right: 20px; z-index: 9999">
			<div id="toastAlert" class="toast hide" role="alert" data-delay="3000" style="width:200px;">
			<div class="toast-header">
				<strong class="mr-auto"><span class="text-info">알림</span></strong>
				<button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
				<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="toast-body"></div>
			</div>
		</div>
    `
	);
	// 검색창에서 화살표이미지 on class 토글하기
	search_details();

	// alarmPageList 개설 중간에 좌측 네비게이션 메뉴 및 상단 메뉴 클릭시 alert 띄워준다
	let alarmPageList = ['seminaRegist', 'lctreRegist', 'seminaUpdt', 'lctreUpdt'];
	let url = document.location.href;
	let extensionIndex = url.lastIndexOf(".");
	let startIndex = url.lastIndexOf("/");
	let htmlFileName = url.substring(startIndex + 1, extensionIndex);
	let alertTargetPage = ["/lctreSemina", "/dashBoard", "/userManage", "/noticeExprn", "/alarm", "/board", "/sysManage"];
	if (alarmPageList.includes(htmlFileName)) {
		window.onbeforeunload = function(event) {
			let targetHref = event.target.activeElement.href;
			let targetLastIndex = targetHref.lastIndexOf("/");
			targetHref = targetHref.substr(0, targetLastIndex);
			let targetStartIndex = targetHref.lastIndexOf("/");
			targetHref = targetHref.substr(targetStartIndex, targetHref.length);
			if (alertTargetPage.includes(targetHref)) {
				event.returnValue = '"내용2"';
			}
		}
	}
});


// 캘린더
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

$(document).ajaxStart(function() {
	$("body").css("overflow", "hidden");
	$("#loadingSpinner").show();
});
$(document).ajaxStop(function() {
	$("body").css("overflow", "");
	$("#loadingSpinner").hide();
});
$(document).on("keyup", ".only-phone", function(e) {
	var val = util.formmater.phone($(this).val());
	$(this).val(val);
});

/***************
 * 페이징 공통
 ******************/
var fnPaging = (totalData, dataPerPage, pageCount, currentPage, callBack, loadingYn, elemId) => {
	elemId = !elemId ? "#pagination" : "#" + elemId;
	if (totalData < 1) {
		$(elemId).html("");
		return;
	}

	var totalPage = Math.ceil(totalData / dataPerPage); // 총 페이지 수
	var pageGroup = Math.ceil(currentPage / pageCount); // 페이지 그룹

	var last = pageGroup * pageCount; // 화면에 보여질 마지막 페이지 번호
	var first = last - (pageCount - 1) <= 0 ? 1 : last - (pageCount - 1); // 화면에 보여질 첫번째 페이지 번호
	if (last > totalPage) {
		last = totalPage;
	}

	var html = "";

	html += '<li style="cursor:pointer" id="first" class="page-item"><a class="page-link">&lt;&lt;</a></li>';
	html += '<li style="cursor:pointer" id="prev" class="page-item"><a class="page-link">&lt;</a></li>';

	for (var i = first; i <= last; i++) {
		html += `<li style="cursor:pointer" id="${i}" class="page-item"> <a class="page-link">${i}</a> </li>`;
	}

	html += '<li style="cursor:pointer" id="next" class="page-item"><a class="page-link">&gt;</a></li>';
	html += '<li style="cursor:pointer" id="last" class="page-item"><a class="page-link">&gt;&gt;</a></li>';

	$(elemId).html(html); // 페이지 목록 생성
	$(elemId + " li#" + currentPage).addClass("active"); // 현재 페이지 표시

	$(elemId + " li")
		.off("click")
		.on("click", function() {
			if (loadingYn != null && loadingYn == "Y") {
				$("#loadingDiv").addClass("dimm_load").show();
			}
			var $item = $(this);
			var $id = $item.attr("id");
			var selectedPage = +$item.text();

			if ($id == "first") selectedPage = 1;
			if ($id == "next") selectedPage = last + 1 > totalPage ? totalPage : last + 1;
			if ($id == "prev") selectedPage = first - pageCount <= 0 ? 1 : first - pageCount;
			if ($id == "last") selectedPage = totalPage;

			if (typeof callBack == "function") {
				callBack(selectedPage);
			}

			fnPaging(totalData, dataPerPage, pageCount, selectedPage, callBack, loadingYn, elemId.substring(1));
		});

	if (loadingYn != null && loadingYn == "Y") {
		setTimeout(() => {
			$("#loadingDiv").removeClass("dimm_load").hide();
		}, 20);
	}
};

/* jQuery 공통함수 */
/**
 * $.sendAjax
 */
jQuery.sendAjax = (option) => {
	if (option == null || typeof option != "object" || option.url == null) {
		alert("option type 오류!!!");
		return;
	}

	// 모든 string인자 trim 처리
	if (option.data) {
		Object.keys(option.data).forEach((key) => {
			if (typeof option.data[key] == "string") {
				option.data[key] = $.trim(option.data[key]);
			}
		});
	}

	//error 공통처리
	var paramError = option.error;

	if (option.contentType == "application/json") {
		option.data = JSON.stringify(option.data);
	}

	var lOption = {
		url: gServerApiUrl,
		method: "post",
		headers: { "X-AUTH-TOKEN": util.getToken() },
		global: true,
		success: (res) => { },
	};

	lOption = $.extend({}, lOption, option);
	lOption.url = gServerApiUrl + option.url;

	lOption.error = (error) => {
		// 토큰 secret은 맞으나 db에서 유저가없을때 나오는 에러로 임시로 message구분해서 막음
		if (error.status === 500) {
			if (error.responseJSON.message === 'username cannot be null') {
				util.logout(false);
			}
		}
		if (error.status === 401) {
			if (util.getStorage("autoLogin") == true) {
				$.ajax({
					url: "/login/refreshToken.api",
					method: "POST",
					contentType: "application/json",
					data: JSON.stringify({ accessToken: util.getToken(), userSeq: util.getUserSeq() }),
					success: (res) => {
						// alert("자동로그인 하겠습니다");
						util.setStorage("accessToken", res.data.newToken);
						location.reload();
					},
					error: (err) => {
						alert("자동로그인 실패");
						util.logout(false);
					},
				});
				return;
			} else {
				// alert("세션이 만료되었습니다.");
				util.logout(false);
				return;
			}
		}
		if (paramError != null && typeof paramError == "function") {
			paramError(error);
		} else {
			if (error.responseJSON != null && error.responseJSON.message != null) {
				alert(error.responseJSON.message);
				return;
			}
			alert("일시적인 에러입니다.<br/>잠시후 다시 이용해 주세요.");
			return;
		}
	};

	return $.ajax(lOption);
};

jQuery.confirm = (msg, successCb = null, failCb = null, option = {}, isAlert = false) => {
	!isAlert && $("#alertCenter .btn-secondary").show();

	const options = {
		title: "알림",
		...option,
	};
	$("#alertTitle").text(options.title);
	$("#alertContent").html(msg);

	$("#alertCenter")
		.off("click.alert")
		.on("click.alert", ".btn-primary", function() {
			$("#alertCenter.modal").modal("hide").data("bs.modal", null);
			typeof successCb == "function" && successCb();
		})
		.on("click.alert", ".btn-secondary, .close", function() {
			$("#alertCenter.modal").modal("hide").data("bs.modal", null);
			typeof failCb == "function" && failCb();
		});

	$("#alertCenter").modal();
};
jQuery.alert = (msg, successCb = null, failCb = null, option) => {
	$("#alertCenter .btn-secondary").hide();
	jQuery.confirm(msg, successCb, failCb, option, true);
};
jQuery.closeModal = (modalName = "alertCenter") => {
	$(`#${modalName}`).modal("hide");
};

jQuery.toast = (msg = "", type = "info", title = "알림") => {
	$("#toastAlert strong").html(`<span class="text-${type}">${title}</span>`);
	$("#toastAlert .toast-body").html(msg);
	$("#toastAlert").toast("show");
};

const loginmode = $("#login"); 
const logoutmode = $("#logout"); 
// 공통 유틸 함수
var util = {
	
	validator: {
		isHpFormat : (hp) =>{	
			if(hp == ""){	
				return true;
			}	
			var phoneRule = /^(01[016789]{1})[0-9]{3,4}[0-9]{4}$/;
				return phoneRule.test(hp);
		}
		,
		isTelNumber: (tel) => {
			var tt = tel.replace(/-/g, "");
			var regExp = /^01(?:0|1|[6-9])(?:\d{3}|\d{4})\d{4}$/;
			return regExp.test(tt); // 형식에 맞는 경우 true 리턴
		},
		isEmail: (email) => {
			var regExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
			return regExp.test(email); // 형식에 맞는 경우 true 리턴
		},
		isBsnmNo: (number) => {
			var numberMap = number
				.replace(/-/gi, "")
				.split("")
				.map(function(d) {
					return parseInt(d, 10);
				});
			if (numberMap.length == 10) {
				var keyArr = [1, 3, 7, 1, 3, 7, 1, 3, 5];
				var chk = 0;
				keyArr.forEach(function(d, i) {
					chk += d * numberMap[i];
				});
				chk += parseInt((keyArr[8] * numberMap[8]) / 10, 10);
				return Math.floor(numberMap[9]) === (10 - (chk % 10)) % 10;
			}
			return false;
		},

		isSafePW: (str) => {
			if (str.length < 9) return "비밀번호를 최소 9자 이상 입력해주세요";
			if (str.length > 20) return "비밀번호는 최대 20자까지 입력 가능합니다.";

			const special = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/g;
			const number = /[0-9]/g;
			const lowerCase = /[a-z]/g;
			const upperCase = /[A-Z]/g;

			const list = [special, number, lowerCase, upperCase];

			const count = list.map((item) => item.test(str)).filter((item) => item === true).length;

			if (count < 3) return "영대문자, 영소문자, 숫자, 특수문자(!, @...) 중 3종류 이상으로 구성해주세요";

			return true;
		},

		// isHour: (hour) => {
		//   const special = /[0-9]/g;
		// }
		isEmpty: (param) => {
			if (param.length === 0 || param === "" || param.replaceAll(" ", "") === "") {
				return true;
			} else {
				return false;
			}
		},

		isNotEmpty: (param) => {
			if (param !== "" && param !== undefined && param !== null) {
				return true;
			} else {
				return false;
			}
		},

		isSearchKeyword: (e, maxKeywordNum) => {
			if (util.validator.isEmpty(e.target.value)) {
				e.target.value = "";
				return false;
			}
			let keyword = $(".keyword");

			for (let i = 0; i < keyword.length; i++) {
				if (e.target.value === keyword[i].innerText) {
					if ($("#cantAdd").length === 0) {
						$("#message").append("<span id='cantAdd'>&nbsp;&nbsp;&nbsp;* 동일 키워드는 입력이 불가능합니다.</span>");
					}
					return false;
				}
			}

			$("#cantAdd").remove();

			if (keyword.length >= maxKeywordNum) {
				return false;
			}
			return true;
		},
	},

	date: {
		formatDateToYYYYMMDD(date) {
			const year = date.getFullYear().toString(); // 끝에서 두 자리를 잘라내는 부분을 제거
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');

			return year + month + day; // YYYYMMDD 형식으로 반환
		}
		,
		formatDateToYYMMDD(date) {
			const year = date.getFullYear().toString().slice(-2);
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');

			return year + month + day;
		},
		getTime: () => {
			var today = new Date();

			var hours = ("0" + today.getHours()).slice(-2);
			var minutes = ("0" + today.getMinutes()).slice(-2);
			var seconds = ("0" + today.getSeconds()).slice(-2);
			var timeString = hours + ":" + minutes + ":" + seconds;
			return timeString;
		},

		getToday: () => {
			var date = new Date();
			var year = date.getFullYear();
			var month = ("0" + (1 + date.getMonth())).slice(-2);
			var day = ("0" + date.getDate()).slice(-2);
			return year + month + day;
		},

		getYesterday: () => {
			let today = new Date();
			let yesterday = new Date(today.setDate(today.getDate() - 1));
			var year = yesterday.getFullYear();
			var month = ("0" + (1 + yesterday.getMonth())).slice(-2);
			var day = ("0" + yesterday.getDate()).slice(-2);
			return year + month + day;
		},

		getLastMonth: () => {
			var date = new Date();
			date.setMonth(date.getMonth() - 1);

			var year = date.getFullYear();
			var month = ("0" + (1 + date.getMonth())).slice(-2);
			var day = ("0" + date.getDate()).slice(-2);
			return year + month + day;
		},

		getTodayDateTime: () => {
			var date = new Date();
			var year = date.getFullYear();
			var month = ("0" + (1 + date.getMonth())).slice(-2);
			var day = ("0" + date.getDate()).slice(-2);
			return (
				year +
				month +
				day +
				("0" + date.getHours()).slice(-2) +
				("0" + date.getMinutes()).slice(-2) +
				("0" + date.getSeconds()).slice(-2)
			);
		},

		getSubDate: (subDay, subMonth, subYear) => {
			var today = new Date();
			today.setFullYear(today.getFullYear() - subYear);
			today.setMonth(today.getMonth() - subMonth);
			today.setDate(today.getDate() - subDay);

			var year = today.getFullYear();
			var month = today.getMonth() + 1;
			var day = today.getDate();

			month = month < 10 ? "0" + String(month) : month;
			day = day < 10 ? "0" + String(day) : day;

			return year + "-" + month + "-" + day;
		},

		addDateDash: (date) => {
			if (!date) return "-"
			var year = date.substring(0, 4);
			var month = date.substring(4, 6);
			var day = date.substring(6, 8);

			return year + "-" + month + "-" + day;
		},

		// 1200 --> 12:00 으로 변환
		addDateColon: (date) => {
			return date.slice(0, 2) + ":" + date.slice(2, 4);
		},
		getDayOfWeek: function(dateStr) { //ex) getDayOfWeek('2022-06-13')
			const week = ['일', '월', '화', '수', '목', '금', '토'];
			const dayOfWeek = week[new Date(dateStr).getDay()];
			return dayOfWeek;
		},

		/**
		* 현재 날짜에 해당 날짜만큼 더한 날짜를 반환한다.
		* @param {String} date			날짜 문자열(ex: 2018-01-01)
		* @param {Number} pnDayTerm		추가 일수
		* @return {String} 날짜 문자열
		*/
		addDate: function(psDate, pnDayTerm) {
			var pnYear = psDate.substring(0, 4);
			var pnMonth = psDate.substring(5, 7);
			var pnDay = psDate.substring(8, 10);

			if (new Date(psDate)) {
				var vdDate = new Date(pnYear, pnMonth - 1, pnDay);
				var vnOneDay = 1 * 24 * 60 * 60 * 1000; /* 1day,24hour,60minute,60seconds,1000ms */

				var psTime = vdDate.getTime() + (Number(pnDayTerm) * Number(vnOneDay));
				vdDate.setTime(psTime);

				// return this.format(vdDate,"YYYY-MM-DD");
				let addedDate = new Date(vdDate);

				let year = addedDate.getFullYear();
				let month = addedDate.getMonth() + 1;
				let date = addedDate.getDate();

				let monthStr = "";
				let dateStr = "";

				if (month.toString().length === 1) {
					monthStr = ''.concat("0", month.toString());
				} else {
					monthStr = month;
				}

				if (date.toString().length === 1) {
					dateStr = ''.concat("0", date.toString());
				} else {
					dateStr = date;
				}

				return year + "-" + monthStr + "-" + dateStr;
			} else {
				return psDate;
			}
		},

		/**
		* 두 날짜간의 일(Day)수를 반환한다.
		* @param {String} psDate1st	년월 문자열(ex: 2018-02-01)
		* @param {String} psDate2nd    년월 문자열(ex: 2017-02-01)
		* @return {Number} 일수(Day)
		*/
		getDiffDay: function(psDate1st, psDate2nd) {
			var startDt = new Date(psDate1st);
			var endDt = new Date(psDate2nd);

			return parseInt((endDt - startDt) / (1000 * 60 * 60 * 24));
		},

		getDayIndex: function(addedDateDay) {
			let addedDateDayIndex = 0;
			switch (addedDateDay) {
				case "일": addedDateDayIndex = 0; break;
				case "월": addedDateDayIndex = 1; break;
				case "화": addedDateDayIndex = 2; break;
				case "수": addedDateDayIndex = 3; break;
				case "목": addedDateDayIndex = 4; break;
				case "금": addedDateDayIndex = 5; break;
				case "토": addedDateDayIndex = 6; break;
			}
			return addedDateDayIndex;
		},

		getDateAfterOneMonthStr: function() {
			var now = new Date();
			var oneMonthLater = new Date(now.setMonth(now.getMonth() + 2));
			var yearStr = oneMonthLater.getFullYear().toString();
			var monthStr = oneMonthLater.getMonth().toString();
			if (monthStr.length === 1) {
				monthStr = "0" + monthStr;
			}
			var dateStr = oneMonthLater.getDate().toString();
			if (dateStr.length === 1) {
				dateStr = "0" + dateStr;
			}
			var dateStrFull = yearStr + monthStr + dateStr;
			var oneMonthLaterStr = this.addDateDash(dateStrFull);
			return oneMonthLaterStr;
		},

		getCurrentDateStr: function() {
			var now = new Date();
			var yearStr = now.getFullYear().toString();
			var monthStr = (now.getMonth() + 1).toString();
			if (monthStr.length === 1) {
				monthStr = "0" + monthStr;
			}
			var dateStr = now.getDate().toString();
			if (dateStr.length === 1) {
				dateStr = "0" + dateStr;
			}
			var dateStrFull = yearStr + monthStr + dateStr;
			var currentDateStr = this.addDateDash(dateStrFull);
			return currentDateStr;
		},
		datepickerValueValidation: function(val) {
			var regex = /^[0-9]*$/; // 숫자만 체크
			if (regex.test(val)) {
				var isDateStr = new Date(util.date.addDateDash(val));
				if (isDateStr.toString() === 'Invalid Date') {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		},
		getDateList: (sDate, eDate, weekList) => {
			let list = []
			let days = util.date.getDiffDay(sDate, eDate)
			for (var i = 0; i <= days; i++) {
				var j = util.date.addDate(sDate, i)
				if (weekList.includes((new Date(j).getDay()).toString())) {
					list.push(util.formmater.removeDash(j))
				}
			}
			return list
		}
	},

	formmater: {
		/** 콤마추가 */
		addComma: (str) => {
			if (str == null) {
				return "";
			}
			return str.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		},
		/** 콤마제거 */
		removeComma: (str) => {
			return str.replace(/,/g, "");
		},
		/** 폰번호 "01012345678" -> "010-2644-4093" */
		phone: (phone) => {
			return phone
				.replace(/[^0-9]/g, "")
				.replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/, "$1-$2-$3")
				.replace("--", "-");
		},
		/** 문자열에서 대쉬("-")제거  */
		removeDash: (str) => {
			if (!str) return "";
			const regx = /-/g;
			return str.replace(regx, "");
		},
		textLengthOverCut: (txt, len, lastTxt) => {
			txt = (txt !== null && txt !== undefined) ? txt : "";

			if (len === "" || len === null) { // 기본값
				len = 20;
			}
			if (lastTxt === "" || lastTxt === null) { // 기본값
				lastTxt = "...";
			}
			if (txt.length > len) {
				txt = txt.substr(0, len) + lastTxt;
			}
			return txt;
		},
		hourFormatter: (hour) => {
			const regex = /^[0-9]+$/;
			if (!regex.test(hour)) {
				return "";
			}
			return hour;
		},
		minuteFormatter: (minute) => {
			const regex = /^[0-9]+$/;
			if (!regex.test(minute)) {
				return "";
			}
			return minute;
		}
	},
	REST_Call: (path)=> {
		axios
			.get("http://52.78.212.203:8189" + path, {
				params: {},
				withCredentials: true,
			})
			.then(({ data }) => {
				console.log(data);
				$("#contents").html(JSON.stringify(data));
			})
			.catch((err) => {
				console.log(err);
				$("#contents").html(JSON.stringify(err));
			});
	},
	getKakaoLogin: () => { 

let KAKAO_GET = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=bc6f76bb8856c35bd57a3fa6a4331069&redirect_uri=http://ec2-52-78-212-203.ap-northeast-2.compute.amazonaws.com:8189/auth/register.api";

var MAIN_PAGE = "/sch/huss/dashBoard/main.html";
console.log('카카오 로그인');
 window.location.assign(KAKAO_GET);
/**
	$.ajax({
			type: "get",
			url: KAKAO_GET,
			datatype: "json",
			contentType: "application/json",
			success: (res) => {
				console.log(res)
//window.sessionStorage.setItem("k","k");
				 window.location.assign(MAIN_PAGE);
			},
			error: function (e) {
				console.log(e.responseJSON.message);
			},
		}) 
		 */
		 console.log('메인페이지로 이동 시도');
		  //window.location.assign(MAIN_PAGE);
	},
	login: () => {
		// 로그인 이후 이동할 페이지
		var MAIN_PAGE = "/sch/huss/dashBoard/main.html";
	//var MAIN_PAGE = "http://http://52.78.212.203/:8189/sch/huss/dashBoard/main.html";
		// 로칼 저장값을 함수에서 로그인버튼을 누르지 않아도 로컬스토리지에 저장이 되는 부분이 있다.
		// 그 이유가 왜 그런지 알아보기
		var autoLogin = $("#autoLogin").prop("checked");
		var keepEmail = $("#keepEmail").prop("checked");
		if (!$("#adminLoginId").val()) {
			alert("이메일을 입력해주세요");
			// $("#loginFail").modal("show");
			//$("#modalText").text("아이디를 입력해주세요");
			return
		}
		if (!$("#currentpassword").val()) {
			alert("비밀번호를 입력해주세요");
			//$("#loginFail").modal("show");
			//$("#modalText").text("비밀번호를 입력해주세요");
			return
		}
		util.validator.isSafePW($("#currentpassword").val());
		if (!util.validator.isEmail($("#adminLoginId").val())) {
			alert("이메일 형식에 맞지 않습니다");
			return;
		}
		$.ajax({
			type: "post",
			url: "/login/login.api",
			// headers: {}, // 공통에서 헤더에 토큰을 넣기때문에 여기서는 빈값으로 넣어서 덮어쓴다.
			data: JSON.stringify({
				userEmail: $("#adminLoginId").val(),
				userPassword: $("#currentpassword").val()
				// userType: "user",
			}),
			datatype: "json",
			contentType: "application/json",
			success: function (res) {
				//accessToken 저장
				util.setStorage("accessToken", res.data.accessToken);

          //userEmail 저장
          util.setStorage("keepEmail", keepEmail);
          util.setStorage("userEmail", $("#adminLoginId").val());
        //userAuthor 저장
        util.setStorage("userAuthor", res.data.userAuthor);
        //userSeq 저장
        util.setStorage("userSeq", res.data.userSeq);
        util.setStorage("userNm", res.data.userNm);
        
        window.location.assign(MAIN_PAGE);
        // location.href = MAIN_PAGE;
      },
      error: function (err) {
        console.log(err.responseJSON.message); 
        alert(err.responseJSON.message);
      //  $("#loginFail").modal("show");
       // $("#modalText").text(err.responseJSON.message);
      },
    });
  },
 enterkey: () =>{
    if (window.event.keyCode == 13) {
      login();
    }
  }
,
	logout: () => {  
	const kakaoAccessToken = document.cookie.split("; ")[0].split("=")[1];
		if (!(JSON.parse(window.localStorage.getItem("sch")).userSeq===null)) {
			$.sendAjax({
				url: "/login/logout.api",
				method: "POST", 
				data : { userSeq: util.getUserSeq() },
			//	data: JSON.stringify({usrSeq: JSON.parse(window.localStorage.getItem("sch")).userSeq	}),
				contentType: "application/json",
				success: (res) => {
					console.log("🚀 ~ res:", res);
					location.href = "/sch/huss/dashBoard/main.html"
					util.clearStorage();
				}
			})
		} else if(!(kakaoAccessToken===null || kakaoAccessToken === undefined)) {
				$.sendAjax({
				url: "/auth/kakaoLogout.api",
				data: {},
				contentType: "application/json",
				success: (res) => {
					console.log("🚀 ~ res:", res);
					location.href = "/sch/huss/dashBoard/main.html"
				},
      error: function (err) {
        console.log(err.responseJSON.message); 
        alert(err.responseJSON.message);
      
      },
			})
			
		}
	},
	setStorage: (key, val) => {
		const storage = window.localStorage.getItem("sch");
		const data = {
			...JSON.parse(storage),
			[key]: val,
		};
		window.localStorage.setItem("sch", JSON.stringify(data));
	},
	clearStorage: () => {
		window.localStorage.removeItem("sch");
	},
	clearStorage2: (key, val) => {
		const storage = window.localStorage.getItem("sch")
		;const data = {
			...JSON.parse(storage),
			[key]: val,
		};
		window.localStorage.removeItem("sch", JSON.stringify(data));
	
	},
	clearloginMode : () =>{
		window.localStorage.removeItem(util.getStorage("loginMode"));
	},
	clearlogoutMode: () =>{
		window.localStorage.removeItem(util.getStorage("logoutMode"));
	},
	getStorage: (key) => {
		const storage = window.localStorage.getItem("sch");
		return storage && JSON.parse(storage)[key];
	},
	getToken: () => {
		return util.getStorage("accessToken");
	},
	getUserSeq: () => {
		return util.getStorage("userSeq");
	},
	getUserEmail: () => {
		return util.getStorage("userEmail");
	},
	getUserAuthor: () => {
		return util.getStorage("userAuthor");
	},
	getuserNm: () => {
		return util.getStorage("userNm");
	},

	/** 파일에서 orientation 방향추출 */
	extractOrientation: async (file) => {
		const tags = await ExifReader.load(file);
		const orientation = tags.Orientation?.value;
		return orientation || 1;
	},
	/** 압축된 file을 promise로 리턴 */
	getCompressed: (file, option) => {
		return new Promise((res, rej) => {
			new Compressor(file, {
				// quality: 0.6,
				maxWidth: 400,
				maxHeight: 400,
				convertSize: 1000000,
				success(result) {
					res(result);
				},
				error(err) {
					rej(err);
				},
				...option,
			});
		});
	},

	/** binary 받아서 Base64 Encode 문자열로 반환 Ex.) data:image/jpeg; base64, GDYG….*/
	blobToBase64: (blob) => {
		return new Promise((resolve, _) => {
			const reader = new FileReader();
			reader.onloadend = () => resolve(reader.result);
			reader.readAsDataURL(blob);
		});
	},

	/** url 파라미터를 json으로 리턴 a.com?a=1&b=2 -> {a:1, b:2} */
	getUrlParamJson: (url = location.href) => {
		const result = {};
		url.replace(/[?&]{1}([^=&#]+)=([^&#]*)/g, function(s, k, v) {
			result[k] = decodeURIComponent(v);
		});
		return result;
	},

	/** 파라미터 명으로 value 얻기 ex a.com?a=1&b=2 -> getParameterByName("a") // 결과1 */

	getParameterByName: (name) => {
		name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
		var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
			results = regex.exec(location.search);
		return results == null ? "" : results[1];
	},
	// 미사용시 삭제요망
	//   canvasToBlob: (dataURI) => {
	//     var byteString = atob(dataURI.split(",")[1]);
	//     var mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];
	//     var ab = new ArrayBuffer(byteString.length);
	//     var ia = new Uint8Array(ab);
	//     for (var i = 0; i < byteString.length; i++) {
	//       ia[i] = byteString.charCodeAt(i);
	//     }

	//     var bb = new Blob([ab], { type: mimeString });
	//     return bb;
	//   },
	tableSetting: () => {
		var sortdir = "";
		/**
		 * th 클릭 시 정렬.
		 */
		$("#tableList th").each(function(column) {
			$(this).click(function() {
				if ($(this).is(".asc")) {
					$(this).removeClass("asc");
					$(this).addClass("desc");
					sortdir = -1;
				} else {
					$(this).addClass("asc");
					$(this).removeClass("desc");
					sortdir = 1;
				}

				$(this).siblings().removeClass("asc");
				$(this).siblings().removeClass("desc");

				var rec = $("#tableList").find("tbody > tr").get();
				rec.sort(function(a, b) {
					var val1 = $(a).children("td").eq(column).text().toUpperCase();
					var val2 = $(b).children("td").eq(column).text().toUpperCase();
					/*if(val1 === "" && val2 === ""){
									val1 = document.querySelector(a).dataset.value
									val2 = document.querySelector(b).dataset.value
								}*/
					return val1 < val2 ? -sortdir : val1 > val2 ? sortdir : 0;
				});

				$.each(rec, function(index, row) {
					$("#tableList tbody").append(row);
				});
			});
		});

		$("#tableList tbody").on("click", "tr", function() {
			if ($(this).hasClass("selected")) {
				$(this).removeClass("selected");
			} else {
				$("tr.selected").removeClass("selected");
				$(this).addClass("selected");
			}
		});
	},

	sjChk: (str) => {
		/* 제목 100자 이내 */
		var obj = String(str);
		if (obj.length > 100 || obj.length === 0) {
			return true;
		}
		return false;
	},

	cnChk: (str) => {
		/* 내용 2000자 이내 */
		var obj = String(str);
		if (obj.length > 2000 || obj.length === 0) {
			return true;
		}
		return false;
	},

	cnTime: (time) => {
		getHour = time.substring(0, 2);
		getMin = time.substring(2, 4);
		var cnTime = getHour + " : " + getMin;
		return cnTime;
	},

	mToHi: (minute) => {
		var h = Math.floor(minute / 60);
		var i = minute % 60;
		if (h === 0 && i !== 0) {
			h = "00";
			if (i < 10 && i > 0) {
				i = "0" + i;
			}
		} else if (h !== 0 && i === 0) {
			i = "00";
			if (h < 10 && h > 0) {
				h = "0" + h;
			}
		} else if (h !== 0 && i !== 0) {
			if (i < 10 && i > 0) {
				i = "0" + i;
			}
			if (h < 10 && h > 0) {
				h = "0" + h;
			}
		} else {
			return "00 : 00";
		}

		return h + " : " + i;
	},

	addDateDot: (date) => {
		date = date.replace(/[^0-9]/g, "")
		var year = date.substring(0, 4);
		var month = date.substring(4, 6);
		var day = date.substring(6, 8);

		return year + "." + month + "." + day;
	},

	lastMonth: () => {
		var date = new Date();
		var year = date.getFullYear();
		var month = ("0" + (1 + date.getMonth())).slice(-2);

		return year + "-" + month;
	},
	changeCheckedStatus: (checkedStatus, inputTagName) => {
		let userKindInputList = document.getElementsByName(inputTagName);
		$.each(userKindInputList, function(idx, obj) {
			obj.checked = checkedStatus;
		});
	},
	changeCheckedStatusByClass: (checkedStatus, inputTagClass) => {
		let userKindInputList = document.getElementsByClassName(inputTagClass);
		$.each(userKindInputList, function(idx, obj) {
			obj.checked = checkedStatus;
		});
	},
	emptyCheck: (str) => {
		// 파라미터 확인후 "" 리턴.
		var obj = String(str);
		if (obj == null || obj == undefined || obj == "null" || obj == "undefined" || obj.length == 0)
			return "";
		else
			return str;
	},
	numToDay: (obj) => {
		// 요일변환 : 숫자를 한글로 반환
		let str = obj.replace("1", "월")
			.replace("2", "화")
			.replace("3", "수")
			.replace("4", "목")
			.replace("5", "금")
			.replace("6", "토")
			.replaceAll(",", "")
		return str
	},
	
	chnNumToDay: (obj) => {
		// 요일변환 : 숫자를 한글로 반환 + 요일 사이 '/'
		let str = obj.replace("1", "월")
			.replace("2", "화")
			.replace("3", "수")
			.replace("4", "목")
			.replace("5", "금")
			.replace("6", "토")
			.replaceAll(",", "/")
		return str
	},

	// 마지막 key값 다음 문자열 반환 함수
	getLastString: (fileCours) => {
		let obj = fileCours
		let str = ""
		if (obj.substring(0, 1) === "C") {
			str = obj.substring(obj.lastIndexOf("\\") + 1);
		} else {
			str = obj.substring(obj.lastIndexOf("/") + 1);
		}
		return str;
	},

	// 파일이름의 확장자 체크 후 type 반환 함수
	chkType: (str) => {
		const imgTypes = ['jpg', 'jpeg', 'png', 'gif'];
		const videoTypes = ['mp4'];
		let typeNm = str.substring(str.lastIndexOf('.') + 1);
		let type = ""
		if (imgTypes.includes(typeNm)) {
			type = "image"
		} else if (videoTypes.includes(typeNm)) {
			type = "video"
		}
		return type;
	},
	getDownloadUrl: (fileSeq) => {
		// const host = "https://schback2.musicen.com";
		const host = "";
		return host + `/cmmn/fileDownload.api?fileSeq=${fileSeq}`
	},
	userChk: (obj) => {
		let userChk = false
		if (obj === loginUserSeq || loginUserAuthor === 'SA') {
			userChk = true
		}
		return userChk
	},
	myPage: () => {
		const userSeq = util.getUserSeq();
		location.href = `/sch/admin/sysManage/mberDetail.html?userSeq=${userSeq}&flag=admin`
	},
	screenToggle: (flag) => {
		window.scrollTo({ top: 0 });
		if (flag) {
			document.documentElement.style.width = "100vw";
			document.documentElement.style.height = "100vh";
			document.documentElement.style.overflow = "hidden";
		} else {
			document.documentElement.style.width = null;
			document.documentElement.style.height = null;
			document.documentElement.style.overflow = null;
		}
	},
};

//
function fileDownload(fileSeq, fileDetailSn) {
	const host = "";
	$.sendAjax({
		url: host + "/download/fileDownload.api",
		data: { fileSeq: fileSeq, fileDetailSn: fileDetailSn },
		contentType: "application/json",
		xhrFields: {
			responseType: "blob",
		},
		success: (res, status, xhr) => {
			console.log("🚀 ~ fileDownload ~ res:", res);
			var filename = "downloadFile";
			if (xhr.getResponseHeader("Content-Disposition")) {
				//filename
				filename = xhr.getResponseHeader("Content-Disposition").split("filename=")[1].split(";")[0];
				filename = decodeURIComponent(filename);
			}
			var link = document.createElement("a");

			link.href = URL.createObjectURL(res);
			link.download = filename;
			link.click();
		},
	});
}

// 모달창

function callPop(classId) {
	$(classId).show();
	$("body").addClass("ovf_hdn");
}

function closePop(classId) {
	$(classId).hide();
	$("body").removeClass("ovf_hdn");
}

function search_details() {
	$(document).on("click", ".search_details img", function() {
		$(".visibil").stop().slideToggle();
		$(".search_details").toggleClass("on");
	});
}

const loginUserSeq = util.getUserSeq() // 접속유져 userSeq
const loginUserAuthor = util.getUserAuthor() // 접속유저 userAuthor
