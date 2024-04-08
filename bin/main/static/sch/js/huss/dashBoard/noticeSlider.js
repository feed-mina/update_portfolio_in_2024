var vueData = {
	imageButton: "false",
	step: "master",
	totalCount: 9,
	modifyData: {},
	noticeList: [],
	noticeSlideList: [],
	searchData: {
		searchText: '',
		pageNo: 1,
		pageLength: 10,
	},
	noticeTimeList: {}
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
				event.getnoticeList();
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
	, getnoticeList: () => {
	//let noticeSeq = 'CNSLT_0001'
	//let paramMap = {'noticeSeq': ''};
		$.sendAjax({
			url: "/noticeController/selectNoticeSlideList.api", 
	    	data: vm.searchData,
			contentType: "application/json",
			success: (res) => { 
				console.log(res.data) 
				let totalCount = res.data.length;
				vm.noticeSlideList = res.data;
				for (var i = 0; i <totalCount; i++) {
				// 글자가 8글자 이상일때 ...
					if(vm.noticeSlideList[i].noticeSj.length>= 8){
    					vm.noticeSlideList[i].noticeSj = vm.noticeSlideList[i].noticeSj.substring(0, 8) + '...';
					}
				// 글자가 20글자 이상일때 ...
					if(vm.noticeSlideList[i].noticeCn.length>= 10){
    					vm.noticeSlideList[i].noticeCn =vm.noticeSlideList[i].noticeCn.substring(0, 10) + '...';
					}
			   
						 console.log(vm.noticeSlideList[i].registDt);  
				}
			 
			}
			, error: function(e) {
				console.log(e);
			}
		});
	
	 }  
};

$(document).ready(() => {
	vueInit();
	event.getnoticeList(); 
});

