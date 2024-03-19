var vueData = {
	imageButton: "false",
	step: "master",
	totalCount: 0,

	modifyData: {},
	noticeList: [],
	searchData: {
		searchText: '',
		pageNo: 1,
		pageLength: 10,
	},
};

let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
var vm;

var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: {
			fnSearch: function(userAuthor) {
				this.searchData.pageNo = 1;
				event.getNoticeList();
			},
			fnDtail: (noticeSeq) => {
				 location.assign("/sch/huss/notice/noticeDetail.html?noticeSeq=" + noticeSeq)
			},


		}
	})
	vm = app.mount("#content");

};
 let  event = {
	init: () => {
	}
	, getNoticeList: () => {
		$.sendAjax({
			url: "/notice/selectNoticeList.api",
			data: vm.searchData,
			contentType: "application/json",
			success: (res) => {
				vm.totalCount = res.data.totalCount;
				vm.noticeList = res.data.list;
				// 요일변환 numToDay(obj)
				for (var i = 0; i < vm.noticeList.length; i++) {
					let day = []
					if (vm.noticeList[i].noticeWeekArray !== "") {
						day = vm.noticeList[i].noticeWeekArray.split(',');

						let dayname = Object.fromEntries(
							Object.entries(day).map(([key, value]) =>
								[key, value])
						);
						var dvalue = Object.values(dayname);

						let name = dvalue.map(dayname => {
							if (Object.values(dayname) == 1) {
								return "월";
							} if (Object.values(dayname) == 2) {
								return "화";
							} if (Object.values(dayname) == 3) {
								return "수";
							} if (Object.values(dayname) == 4) {
								return "목";
							} if (Object.values(dayname) == 5) {
								return "금";
							} if (Object.values(dayname) == 6) {
								return "토";
							}
							return "오류";
						})
						vm.noticeList[i].noticeWeekArray = name;
						vm.noticeList[i].noticeWeekArray = Object.values(vm.noticeList[i].noticeWeekArray).toString().replaceAll(",", "/");

					}
				}
				// 페이지
				fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
					vm.searchData.pageNo = selectPage;
					event.getNoticeList();
				})
			}
			, error: function(e) {
				$.alert(e.responseJSON.message);
			}
		});
	}
};

$(document).ready(() => {
	vueInit();
	util.tableSetting();
	event.getNoticeList();
});

