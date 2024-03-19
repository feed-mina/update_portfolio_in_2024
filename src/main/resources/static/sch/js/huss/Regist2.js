let vueData = {
    seminaData: {
        seminaSeq: "",             //세미나일련
        seminaNm: "",              //세미나명
        seminaKndCode: "",         //세미나종류코드
        useLangCd: "KO_KR",        //사용언어
        progrsUserSeq: "",         //진행이용자일련
        lctrUserSeqArray: [],      //강연이용자일련배열
        smtmIntrprSeq: "",         //동시통역일련
        seminaStyleSe: "GENERAL",  //세미나스타일구분
        seminaDe: "",              //세미나일자

        startHour: "",				//세미나 시작 시간
        startMinute: "",			//세미나 시작 분
        endHour: "",				//세미나 종료 시간
        endMinute: "",				//세미나 종료 분

        seminaPlaceNm: "공자 아카데미", //세미나장소명
        startPeriodDt: "",			//모집기간 시작일자
        endPeriodDt: "",			//모집기간 종료일자
        startPeriodHour: "09",		//모집기간 시작시간
        startPeriodMinute: "00",    //모집기간 시작분
        endPeriodHour: "23",		//모집기간 종료시간
        endPeriodMinute: "59",		//모집기간 종료분

        atnlcAuthorArray: "",      //수강권한배열 -> 회원 종류
        atnlcCo: "30",             //수강수
        seminaImageCn: "/image/lctreSeminaDefaultImg.png",         //세미나 현수막 이미지
        seminaDc: "",              //세미나설명
        seminaCn: "",              //세미나내용
        seminaSttusSe: "R",        //세미나상태구분 (R:미정,C:확정,D:취소)

        audienceMax: 300, 			//최대 신청자 수
        seminaKeywordArray: [],		//검색 키워드
    },
    authorList: [],					//commonCode에서 받아온 회원 종류 리스트
    seminaKndCodeList: [],			//commonCode에서 받아온 강의 종류 리스트
    langCodeList: [],               //commonCode에서 받아온 언어 종류 리스트
    totalUserList: [],
    userListFilteredByAuthor: [],
    userListFilteredByKeyword: [],
    userList: [],
    preview: "/image/lctreSeminaDefaultImg.png",

    totalCount: 0,
    seminaList: [],
    searchData: {
        canceledSeminaInclude: false,
        authorOne: "",
        searchText: '',
        pageNo: 1,
        pageLength: 10
    },
    lctrUserSeqCount: 0,    //강연자 명수
    lctrUserLimit: 10,      //최대 등록 가능 강연자 명수
    userSearchModalType: "",
    checkedAuthorList: new Set(),
    selectedUserSet: new Set(),
}
let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
let vm;

