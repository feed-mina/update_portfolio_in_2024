let vueData = {
    totalCount: 0,
    communityList: [],
    searchData: {
        division: "F",
        tag: "",
        searchKnd: "",
        searchText: "",
        dataPerPage: "12",
        pageNo: "1",
    },
    userAuthor: "", //로그인한 사용자의 권한
    userName: "",   //로그인한 사용자의 이름
    userEmail: "",  //로그인한 사용자의 이메일
    userSeq: "",    //로그인한 사용자의 일련번호
    accessToken: "",
    userId:"",      //로그인ID
    pagination: {
        division: "F",
        pageNo: "1",
        dataPerPage: "12",
        pageCount:"10",
        totalData: "",
        totalPage: "",
        pageGroup: "",
        last:"",
        first:"",
        selectedLast:"",
    },
    isActive: true,
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
                if (tag == 'video') {
                    var videoEl = $(".video");
                    for (var i= 0; i<videoEl.length; i++) {
                        if (videoEl[i].classList.value.indexOf("on") !== -1 ) {
                            videoEl[i].classList = videoEl[i].classList.value.substring(0, 5);
                            videoEl[i].parentNode.classList = videoEl[i].parentNode.classList.value.substring(0, 5);
                        } else {
                            videoEl[i].classList += " on";
                            videoEl[i].parentNode.classList += " on";
                        }
                    }
                } else if (tag == 'poster') {
                    var posterEl = $(".poster");
                    for (var i= 0; i<posterEl.length; i++) {
                        if (posterEl[i].classList.value.indexOf("on") !== -1 ) {
                            posterEl[i].classList = posterEl[i].classList.value.substring(0, 5);
                            posterEl[i].classList = posterEl[i].classList.value.substring(0, 10);
                            posterEl[i].parentNode.classList = posterEl[i].parentNode.classList.value.substring(0, 5);
                        } else {
                            posterEl[i].classList += " on";
                            posterEl[i].parentNode.classList += " on";
                        }
                    }
                }
            },
        },
    });
    vm = app.mount("#content");
};

let event = {
    init: () => {
        $(document).on("click", "#freeNtce", function (e) {
            vm.searchData.division = "F";
            vm.pagination.division = vm.searchData.division;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
        $(document).on("click", "#prizeNtce", function (e) {
            vm.searchData.division = "P";
            vm.pagination.division = vm.searchData.division;
            event.setPageNoOne();
            event.getCommunityList();
            event.clearTagValue();
        });
    },
    getCommunityList: () => {
     
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