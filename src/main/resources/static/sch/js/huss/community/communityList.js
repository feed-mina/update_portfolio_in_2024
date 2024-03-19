let vueData = {
    totalCount: 0,
    communityList: [],
    searchData: {
        division: "",
        tag: "",
        searchKnd: "",
        searchText: "",
        pageLength: "10",
        pageNo: "",
    },
    userAuthor: "", //로그인한 사용자의 권한
    userName: "",   //로그인한 사용자의 이름
    userEmail: "",  //로그인한 사용자의 이메일
    userSeq: "",    //로그인한 사용자의 일련번호
    accessToken: "",
    userId:"",      //로그인ID
    pagination: {
        division: "",
        pageNo: "1",
        dataPerPage: "12",
        pageLength:"10",
        totalData: "",
        totalPage: "",
        pageGroup: "",
        last:"",
        first:"",
        selectedLast:"",
        tag : "",
    },
    isActive: true,
    tag : "",
};

let vm;

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        computed: {
            pagingList() {
                var pagingListVar = [];
                for (var i=this.pagination.first; i<=this.pagination.last; i++) {
                    pagingListVar.push(i);
                }
                return pagingListVar;
            },
        },
        methods: {
            fnSearch: function () {
                this.searchData.pageNo = 1;
                this.pagination.pageNo = this.searchData.pageNo;
                event.getCommunityList();
            },
            ellipsisText: (text) => {
                return util.formmater.textLengthOverCut(text, null, null);
            },
            fnPaging: function (pageNo) {
                this.searchData.pageNo = pageNo;
                this.pagination.pageNo = this.searchData.pageNo;
                event.getCommunityList();
            },
            setTag: function (tag) {
                    let videoEl = $(".video");
                    let posterEl = $(".poster");
                    
                        if (tag == 'video') {
                console.log('video 테그') 
                    for (var i= 0; i<videoEl.length; i++) {
                       posterEl[i].classList == "posterEl on"; 
                        console.log(videoEl[i])
                        console.log(posterEl[i]) 
                        // video toggle >> 카드에 id를 video로 바꾸고 css를 display:none한다.   
                       	 let videolitag = document.getElementsByClassName('posterlitag')[i] 
                       	 let posterlitag = document.getElementsByClassName('videolitag')[i]  
                        videolitag.classList.add("toggleOFFposter") 
                        document.querySelectorAll('.toggleOFFposter')[i].style.display = 'none';     
                        console.log(videolitag)  
                        console.log('videolitag---------------') 
                    posterlitag.classList =  posterlitag.classList[0]
                        console.log(  posterlitag.classList)   
                      
                    }
                }  
             if (tag == 'poster') {
                console.log('poster 테그') 
                    for (var i= 0; i<posterEl.length; i++) {
                            videoEl[i].classList == "videoEl on";  
                        console.log(videoEl[i])  //토글버튼 video
                       console.log(posterEl[i])// 토글버튼 poster 
                        // poster toggle >> 카드에 id를 poster로 바꾸고 css를 display:none한다. 
                       	 let posterlitag = document.getElementsByClassName('videolitag')[i] 
                       	 let videolitag = document.getElementsByClassName('posterlitag')[i] 
                        posterlitag.classList.add("toggleOFFvideo")
                        document.querySelectorAll('.toggleOFFvideo')[i].style.display = 'none';    
                        console.log( posterlitag)  
                        console.log('posterlitag---------------')   
                    videolitag.classList =  videolitag.classList[0]
                        console.log(  videolitag.classList)    
                    }
                }
            },
        },
    });
    vm = app.mount("#content");
};

let event = {
    init: () => {
    // 자유 게시
        $(document).on("click", "#freeNtce", function (e) {
            vm.searchData.division = "F";
            vm.pagination.division = vm.searchData.division;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
    // 수상작
        $(document).on("click", "#prizeNtce", function (e) {
            vm.searchData.division = "P";
            vm.pagination.division = vm.searchData.division;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
       // Video        
        $(document).on("click", "#VideoDivison", function (e) {
            vm.searchData.tag = "video";
            vm.tag = "video";
            vm.pagination.tag = vm.searchData.tag;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
       // Poster       
        $(document).on("click", "#PosterDivison", function (e) {
            vm.searchData.tag = "poster";
            vm.tag = "poster";
            vm.pagination.tag = vm.searchData.tag;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
       
        
    },
    getCommunityList: () => {
        $.sendAjax({
            url: "/communityController/getPagination.api",
            data: vm.pagination,
            contentType: "application/json",
            success: (res) => {
                vm.pagination.totalData = res.data.totalData;
                vm.pagination.totalPage = res.data.totalPage;
                vm.pagination.pageGroup = res.data.pageGroup;
                vm.pagination.last = res.data.last;
                vm.pagination.first = res.data.first;
                vm.pagination.pagingList = res.data.pagingList;
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
        $.sendAjax({
            url: "/communityController/selectCommunityList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
            console.log(res.data)
            console.log( res.data.totalCount)
            	vm.pagination.totalData = res.data.totalCount
                vm.communityList = res.data.list;

            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    setPageNoOne: () => {
        vm.searchData.pageNo = "1";
        vm.pagination.pageNo = vm.searchData.pageNo;
    },
    clearTagValue: () => {
        $(".liTag").removeClass("on");
        var videoArr = $(".video");
        for (var i=0; i<videoArr.length; i++) {
            videoArr[i].classList = videoArr[i].classList.value.substring(0, 9);
        }
        var posterArr = $(".poster");
        for (var i=0; i<posterArr.length; i++) {
            posterArr[i].classList = posterArr[i].classList.value.substring(0, 10);
        }
    },
}

$(document).ready(() => {
    vueInit();
    event.getCommunityList();
    event.init();
});