let userSearchObj = {
    'progrs':
        ['PR', 'SA',],
};

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        computed: {
            optionsWithStep() {
                const step = 10;
                const max = this.seminaData.audienceMax;
                const options = [];

                for (let i = 10; i <= max; i += step) {
                    options.push(i);
                }

                return options;
            }
        },
        methods: {
            hourValidation: function (e) {
                e.target.value = util.formmater.hourFormatter(e.target.value);
            },
            minuteValidation: function (e) {
                e.target.value = util.formmater.minuteFormatter(e.target.value);
            },
            selectCmmnCode: () => {
                let paramList = [
                    {upperCmmnCode: "AUTHOR", cmmnCodeEtc: "LCTRE_USER"},
                    {upperCmmnCode: "SEMINA_KND_CODE"},
                    {upperCmmnCode: "LANG_CODE"},
                ];

                for (let i = 0; i < paramList.length; i++) {
                    $.sendAjax({
                        url: "/cmmn/selectCmmnCode.api",
                        data: paramList[i],
                        contentType: "application/json",
                        success: (res) => {
                            switch (paramList[i].upperCmmnCode) {
                                case 'AUTHOR':
                                    vm.authorList = res.data;
                                    for (let i=0; i<vm.authorList.length; i++) {
                                        vm.checkedAuthorList.add(vm.authorList[i].cmmnCode);
                                    }
                                case 'SEMINA_KND_CODE':
                                    vm.seminaKndCodeList = res.data;
                                    break;
                                case 'LANG_CODE':
                                    vm.langCodeList = res.data;
                                    break;
                            }
                        },
                        error: function (e) {
                            $.alert(e.responseJSON.message);
                        },
                    });
                }
            },
            createKeyword: (e) => {
                let flag = util.validator.isSearchKeyword(e, 30);

                if (flag) {
                    let keywordTag = "<div class='keyword dp_center side-by-side'>" + e.target.value + "<button class='deleteKeyword' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                    $("#keywordContainer").append(keywordTag);
                    e.target.value = "";
                }
            },
            fnAddIconClick: (e) => {
                $("#inputSeminaImgFile").click();
            },
            fnCopySemina: () => {
                event.detailModal();
            },
            fnSave: event.fnSave,
            fnCancel: function () {
                $.confirm("지금까지 입력한 내용이 모두 사라집니다.정말 취소하시겠습니까?", () => {
                    $.confirm("입력한 내용이 취소되어 목록으로 이동합니다.", () => {
                        //내용초기화
                        vm.seminaData = {};
                        vm.authorList = [];
                        vm.seminaKndCodeList = [];
                        vm.langCodeList = [];
                        vm.preview = "/image/no_img.png";
                        location.href = "seminaList.html";
                    });
                });
            },
            fnDetail: event.fnDetail,
            fnModalSave: function () {
                let seminaSeq = $("input:radio[name='seminaNum']:checked").attr('id');
                let paramMap = {'seminaSeq': seminaSeq};
                $.alert("해당 항목이 복사되었습니다.", () => {
                    $.sendAjax({
                        url: "/seminaController/selectSemina.api",
                        data: paramMap,
                        contentType: "application/json",
                        success: async (res) => {
                            let resData = res.data;
                            await event.setCopySemina(resData);
                        },
                        error: function (e) {
                            $.alert(e.responseJSON.message);
                        },
                    });
                    $('#seminaCopyModal').modal('hide');
                });
            },
            fnSearch: function (userAuthor) {
                this.searchData.pageNo = 1;
                if (userAuthor !== '') {
                    vm.searchData.authorOne = userAuthor;
                }
                event.detailModal();
            },
            fnModalCancel: function () {
                $("input:radio[name='seminaNum']").prop('checked', false);
            },
            ellipsisText: (text) => {
                return util.formmater.textLengthOverCut(text, null, null);
            },
            fnUserModalCancel: function () {
                $("input:checkbox[name='userNum']").prop('checked', false);
            },
            userSearch: function () {
                $("#userSearchBtn").click();
            },
        },
    });
    vm = app.mount('#content');
}

