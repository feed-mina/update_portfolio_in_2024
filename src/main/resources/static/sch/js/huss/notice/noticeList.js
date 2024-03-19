let vueData = {
    totalCount: 0,
    noticeList: [],
    searchData: {
        division: "F",
        tag: "",
        searchKnd: "",
        searchText: "",
        dataPerPage: "12",
        currentPage:"1",
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
        currentPage:"1",
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
 

let dataPerPage = 12;
let pagePerBar = 10;
let pageCount = 10;
let vm;
let token;

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
                event.getNoticeList();
            },
            fnDetail: event.fnDetail,
        },
            fnPaging: function (pageNo) {
                this.searchData.pageNo = pageNo;
                this.pagination.pageNo = this.searchData.pageNo;
                event.getNoticeList();
            },
    });
    vm = app.mount("#content");
};

let event = {
    getNoticeList: () => {
        $.sendAjax({
            url: "/noticeController/selectNoticeList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
                vm.totalCount = res.data.totalCount;
                vm.noticeList = res.data.list;
                vm.pagination.totalData = res.data.totalData;
                vm.pagination.totalPage = res.data.totalPage;
                vm.pagination.pageGroup = res.data.pageGroup;
                vm.pagination.last = res.data.last;
                vm.pagination.first = res.data.first;
                vm.pagination.pagingList = res.data.pagingList;

                fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
                    vm.searchData.pageNo = selectPage;
                    event.getNoticeList();
                })
            },
            error: function (e) {
                $.alert(e.responseJSON);
            },
        });
    },
    setPageNoOne: () => {
        vm.searchData.pageNo = "1";
        vm.pagination.pageNo = vm.searchData.pageNo;
    },
   
}

$(document).ready(() => {
    vueInit();
    util.tableSetting();
    event.getNoticeList();
});
