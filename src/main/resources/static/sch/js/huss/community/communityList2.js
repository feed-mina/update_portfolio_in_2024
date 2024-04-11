var vueData = {
    isLogin: false,
    isAward: false,
    totalCount: 0,
    communityList: [],

    searchData: {
        pageNo: 1,
        dataPerPage: util.isMobile() ? 3 : 6,
        useAt: "Y",
        tagAt: '',
        awardAt: '',
        searchText: "",
        searchDivision: "",
    },
}

let vm;
let pageCount = 10;

var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;			
		},
		methods: {
            util: () => util,
            fnTagAt: function(val){
                vm.searchData.tagAt = val;
                vm.searchData.pageNo = 1;
                events.selectCommunityList();
            },
            fnToggle: function(val){
                vm.isAward = val;
                vm.searchData.awardAt = val ? "Y" : "";
                vm.searchData.pageNo = 1;
                events.selectCommunityList();
            },
           
            fnSearch: function () {
                this.searchData.pageNo = 1;
                events.selectCommunityList();
            },
            fnDetail: function(community) {
                console.log("🚀 ~ vueInit ~ community:", community);
                location.href = "communityDetail.html?communitySeq=" + community.communitySeq;
            },
		}
	})
	vm = app.mount('#container');
}

var events = {
	init : () => {
        // 로그인체크
        const token = util.getToken();
        vm.isLogin = token != null;

        // 탭 init값
        const urlParam = util.getUrlParamJson();
        const tab = urlParam.tab;
        if(tab == 1){
            vm.fnToggle(true)
        }

    },
	
	selectCommunityList: (clearList = true) => {
        $.sendAjax({
            url: "/community/selectCommunityList.api",
            // url: "http://localhost:3000",
            data: vm.searchData,
            contentType: "application/json",
            success: async (res) => {
                // res.data = {
                //     list : [],
                //     totalCount:0
                // }

                if(clearList){
                    vm.totalCount = 0;
                    vm.communityList = [];
                }
                
                if(util.isMobile()){
                    vm.totalCount = res.data.totalCount;
                    vm.communityList = [...vm.communityList, ...res.data.list];
                }else{

                    vm.communityList = res.data.list;
                    vm.totalCount = res.data.totalCount;
    
                    fnPaging(res.data.totalCount, res.data.dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
                        vm.searchData.pageNo = selectPage;
                        events.selectCommunityList();
                    })
                }


            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        })
	},

    initInfiniteScroll: () => {
        const listEnd = document.querySelector('#footer'); // 관찰할 대상(요소)
        const options = {
            root: null, // 뷰포트를 기준으로 타켓의 가시성 검사
            rootMargin: '0px 0px 0px 0px', // 확장 또는 축소 X
            threshold: 0, // 타켓의 가시성 0%일 때 옵저버 실행
        };

        const onIntersect = (entries, observer) => {
            entries.forEach(async (entry) => {
                if (entry.isIntersecting) {
                    // 아직 덜불러왔으면 한번더
                    if(vm.searchData.pageNo * vm.searchData.dataPerPage < vm.totalCount){
                        vm.searchData.pageNo = vm.searchData.pageNo + 1;
                        events.selectCommunityList(false);
                    }
                    
                }
            });
        };
        
        const observer = new IntersectionObserver(onIntersect, options); // 관찰자 초기화
        observer.observe(listEnd); // 관찰할 대상(요소) 등록
    }
}


$(document).ready( () => {
	vueInit();
    events.init();
    events.selectCommunityList();

    if(util.isMobile()){
        events.initInfiniteScroll();
    }
})