let event = {
    init: () => {
        /* 모집기간 datepicker는 레벨이 달라서 수동으로 minDate, maxDate설정함 */
        $(document).on("change", "#startPeriodDt", function (e) {
            $("#endPeriodDt").datepicker("option", "minDate", this.value);
        });
        $(document).on("change", "#endPeriodDt", function (e) {
            $("#startPeriodDt").datepicker("option", "maxDate", this.value);
        });
        $(document).on("click", ".deleteKeyword", function (e) {
            if (e.target.tagName === 'BUTTON') {
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                e.target.parentElement.parentElement.remove();
            }
        });
        $(document).on("click", ".deleteUser", function (e) {
            if (e.target.tagName === 'BUTTON') {
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                e.target.parentElement.parentElement.remove();
            }
        });
        $(document).on("click", ".deleteLctrUser", function (e) {
            let lctrUserSeqSet = new Set(vm.seminaData.lctrUserSeqArray);
            let targetId = "";
            if (e.target.tagName === 'BUTTON') {
                targetId = e.target.parentElement.id;
                lctrUserSeqSet.delete(targetId);
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                targetId = e.target.parentElement.parentElement.id;
                lctrUserSeqSet.delete(targetId);
                e.target.parentElement.parentElement.remove();
            }
            vm.seminaData.lctrUserSeqArray = [...lctrUserSeqSet];
        });
        $(document).on("click", ".deleteProgrsUser", function (e) {
            vm.seminaData.progrsUserSeq = "";
        });
        $(document).on("click", ".deleteSmtmIntrprUser", function (e) {
            vm.seminaData.smtmIntrprSeq = "";
        });
        $('#inputSeminaImgFile').on('change', function (obj) {
            event.addFile(obj);
        });
        //회원종류에 전체 선택하면 다른 모든 회원종류 체크되도록 처리
        $(document).on("change", "input[id='ALL']", function (e) {
            let checkedStatus = e.target.checked;
            util.changeCheckedStatus(checkedStatus, "userKndCode");

            if (e.target.checked === true) {
                for (let i=0; i<vm.authorList.length; i++) {
                    vm.checkedAuthorList.add(vm.authorList[i].cmmnCode);
                }
            } else if (e.target.checked === false) {
                vm.checkedAuthorList.delete("ALL");
                for (let i=0; i<vm.authorList.length; i++) {
                    vm.checkedAuthorList.delete(vm.authorList[i].cmmnCode);
                }
            }
        });
        //모든 회원종류에 체크하면 전체 체크된 걸로 처리
        $(document).on("change", "input[name='userKndCode']", function (e) {
            if (e.target.checked === true) {
                vm.checkedAuthorList.add(e.target.id);
                if (e.target.id !== "ALL") {
                    let allUserTypeTemp = document.getElementById("ALL");
                    if (vm.checkedAuthorList.size === (vm.authorList.length - 1)) {
                        allUserTypeTemp.checked = true;
                    }
                }
            } else if (e.target.checked === false) {
                vm.checkedAuthorList.delete(e.target.id);
            }
        });
        $(document).on("click", "#progrsUser", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "progrs";
            event.userSearchModal();
        });
        $(document).on("click", "#progrsUserBtn", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "progrs";
            event.userSearchModal();
        });
        $(document).on("click", "#lctrUser", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.lctrUserSeqCount = vm.selectedUserSet.size;
            vm.userSearchModalType = "lctr";
            event.userSearchModalDefault(false);
        });
        $(document).on("click", "#lctrUserBtn", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.lctrUserSeqCount = vm.selectedUserSet.size;
            vm.userSearchModalType = "lctr";
            event.userSearchModalDefault(false);
        });
        $(document).on("click", "#smtmIntrpr", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "smtmIntrpr";
            event.userSearchModalDefault(false);
        });
        $(document).on("click", "#smtmIntrprBtn", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "smtmIntrpr";
            event.userSearchModalDefault(false);
        });
        $('#userSearchBtn').on('click', function (e) {
            if(vm.searchData.searchText === '') {
                if (vm.userSearchModalType === "smtmIntrpr" || vm.userSearchModalType === "lctr") {
                    vm.userListFilteredByAuthor = vm.totalUserList;
                    vm.userList = vm.userListFilteredByAuthor;
                } else {
                    let firstAuthortype = userSearchObj[vm.userSearchModalType][0];
                    let secondAuthortype = userSearchObj[vm.userSearchModalType][1];

                    vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === firstAuthortype || user.userAuthor === secondAuthortype);
                    vm.userList = vm.userListFilteredByAuthor;
                }
            } else {
                vm.userListFilteredByKeyword = vm.userListFilteredByAuthor.filter(user => user.userEmail.includes(vm.searchData.searchText) || user.userNm.includes(vm.searchData.searchText));
                vm.userList = vm.userListFilteredByKeyword;
            }
        });
        $('#userSearchInput').on('enter', function (e) {
            if(vm.searchData.searchText ==='') {
                if (vm.userSearchModalType === "smtmIntrpr" || vm.userSearchModalType === "lctr") {
                    vm.userListFilteredByAuthor = vm.totalUserList;
                    vm.userList = vm.userListFilteredByAuthor;
                } else {
                    let firstAuthortype = userSearchObj[vm.userSearchModalType][0];
                    let secondAuthortype = userSearchObj[vm.userSearchModalType][1];

                    vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === firstAuthortype || user.userAuthor === secondAuthortype);
                    vm.userList = vm.userListFilteredByAuthor;
                }
            } else {
                vm.userListFilteredByKeyword = vm.userListFilteredByAuthor.filter(user => user.userEmail.includes(vm.searchData.searchText) || user.userNm.includes(vm.searchData.searchText));
                vm.userList = vm.userListFilteredByKeyword;
            }
        });
        $('#userSearchModal').on('shown.bs.modal', function (e) {
            $("#lctrUserRegistMessage").hide();
            vm.searchData.searchText = "";
            $("input:checkbox[name='userNum']").prop('checked', false);
            if (vm.userSearchModalType === "lctr") {
                for ( let item of vm.selectedUserSet) {
                    $("input:checkbox[id='" + item + "']").prop('checked', true);
                }
            }
        });
        $('#userSearchModal').on('hidden.bs.modal', function (e) {
            vm.searchData.searchText = "";
            $("input:checkbox[name='userNum']").prop('checked', false);
        });
        $(document).on("change", "input[name=userNum]", function (e) {
            if (vm.userSearchModalType === "progrs" || vm.userSearchModalType === "smtmIntrpr") {
                var currentCheckedUser = e.target.id.toString().substr(5, 8);
                let userKndCodeCheck = $("input:checkbox[name='userNum']:checked");
                if (userKndCodeCheck.length > 1) {
                    var firstIdxUser = userKndCodeCheck[0].id.toString().substr(5, 8);
                    if (firstIdxUser < currentCheckedUser) {
                        userKndCodeCheck[0].checked = false;
                    } else {
                        userKndCodeCheck[1].checked = false;
                    }
                }
            } else if (vm.userSearchModalType === "lctr") {
                let userKndCodeCheck = $("input:checkbox[name='userNum']:checked");
                if (userKndCodeCheck.length > vm.lctrUserLimit) {
                    userKndCodeCheck[userKndCodeCheck.length-1].checked = false;
                    $("#lctrUserRegistMessage").show();
                    return true;
                } else {
                    $("#lctrUserRegistMessage").hide();
                }
            }
        });
        $(document).on("click", "#btnUserSearchModalSave", function (e) {
            if (vm.userSearchModalType === "progrs") {
                var userEmail = "";
                var userNm = "";
                var userSeqEl = $("input:checkbox[name='userNum']:checked");
                var userSeq = userSeqEl[0].id;
                for (let i = 0; i < vm.userList.length; i++) {
                    if (vm.userList[i].userSeq === userSeq) {
                        userEmail = vm.userList[i].userEmail;
                        userNm = vm.userList[i].userNm;
                        break;
                    }
                }
                $(".deleteProgrsUser").parent().remove();
                vm.seminaData.progrsUserSeq = userSeq;
                let userInfoTag = "<div>" + userNm + "&nbsp;&nbsp;&nbsp;" + userEmail + "<button class='deleteProgrsUser deleteUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#progrsUserContainer").append(userInfoTag);
            } else if (vm.userSearchModalType === "lctr") {
                $("#lctrUserContainer").children().remove();
                var userEl = $("input:checkbox[name='userNum']:checked");
                var userInfoTag = "";
                for (let i = 0; i < userEl.length; i++) {
                    for (let j = 0; j < vm.userList.length; j++) {
                        if (vm.userList[j].userSeq === userEl[i].id) {
                            vm.seminaData.lctrUserSeqArray.push(userEl[i].id);
                            vm.selectedUserSet.add(userEl[i].id);
                            let lctrUserSeqSet = new Set(vm.seminaData.lctrUserSeqArray);
                            vm.seminaData.lctrUserSeqArray = [...lctrUserSeqSet];
                            let addEl = document.getElementById(userEl[i].id);
                            if (addEl.tagName === "INPUT") {
                                userInfoTag = "<div id='" + vm.userList[j].userSeq + "' class='dp_center side-by-side lctrUser'>" + vm.userList[j].userNm + "&nbsp;&nbsp;&nbsp;" + vm.userList[j].userEmail + "<button class='deleteLctrUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                                $("#lctrUserContainer").append(userInfoTag);
                            }
                        }
                    }
                }
            } else if (vm.userSearchModalType === "smtmIntrpr") {
                var userEmail = "";
                var userNm = "";
                var userSeqEl = $("input:checkbox[name='userNum']:checked");
                var userSeq = userSeqEl[0].id;
                for (let i = 0; i < vm.userList.length; i++) {
                    if (vm.userList[i].userSeq === userSeq) {
                        userEmail = vm.userList[i].userEmail;
                        userNm = vm.userList[i].userNm;
                        break;
                    }
                }
                $(".deleteSmtmIntrprUser").parent().remove();
                vm.seminaData.smtmIntrprSeq = userSeq;
                let userInfoTag = "<div>" + userNm + "&nbsp;&nbsp;&nbsp;" + userEmail + "<button class='deleteSmtmIntrprUser deleteUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#smtmIntrprContainer").append(userInfoTag);
            }
            $('#userSearchModal').modal('hide');
            return true;
        });
        $(document).on("click", "#lctrUserContainer .deleteLctrUser", function (e) {
            vm.selectedUserSet.delete(e.target.parentElement.id);
        });
        $(document).on("click", "#lctrUserContainer .delete-keyword-btn", function (e) {
            vm.selectedUserSet.delete(e.target.parentElement.parentElement.id);
        });
        $(document).on("keydown", "#seminaDt", function (e) {
            if (e.keyCode == 13) {
                if (!util.date.datepickerValueValidation(e.target.value)) {
                    vm.seminaData.seminaDe = "";
                    return;
                }
                vm.seminaData.seminaDe = util.date.addDateDash(e.target.value);
            }
        });
        $(document).on("keydown", "#startPeriodDt", function (e) {
            if (e.keyCode == 13) {
                if (!util.date.datepickerValueValidation(e.target.value)) {
                    $("#startPeriodDt").val("");
                    return;
                }
                $("#startPeriodDt").val(util.date.addDateDash(e.target.value));
            }
        });
        $(document).on("keydown", "#endPeriodDt", function (e) {
            if (e.keyCode == 13) {
                if (!util.date.datepickerValueValidation(e.target.value)) {
                    $("#endPeriodDt").val("");
                    return;
                }
                $("#endPeriodDt").val(util.date.addDateDash(e.target.value));
            }
        });
    },
    fileValidation: function (obj) {
        const fileTypes = ['image'];
        if (obj.name.length > 100) {
            $.alert("파일명이 100자 이상인 파일은 제외되었습니다.");
            return false;
        } else if (obj.size > (100 * 1024 * 1024)) {
            $.alert("최대 파일 용량인 100MB를 초과한 파일은 제외되었습니다.");
            return false;
        } else if (obj.name.lastIndexOf('.') == -1) {
            $.alert("확장자가 없는 파일은 제외되었습니다.");
            return false;
        } else if (!fileTypes.includes(obj.type.split('/')[0])) {
            $.alert("첨부가 불가능한 파일은 제외되었습니다.");
            return false;
        } else {
            return true;
        }
    },
    fnSave: () => {
        event.dataBindingForSend();

        //필수 입력 validation
        if (!event.validation()) {
            return false;
        }

        $.sendAjax({
            url: "/seminaController/insertSemina.api",
            data: vm.seminaData,
            contentType: "application/json",
            success: (res) => {
                $.alert("세미나 개설이 완료되었습니다. 세미나 개설 목록으로 이동합니다.", () => {
                    location.href = "seminaList.html";
                });
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    addFile: async function (e) {
        // 첨부파일 검증
        $.each(e.target.files, function (i, val) {
            if (!event.fileValidation(val)) {
                return false;
            }
        });

        const compressedFile = await util.getCompressed(e.target.files[0]);
        util.blobToBase64(compressedFile).then(function (compressedFile) {
            vm.preview = compressedFile;
            vm.seminaData.seminaImageCn = compressedFile;
        });
    },
    validation: () => {
        if (vm.seminaData.seminaKndCode === "") {
            $.alert("세미나 종류를 선택해주세요.");
            return false;
        } else if (vm.seminaData.seminaNm.trim() === "") {
            $.alert("세미나명을 입력해주세요.");
            return false;
        } else if (vm.seminaData.useLangCd === "") {
            $.alert("사용 언어를 선택해주세요.");
            return false;
        } else if (vm.seminaData.progrsUserSeq === "") {
            $.alert("진행자를 선택해주세요.");
            return false;
        } else if (vm.seminaData.seminaDe.trim() === "") {
            $.alert("세미나 날짜를 입력해주세요.");
            return false;
        } else if (vm.seminaData.startHour.trim() === "") {
            $.alert("세미나 시작 시간을 입력해주세요.");
            return false;
        } else if (vm.seminaData.startMinute.trim() === "") {
            $.alert("세미나 시작 분을 입력해주세요.");
            return false;
        } else if (vm.seminaData.endHour.trim() === "") {
            $.alert("세미나 종료 시간을 입력해주세요.");
            return false;
        } else if (vm.seminaData.endMinute.trim() === "") {
            $.alert("세미나 종료 분을 입력해주세요.");
            return false;
        } else if (vm.seminaData.startPeriodDt === "") {
            $.alert("모집기간 시작일자를 입력해주세요.");
            return false;
        } else if (vm.seminaData.startPeriodHour.trim() === "") {
            $.alert("모집기간 시작시간을 입력해주세요.");
            return false;
        } else if (vm.seminaData.startPeriodMinute.trim() === "") {
            $.alert("모집기간 시작분을 입력해주세요.");
            return false;
        } else if (vm.seminaData.endPeriodDt === "") {
            $.alert("모집기간 종료일자를 입력해주세요.");
            return false;
        } else if (vm.seminaData.endPeriodHour.trim() === "") {
            $.alert("모집기간 종료시간을 입력해주세요.");
            return false;
        } else if (vm.seminaData.endPeriodMinute.trim() === "") {
            $.alert("모집기간 종료분을 입력해주세요.");
            return false;
        } else if (vm.seminaData.atnlcAuthorArray.length <= 0) {
            $.alert("회원 종류를 선택해주세요.");
            return false;
        } else if (vm.seminaData.atnlcCo === "") {
            $.alert("최대 신청자 수를 선택해주세요.");
            return false;
        }

        if ((vm.seminaData.startHour + vm.seminaData.startMinute) >= (vm.seminaData.endHour + vm.seminaData.endMinute)) {
            $.alert("세미나 시작시간은 종료시간보다 이른 시간이어야 합니다.");
            return false;
        }

        if ((vm.seminaData.startPeriodDt === vm.seminaData.endPeriodDt) && (vm.seminaData.startPeriodHour + vm.seminaData.startPeriodMinute) >= (vm.seminaData.endPeriodHour + vm.seminaData.endPeriodMinute)) {
            $.alert("모집기간 시작일시는 종료일시보다 이른 시간이어야 합니다.");
            return false;
        }

        return true;
    },
    detailModal: () => {
        $.sendAjax({
            url: "/seminaController/selectSeminaList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
                vm.totalCount = res.data.totalCount;
                vm.seminaList = res.data.list;
                fnPaging(res.data.totalCount, dataPerPage, pageCount, res.data.pageNo, (selectPage) => {
                    vm.searchData.pageNo = selectPage;
                    event.detailModal();
                });
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    dataBindingForSend: () => {
        //검색 키워드 모델에 담기
        let keywordList = $(".keyword");
        vm.seminaData.seminaKeywordArray = [];
        for (let i = 0; i < keywordList.length; i++) {
            vm.seminaData.seminaKeywordArray.push(keywordList[i].innerText);
        }
        if (keywordList.length === 0) {
            vm.seminaData.seminaKeywordArray.push('세미나');
        }

        //회원종류 모델에 담기
        let userKndCode = $("input[name='userKndCode']");
        vm.seminaData.atnlcAuthorArray = [];
        for (let i = 0; i < userKndCode.length; i++) {
            if (userKndCode[i].checked === true) {
                vm.seminaData.atnlcAuthorArray.push(userKndCode[i].id);
            }
        }

        //회원종류가 전체인 경우는 전체만 담기
        if (vm.seminaData.atnlcAuthorArray.includes('ALL')) {
            vm.seminaData.atnlcAuthorArray = [];
            vm.seminaData.atnlcAuthorArray.push('ALL');
        }
        if (vm.checkedAuthorList.has("ALL")) {
            vm.checkedAuthorList = new Set();
            vm.checkedAuthorList.add("ALL");
        }
    },
    setCopySemina: async (resData) => {
        $("#keywordContainer").children().remove();
        $("#progrsUserContainer").children().remove();
        $("#lctrUserContainer").children().remove();

        vm.seminaData.seminaNm = resData.seminaNm;                                  //세미나명
        vm.seminaData.seminaKndCode = resData.seminaKndCode;                        //세미나종류코드
        vm.seminaData.useLangCd = resData.useLangCd;                                //세미나사용언어
        vm.seminaData.progrsUserSeq = resData.progrsUserSeq;                        //진행이용자일련
        vm.seminaData.lctrUserSeqArray = resData.lctrUserSeqArray.split(",");       //강연이용자일련배열
        vm.seminaData.smtmIntrprSeq = resData.smtmIntrprSeq;                        //동시통역일련
        vm.seminaData.seminaStyleSe = resData.seminaStyleSe;                        //세미나스타일구분
        vm.seminaData.seminaDe = util.date.addDateDash(resData.seminaDe);           //세미나일자
        vm.seminaData.startHour = resData.seminaBeginTime.substring(0, 2); 	        //세미나 시작 시간
        vm.seminaData.startMinute = resData.seminaBeginTime.substring(2, 4);        //세미나 시작 분
        vm.seminaData.endHour = resData.seminaEndTime.substring(0, 2); 				//세미나 종료 시간
        vm.seminaData.endMinute = resData.seminaEndTime.substring(2, 4);            //세미나 종료 분
        vm.seminaData.seminaPlaceNm = resData.seminaPlaceNm;                        //세미나장소명
        vm.seminaData.startPeriodDt = resData.rcritBeginDt.substring(0, 10);        //모집기간 시작일자
        vm.seminaData.endPeriodDt = resData.rcritEndDt.substring(0, 10); 			//모집기간 종료일자
        vm.seminaData.startPeriodHour = resData.rcritBeginDt.substring(11, 13); 	//모집기간 시작시간
        vm.seminaData.startPeriodMinute = resData.rcritBeginDt.substring(14, 16); 	//모집기간 시작분
        vm.seminaData.endPeriodHour = resData.rcritEndDt.substring(11, 13); 		//모집기간 종료시간
        vm.seminaData.endPeriodMinute = resData.rcritEndDt.substring(14, 16); 		//모집기간 종료분
        let tempUserKndCodeArray = resData.atnlcAuthorArray.split(",");    //수강권한배열 -> 회원 종류
        let tempStr = "";
        vm.seminaData.userKndCodeArray = [];
        for (let i = 0; i < tempUserKndCodeArray.length; i++) {
            tempStr = tempUserKndCodeArray[i];
            vm.seminaData.userKndCodeArray.push(tempStr);
            $("input[id=" + tempStr + "]").prop("checked", true);
        }
        vm.seminaData.atnlcCo = resData.atnlcCo;                    //수강수
        vm.seminaData.seminaImageCn = resData.seminaImageCn;        //세미나이미지내용
        if (resData.seminaImageCn === "") {
            vm.seminaData.seminaImageCn = "/image/lctreSeminaDefaultImg.png";
        }
        vm.preview = resData.seminaImageCn;
        vm.seminaData.seminaDc = resData.seminaDc;                  //세미나설명
        vm.seminaData.seminaCn = resData.seminaCn;                  //세미나내용
        vm.seminaData.seminaKeywordArray = []; 		                //검색 키워드
        let tempKeywordArray = resData.seminaKwrd.split("&");
        for (let i = 0; i < tempKeywordArray.length; i++) {
            let keyword = tempKeywordArray[i];

            let keywordTag = "<div class='keyword dp_center side-by-side'>" + keyword + "<button class='deleteKeyword' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
            $("#keywordContainer").append(keywordTag);

            vm.seminaData.seminaKeywordArray.push(keyword);
        }
        let addTagFlag = true;
        event.userSearchModalDefault(addTagFlag);
    },
    fnDetail: (seminaSeq) => {
        location.href = "seminaDetail.html?seminaSeq=" + seminaSeq;
    },
    userSearchModal: () => {
        let firstAuthortype = userSearchObj[vm.userSearchModalType][0];
        let secondAuthortype = userSearchObj[vm.userSearchModalType][1];
        vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === firstAuthortype || user.userAuthor === secondAuthortype);
        vm.userList = vm.userListFilteredByAuthor;
    },
    userSearchModalDefault(addTagFlag) {
        vm.userListFilteredByAuthor = vm.totalUserList;
        vm.userList = vm.userListFilteredByAuthor;
        if (addTagFlag) {
            event.makeProgrsUserTag();
            event.makeLctrUserTag();
            event.makeSmtmIntrprUserTag();
        }
    },
    makeProgrsUserTag() {
        let userInfoTag = "";
        for (let i = 0; i < vm.userList.length; i++) {
            if (vm.seminaData.progrsUserSeq === vm.userList[i].userSeq) {
                userInfoTag = "<div>" +  vm.userList[i].userNm + "&nbsp;&nbsp;&nbsp;" +vm.userList[i].userEmail + "<button class='deleteProgrsUser deleteUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#progrsUserContainer").append(userInfoTag);
                break;
            }
        }
    },
    makeLctrUserTag() {
        let userInfoTag = "";
        for (let i = 0; i < vm.seminaData.lctrUserSeqArray.length; i++) {
            for (let j = 0; j < vm.userList.length; j++) {
                if (vm.seminaData.lctrUserSeqArray[i] === vm.userList[j].userSeq) {
                    userInfoTag = "<div id='" + vm.userList[j].userSeq + "' class='dp_center side-by-side lctrUser'>" + vm.userList[j].userNm + "&nbsp;&nbsp;&nbsp;" + vm.userList[j].userEmail + "<button class='deleteLctrUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                    $("#lctrUserContainer").append(userInfoTag);
                    break;
                }
            }
        }
    },
    makeSmtmIntrprUserTag() {
        let userInfoTag = "";
        for (let i = 0; i < vm.userList.length; i++) {
            if (vm.seminaData.smtmIntrprSeq === vm.userList[i].userSeq) {
                userInfoTag = "<div>" + vm.userList[i].userNm + "&nbsp;&nbsp;&nbsp;" + vm.userList[i].userEmail + "<button class='deleteSmtmIntrprUser deleteUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#smtmIntrprContainer").append(userInfoTag);
                break;
            }
        }
    },
    userSearchModalSet() {
        $.sendAjax({
            url: "/user/userInfoList.api",
            data: {},
            contentType: "application/json",
            success: (res) => {
                vm.totalUserList = res.data;
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    setDefaultValues() {
        var oneMonthLater = util.date.getDateAfterOneMonthStr();

        $("#startPeriodDt").datepicker("setDate", "today");
        $("#endPeriodDt").datepicker("setDate", oneMonthLater);

        vm.seminaData.startPeriodDt = util.date.getCurrentDateStr();
        vm.seminaData.endPeriodDt = oneMonthLater;
    },
}

$(document).ready(() => {
    vueInit();
    event.userSearchModalSet();
    event.init();
    datepicker();
    //datepicker달력에 화면 깨지는 것 설정 변경
    $.datepicker.setDefaults({
        changeMonth: false,
        changeYear: false,
    });
    vm.selectCmmnCode();
    /* 세미나일자는 현재 일자 이후로 선택 가능 */
    $("#seminaDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));
    /* 모집기간 시작일자, 종료일자는 현재 일자 이후로 선택 가능 */
    $("#startPeriodDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));
    $("#endPeriodDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));

    event.setDefaultValues();
});
