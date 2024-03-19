 let vueData = {
    totalCount: 0,
    noticeOperateList: [],
    searchData: {
        canceledNoticeInclude: false,
        closedNoticeInclude: false,
        authorOne: "",
        searchText: '',
        pageNo: 1,
        pageLength: 10
    },
    userAuthor: "", //로그인한 사용자의 권한
    userName: "",   //로그인한 사용자의 이름
    userEmail: "",  //로그인한 사용자의 이메일
    userSeq: "",    //로그인한 사용자의 일련번호
    accessToken: "",
};

let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
let vm;
let token;

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        methods: {
            fnSearch: function (userAuthor) {
                this.searchData.pageNo = 1;
                if(userAuthor !== ''){
                    vm.searchData.authorOne = userAuthor;
                }
                event.getNoticeOperateList();
            },
            fnDetail : event.fnDetail,
           
            startNoticeFn: (noticeSeq, noticeSn) => {
                event.startNoticeFn(noticeSeq, noticeSn);
            },
            fnParticipantDetail: (noticeSeq, noticeSn) => {
            },
        }
    });
    vm = app.mount("#content");
};

let event = { 
    getNoticeOperateList : () =>{
        $.sendAjax({
            url: "/notice/selectNoticeOperateList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
                vm.totalCount = res.data.totalCount;
                vm.noticeOperateList = res.data.list;
                let len = vm.noticeOperateList.length;
 
                //강의일 포맷 맞춤
                for(let i=0; i<len; i++) {
                    let noticeDtStr = vm.noticeOperateList[i].noticeDt;
                 //   noticeDtStr = util.date.addDateDash(noticeDtStr);
                    vm.noticeOperateList[i].noticeDt = noticeDtStr;
                }

                fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
                    vm.searchData.pageNo = selectPage;
                    event.getNoticeList();
                });
            }
            ,error: function (e) {
                $.alert(e.responseJSON.message);
            }
        });
    },
    getUser: () => {
        $.sendAjax({
            url: "/notice/selectUser.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
                vm.userAuthor = res.data.userAuthor;
                vm.userName = res.data.userNm;
                vm.userEmail = res.data.userEmail;
                vm.userSeq = res.data.userSeq;
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    fnDetail : (noticeSeq, noticeSn)   => {
        location.href="noticeOperateDetail.html?noticeSeq=" + noticeSeq + "&noticeSn=" + noticeSn;
    },
    startNoticeFn: async (noticeSeq, noticeSn) => {
        let paramMap =
            {"onAirAt":"Y",
                "noticeSeq": noticeSeq,
                "noticeSn": noticeSn
            };
        await $.sendAjax({
            url: "/notice/updateNoticeFx.api",
            data: paramMap,
            contentType: "application/json",
            success: async (res) => {
                //await event.gitSiMeetSetting(noticeSeq, noticeSn);
                $("#meet").css({
                    "display": "block",
                    "position": "absolute",
                    "z-index": "1200",
                    "top": "0",
                    "left": "0",
                    "width": "100%",
                    "height": "100%"
                });
                $("#meet").show();
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    gitSiMeetSetting: async(noticeSeq, noticeSn) => {
        await fetch("https://schnode.musicen.com/generate-jwt", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                context: {
                    user: {
                        id: vm.userSeq,
                        name: vm.userName,
                        email: vm.userEmail,
                        avatar: "",
                        affiliation: "owner"
                    },
                    group: ""
                },
                aud: "jitsimeetid",
                iss: "jitsimeetid",
                sub: "*",
                room: "*"
            }),
        }).then((res) => {
            res.json().then(res=>{
                vm.token = res.token;
                const TOKEN = vm.token;
                const domain = "jit-si.musicen.com";
                const roomName = "notice-" + noticeSeq + "-" + noticeSn;
                const now = new Date().toLocaleString("ko-KR", {timeZone: "Asia/Seoul"});
                const koreanDate = new Date(now);
                const timestamp = `${koreanDate.getFullYear()}${String(koreanDate.getMonth() + 1).padStart(2,"0")}${String(koreanDate.getDate()).padStart(2, "0")}${String(koreanDate.getHours()).padStart(
                    2,
                    "0"
                )}${String(koreanDate.getMinutes()).padStart(2, "0")}`;

                const encodedRoomName = encodeURIComponent(roomName);
                const fileName = `${encodedRoomName}_${timestamp}.webm`;
                const options = {
                    jwt: TOKEN,
                    roomName: roomName,
                    height: "100%",
                    width: "100%",
                    configOverwrite: {
                        defaultLanguage: "en",
                        startWithAudioMuted: true,
                        startWithVideoMuted: false,
                        disableProfile: false,
                        prejoinPageEnabled: false,
                        disableInviteFunctions: true
                        //  maxParticipants: 5
                    },
                    interfaceConfigOverwrite: {
                        PIP_ENABLED: true
                    },

                    parentNode: document.querySelector("#meet")
                };
                const api = new window.JitsiMeetExternalAPI(domain, options);

                api.addEventListener("readyToClose", (e) => {
                    let paramMap =
                        {"onAirAt":"N",
                            "noticeSeq": noticeSeq,
                            "noticeSn": noticeSn
                        };
                    $.sendAjax({
                        url: "/notice/updateNoticeFx.api",
                        data: paramMap,
                        contentType: "application/json",
                        success: (res) => {
                            $("#meet").hide();
                        },
                        error: function (e) {
                            $.alert(e.responseJSON.message);
                        },
                    });
                });
            });
        });
    },
}

$(document).ready(() => {
    vueInit();
    util.tableSetting();
    event.getUser();
    event.getNoticeOperateList();
});
