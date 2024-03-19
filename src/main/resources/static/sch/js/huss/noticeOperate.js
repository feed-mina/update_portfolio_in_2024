var vueData = {
	noticeOperateList : [],
	imageButton: "false",
	step: "master",
	noticeUserSeq: 0,
	totalCount: 0, 
    preview:"",
	noticeFx : {},
	modifyData: {},
	noticeList: {},
	searchData: {
		deletedNoticeInclude: false,
		searchText: '',
		pageNo: 1,
		pageLength: 10,
	},
	deletedChekedValue : '',
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
				event.getNoticeOperateList();
			},
            fnNoticeOperationDetail: (noticeSeq, noticeSn) => {
				 location.assign("/sch/huss/notice/noticeOperate.html?noticeSeq=" + noticeSeq+"&noticeSn="+noticeSn)
            }  
			,
			fnDtail: (noticeSeq) => {
				 location.assign("/sch/huss/notice/noticeOperate.html?noticeSeq=" + noticeSeq)
			} 
		}
	})
	vm = app.mount("#content");

};
 let  event = {
	init: () => {  
	} 
	,getNoticeOperateList : () =>{
        $.sendAjax({
            url: "/notice/selectNoticeOperateList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => { 
				console.log(res)
                vm.totalCount = res.data.totalCount;
                vm.noticeOperateList = res.data.list;  
                console.log( vm.noticeOperateList)
                let len = vm.noticeOperateList.length;
                  for(i=0; i<len; i++){
					  // vm.noticeOperateList[i].noticeWeekArray = res.data.list[i].noticeWeekArray
                		let noticeDayArray =[]
                		 vm.noticeOperateList[i].noticeWeekArray
					  	console.log(res.data.list[i].noticeWeekArray.split(","))
					  	console.log( util.date.addDateDash(vm.noticeOperateList[0].noticeDt))
					   
					  /**
					   * 	if(vm.noticeOperateList[i].noticeWeekArray = '1'){
							 noticeDayArray.push(vm.noticeOperateList[i].noticeWeekArray.getDay()) 
						  }
						  else if(vm.noticeOperateList[i].noticeWeekArray = '2'){
							 noticeDayArray.push(vm.noticeOperateList[i].noticeWeekArray.getDay()) 
						  }
						 
					   */
					  
				  }
				    fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
                    vm.searchData.pageNo = selectPage;
                    event.getNoticeOperateList();});
				}
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            }
        });
    }  
};

$(document).ready(() => {
	vueInit();
	util.tableSetting();  
	event.getNoticeOperateList();
    event.init(); 
});

