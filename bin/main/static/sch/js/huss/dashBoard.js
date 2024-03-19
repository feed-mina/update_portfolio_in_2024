let vueData = {
    userSignUpToday: 0,
    userSignUpYesterday: 0,
    lctreList:[],
    seminaList:[],
    noticeList: [],
    exprnList:[],
    noticeList:[],
    qnaList: [],
    loginCntList: "",
    dailyList: [],
    monthlyList: [],
    timesList: [],
};
let today = util.date.getToday()
let vm;
let userAuthorRatioLegend = "";
let userAuthorRatioValue = "";
let exTime = util.date.getTime().replaceAll(':', '').substring(0,4)

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        methods: {
					initData: () =>{
						vm.drowCht()	// 접속자 수 차트
						vm.loginCnt()	// 접속자 수 조회
						vm.tdLctre()	// 오늘의 강의
						vm.tdSemina()	// 오늘의 세미나
						vm.tdNotice()	// 오늘의 상담
						vm.tdExprn()	// 오늘의 체험
						vm.mainNotice()	// 공지사항
						vm.mainQna()	// Q&a
					},
					
					loginCnt : () => {
						
						let daily = {
							resultType : 'daily',
							searchType : '%Y-%m-%d',
							dateType : '%m월 %d일'
						}
						let monthly = {
							resultType : 'monthly',
							searchType : '%Y-%m-%d',
							dateType : '%Y년 %m월'
						}
						let times = {
							resultType : 'times',
							searchType : '%Y-%m-%d %H',
							dateType : '%H시'
						}
						
						let param = {
							daily : daily,
							monthly : monthly,
							times : times
						}
						console.log(param)
						$.sendAjax({
	            url: "/main/selectUserLoginCountList.api",
	            data: param,
	            contentType: "application/json",
	            success: (res) => {
									vm.dailyList = res.data.daily
									vm.monthlyList = res.data.monthly
									vm.timesList = res.data.times
									vm.fnCht();
									
	            },
	            error: function (e) {
	                alert(e.responseJSON.message);
	            },
		        });
					},
					
					fnCht: (resultType) => {
						vm.loginCntList.data.labels = []
						vm.loginCntList.data.datasets[0].data = null
            vm.loginCntList.update();
            
            let chtData = null
            
            if(resultType === "daily"){
							chtData = vm.dailyList
						}else if(resultType === "monthly"){
							chtData = vm.monthlyList
						}else if(resultType === "times"){
							chtData = vm.timesList
						}else{
							chtData = vm.dailyList
						}
            
						if(chtData.length > 0){
							chtData.forEach(function (i, idx) {
								vm.loginCntList.data.datasets[0].data[idx] = i.userCnt;
          			vm.loginCntList.data.labels[idx] = i.loginTm;
							})
						}
						vm.loginCntList.update();
					},
					
					drowCht: () => {
						const ctx = $("#myChart")

		        vm.loginCntList = new Chart(ctx, {
		            type: 'line',
		            data: {
		                labels: "",
		                datasets: [{
		                    label: false,
		                    data: "",
		                    borderColor: "#4e73df",
		                    backgroundColor: "#4e73df",
		        						fill: false,
		                    borderWidth: 1,
		                    lineTension: 0
		                }],
		            },
		            options: {
									responsive: false,
		              legend: {
										display: false
									},
									animation: false,
									defaultFontSize: 18,
									scales: {
					            xAxes: [{
				                display: true,
				                beginAtZero: true,
				                gridLines: {
									        display: false
									      },
									      offset : true,
					            }],
					            yAxes: [{
												afterDataLimits: (scale) => {
													// Y축 max값 * 1.2
													scale.max = scale.max * 1.2;
												},
												ticks:{
							            beginAtZero: true
							          },
											}]
					        }
		            }
		        });
					},
					
					tdLctre : () => {
						$.sendAjax({
                url: "/main/selectTodayLctre.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.lctreList = res.data
                    vm.lctreList.forEach(function(item) {
											item.useAt = 'Y'
											if(item.lctreBeginTime){
												if(item.lctreBeginTime > exTime){
													item.useAt = 'N'
												}
												item.lctreBeginTime = util.cnTime(item.lctreBeginTime)
											}
											if(item.lctreEndTime){
												item.lctreEndTime = util.cnTime(item.lctreEndTime)
											}
										})
                },
            });
					},
					movLctreList : () => {
						location.href= window.origin + "/sch/admin/lctreSemina/lctreOperateList.html"
					},
					movLctreDetail : (lctreSeq, lctreSn) => {
						location.href= window.origin + "/sch/admin/lctreSemina/lctreOperateDetail.html?lctreSeq=" + lctreSeq + "&lctreSn=" + lctreSn
					},
					tdSemina : () => {
						$.sendAjax({
                url: "/main/selectTodaySemina.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.seminaList = res.data
                    vm.seminaList.forEach(function(item) {
											item.useAt = 'Y'
											if(item.seminaBeginTime){
												if(item.seminaBeginTime > exTime){
													item.useAt = 'N'
												}
												item.seminaBeginTime = util.cnTime(item.seminaBeginTime)
											}
											if(item.seminaEndTime){
												item.seminaEndTime = util.cnTime(item.seminaEndTime)
											}
										})
                },
            });
					},
					movSeminaList : () => {
						location.href= window.origin + "/sch/admin/lctreSemina/seminaOperateList.html"
					},
					movSeminaDetail : (seminaSeq) => {
						location.href= window.origin + "/sch/admin/lctreSemina/seminaOperateDetail.html?seminaSeq=" + seminaSeq
					},
					tdNotice : () => {
						$.sendAjax({
                url: "/main/selectTodayNotice.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.noticeList = res.data
                    vm.noticeList.forEach(function(item) {
											
											item.useAt = ''
											if(item.noticeBeginTime){
												if(item.noticeBeginTime > exTime && item.onAirAt === "N"){
													item.useAt = '진행 전'
												}else if(item.noticeBeginTime < exTime && item.onAirAt === "N"){
													item.useAt = '진행 완료'
												}else{
													item.useAt = '진행 중'
												}
												item.noticeBeginTime = util.cnTime(item.noticeBeginTime)
											}
											if(item.noticeEndTime){
												item.noticeEndTime = util.cnTime(item.noticeEndTime)
											}
										})
                },
            });
					},
					movNoticeList : () => {
						location.href= window.origin + "/sch/admin/noticeExprn/noticeFx.html"
					},
					movNoticeDetail : (noticeSeq, noticeSn) => {
						location.href= window.origin + "/sch/admin/noticeExprn/noticeOperateDetail.html?noticeSeq=" + noticeSeq + "&noticeSn=" + noticeSn
					},
					tdExprn : () => {
						$.sendAjax({
                url: "/main/selectTodayExprn.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.exprnList = res.data
                    vm.exprnList.forEach(function(item) {
											
											item.useAt = ''
											if(item.exprnBeginTime){
												if(item.exprnBeginTime > exTime && item.onAirAt === "N"){
													item.useAt = '진행 전'
												}else if(item.exprnBeginTime < exTime && item.onAirAt === "N"){
													item.useAt = '진행 완료'
												}else{
													item.useAt = '진행 중'
												}
												item.exprnBeginTime = util.cnTime(item.exprnBeginTime)
											}
											if(item.exprnEndTime){
												item.exprnEndTime = util.cnTime(item.exprnEndTime)
											}
										})
                },
            });
          },
          movExprnList : () => {
						location.href= window.origin + "/sch/admin/noticeExprn/experienceFx.html"
					},
					movExprnDetail : (exprnSeq, exprnSn) => {
						location.href= window.origin + "/sch/admin/noticeExprn/experienceOperateDetail.html?exprnSeq=" + exprnSeq + "&exprnSn=" + exprnSn
					},
          mainNotice : () => {
						$.sendAjax({
                url: "/main/selectMainNotice.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.noticeList = res.data
                },
            });
          },
          movNoticeList: () => {
					  location.href= window.origin + "/sch/admin/board/noticeList.html"
				  },
          movNotice: (bbsSeq) => {
					  location.href= window.origin + "/sch/admin/board/noticeDetail.html?bbsSeq=" + bbsSeq
				  },
				  mainQna : () => {
						$.sendAjax({
                url: "/main/selectMainQna.api",
                data:{},
                contentType: 'application/json',
                success: function (res) {
                    vm.qnaList = res.data
                },
            });
          },
          movQnaList: () => {
					  location.href= window.origin + "/sch/admin/board/qnaList.html"
				  },
          movQna: (bbsSeq, ans) => {
					  if(ans === "N") {
							location.href= window.origin + "/sch/admin/board/qnaRegist.html?bbsSeq=" + bbsSeq
						}else{
							location.href= window.origin + "/sch/admin/board/qnaDetail.html?bbsSeq=" + bbsSeq
						}
				  }
        },
    });
    vm = app.mount("#content");
}

