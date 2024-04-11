let vueData = {
    isLogin: false,
    isControllable: false, // 수정 or 삭제권한 유무
    communitySeq: util.getUrlParamJson().communitySeq,
    community: {},
};

let vm;

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        methods: {
            util: () => util,
     
            fnDelete: function(){
                $.sendAjax({
                    url: "/community/deleteCommunity.api",
                    data: {
                        communitySeq: vm.communitySeq,
                        useAt: "N",
                    },
                    contentType: "application/json",
                    success: (res) => {
                        alert("삭제 되었습니다")
                        location.href = "communityList.html";
                    },
                    error: function (e) {
                        alert("문제가 발생했습니다")
                        location.href = "communityList.html";
                        // $.alert(e.responseJSON.message);
                    }
                })
            },
            fnUpdate: function(){
                location.href = "communityInsert.html?communitySeq=" + vm.communitySeq
            },
        },
    });

    vm = app.mount("#container");
};

let events = {
    init: () => {
        const token = util.getToken();
        vm.isLogin = token != null;
    },
    getCommunity: () => {
         $.sendAjax({
            url: "/community/selectCommunity.api",
            data: {communitySeq: vm.communitySeq},
            contentType: "application/json",
            success: (res) => {
                vm.community = res.data;

                // 로그인 되어있고, (관리자거나, 등록자가 "나" 인경우)
                vm.isControllable = 
                    vm.isLogin &&
                    (
                        util.getStorage("userAuthor") == "SA" ||
                        vm.community.registId == util.getStorage("userSeq") 
                    )
            },
            error: function (e) {
                // $.alert(e.responseJSON.message);
            },
        });
    },
}

$(document).ready(() => {
    vueInit();
    events.init();
    events.getCommunity();
});
