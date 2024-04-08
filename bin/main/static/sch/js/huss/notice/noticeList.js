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
	noticeTimeList: {}
};

let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
var vm;
let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
       }, 
        methods: {
            fnSearch: function (userAuthor) {
				this.searchData.pageNo = 1;
				event.getCnsltList();
            },
			fnDtail: (noticeSeq) => {
				 location.assign("/sch/huss/notice/noticeDetail.html?noticeSeq=" + noticeSeq)
			}, 
		}
    });
    vm = app.mount("#content");
};

let event = {
    getNoticeList: () => {
        $.sendAjax({
            url: "/noticeController/selectNoticeSlidetestList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
				vm.totalCount = res.data.totalCount;
				vm.noticeList = res.data.list;
				
				console.log(res.data)
				console.log(res.data.list)
               
				for (var i = 0; i < vm.noticeList.length; i++) {
				// 글자가 8글자 이상일때 ...
					if(vm.noticeList[i].noticeSj.length>= 8){
    					vm.noticeList[i].noticeSj = vm.noticeList[i].noticeSj.substring(0, 8) + '...';
						 
				// 글자가 20글자 이상일때 ...
					if(vm.noticeList[i].noticeCn.length>= 20){
    					vm.noticeList[i].noticeCn = vm.noticeList[i].noticeCn.substring(0, 20) + '...';
					}
					}
					 
				}
				// 페이지
				fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
					vm.searchData.pageNo = selectPage;
					event.getNoticeList();
				})
						
            },
            error: function (e) {
                $.alert(e.responseJSON);
            }
        });
    } 
   
};

$(document).ready(() => {
    vueInit();
    event.getNoticeList();
    util.tableSetting();
});