let loginCntList = null;
let memberRatioChart = null;

let event = {
    init: () => {
        const doughnutChart = document.getElementById('doughnutChart');
        
        memberRatioChart = new Chart(doughnutChart, {
            type: 'doughnut',
            data: {
                labels: "",
                datasets:[{
                    label: '# of Votes',
                    data: "",
                    borderWidth: 1,
                    backgroundColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)',
                        'rgba(255, 159, 100, 1)',
                    ],
                }],
                
            },
            options: {
				 				responsive: true,
								legend: {
				            position: 'right',
				            display: true,
				            labels: {
											fontSize: 20,
											padding: 15
										}
				        },
                animation: false,
                defaultFontSize: 18,
                
            },
        });
    },
    getUserCountSignUpToday: () => {
        let today = util.date.addDateDash(util.date.getToday());
        let paramMap = {'dateStr':today};
        $.sendAjax({
            url: "/main/selectCountUserSignUpSomeday.api",
            data: paramMap,
            contentType: "application/json",
            success: (res) => {
                vm.userSignUpToday = res.data.userCount;
            },
            error: function (e) {
                alert(e.responseJSON.message);
            },
        });
    },
    getUserCountSignUpYesterday: () => {
        let yesterday = util.date.addDateDash(util.date.getYesterday());
        let paramMap = {'dateStr':yesterday};
        $.sendAjax({
            url: "/main/selectCountUserSignUpSomeday.api",
            data: paramMap,
            contentType: "application/json",
            success: (res) => {
                vm.userSignUpYesterday = res.data.userCount;
            },
            error: function (e) {
                alert(e.responseJSON.message);
            },
        });
    },
    getUserAuthorRatioArr: () => {
        let today = util.date.addDateDash(util.date.getToday());
        let paramMap = {};
        $.sendAjax({
            url: "/main/selectUserAuthorRatioList.api",
            data: paramMap,
            contentType: "application/json",
            success: (res) => {

                memberRatioChart.data.datasets[0].data = res.data.authorCountArr;
                memberRatioChart.data.labels = res.data.userAuthorArr;
                memberRatioChart.update();
            },
            error: function (e) {
                alert(e.responseJSON.message);
            },
        });
    },
        
}

$(document).ready( () => {
    vueInit();
    vm.initData()
    event.init();
    event.getUserCountSignUpToday();
    event.getUserCountSignUpYesterday();
    event.getUserAuthorRatioArr();
});
