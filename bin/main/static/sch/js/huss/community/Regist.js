let vueData = {
    lctreData: {
        startHour: "",				//강의 시작 시간
        startMinute: "",			//강의 시작 분
        endHour: "",				//강의 종료 시간
        endMinute: "",				//강의 종료 분
        startDt: "",				//강의 기간 시작일자
        endDt: "",					//강의 기간 종료일자
        startPeriodDt: "",			//모집기간 시작일자
        endPeriodDt: "",			//모집기간 종료일자
        startPeriodHour: "09",		//모집기간 시작시간
        startPeriodMinute: "00",	//모집기간 시작분
        endPeriodHour: "23",		//모집기간 종료시간
        endPeriodMinute: "59",		//모집기간 종료분
        lctreKndCode: "",			//강의 종류
        useLangCd: "KO_KR",		    //사용언어
        specialLctreYn: false,		//특강여부
        specialLctreYnStr: "",		//특강여부 리터럴값
        lctreName: "",				//강의명
        profsrName: "",				//교수명
        profsrUserSeq: "",			//교수이용자일련
        studentMax: 300, 			//최대 수강생 수
        lctreMax: 100, 				//최대 강좌수 -> 최대 100개까지 등록 가능
        lctreNum: [],				//전체 강좌 수 -> length로 카운팅
        lctreDayArray: [],			//강의요일
        lctrePlace: "공자 아카데미",	//강의장소
        userKndCodeArray: [], 		//회원 종류
        studentCount: "30", 		//수강생 수
        lctreFile: "/image/lctreSeminaDefaultImg.png",				//강의 대표 이미지
        lctreDescription: "",		//강의설명
        lctreDetailSubjectArray: [],//강좌 디테일 제목 배열
        lctreDetailOutlineArray: [],//강좌 디테일 개요 배열
        lctreKeywordArray: [],		//검색 키워드
        lctreCountArray: [],		//강좌일정
        smtmIntrprSeq: "",          //동시통역일련
    },
    authorList: [],					//commonCode에서 받아온 회원 종류 리스트
    lctreKndCodeList: [],			//commonCode에서 받아온 강의 종류 리스트
    useLangCdList: [],				//commonCode에서 받아온 사용언어 종류 리스트
    preview: "/image/lctreSeminaDefaultImg.png",
    totalUserList: [],
    userListFilteredByAuthor: [],
    userListFilteredByKeyword: [],
    userList: [],
    totalCount: 0,
    lctreList: [],
    searchData: {
        deletedLctreInclude: false,
        authorOne: "",
        searchText: '',
        pageNo: 1,
        pageLength: 10
    },
    userSearchModalType: "",
    checkedAuthorList: new Set(),
    userAuthor: "",
    userName: "",
    userEmail: "",
    userSeq: "",
}
let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
let vm;

let userSearchObj = {
    'profsr':
        {'authorType':'PR', 'userAuthorNm':'교수',},
};

let vueInit = () => {
    const app = Vue.createApp({
        data() {
            return vueData;
        },
        computed: {
            optionsWithStep() {
                const step = 10;
                const max = this.lctreData.studentMax;
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
                    {upperCmmnCode: "LCTRE_KND_CODE"},
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
                                    break;
                                case 'LCTRE_KND_CODE':
                                    vm.lctreKndCodeList = res.data;
                                    break;
                                case 'LANG_CODE':
                                    vm.useLangCdList = res.data;
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
                    let keywordTag = "<div class='keyword dp_center side-by-side'>" + e.target.value + "<button id='deleteKeyword' type='button' @click='deleteKeyword'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                    $("#keywordContainer").append(keywordTag);
                    e.target.value = "";
                }
            },
            fnAddIconClick: (e) => {
                $("#inputLctreImgFile").click();
            },
            fnCopyLctre: () => {
                event.detailModal();
            },
            fnSave: event.fnSave,
            fnCancel: function () {
                $.confirm("지금까지 입력한 내용이 모두 사라집니다.정말 취소하시겠습니까?", () => {
                    $.confirm("입력한 내용이 취소되어 목록으로 이동합니다.", () => {
                        //내용초기화
                        vm.lctreData = {};
                        vm.authorList = [];
                        vm.lctreKndCodeList = [];
                        vm.useLangCdList = [];
                        vm.preview = "/image/lctreSeminaDefaultImg.png";
                        location.href = "lctreList.html";
                    })
                })
            },
            fnDetail: event.fnDetail,
            fnModalSave: function () {
                let lctreSeq = $("input:radio[name='lctreNum']:checked").attr('id');
                let paramMap = {'lctreSeq': lctreSeq};
                $.alert("해당 항목이 복사되었습니다.", () => {
                    $.sendAjax({
                        url: "/lctreController/selectLctre.api",
                        data: paramMap,
                        contentType: "application/json",
                        success: async (res) => {
                            let resData = res.data;
                            await event.setCopyLctre(resData);
                            $.sendAjax({
                                url: "/lctreController/selectLctrePlanList.api",
                                data: paramMap,
                                contentType: "application/json",
                                success: async (lctreFxRes) => {
                                    await event.setLctreFx(lctreFxRes.data);
                                },
                                error: function (e) {
                                    $.alert(e.responseJSON.message);
                                },
                            });
                        },
                        error: function (e) {
                            $.alert(e.responseJSON.message);
                        },
                    });
                    $('#lctreCopyModal').modal('hide');
                });
            },
            fnSearch: function (userAuthor) {
                this.searchData.pageNo = 1;
                if (userAuthor !== '') {
                    vm.searchData.authorOne = userAuthor;
                }
                detailModal();
            },
            fnModalCancel: function () {
                $("input:radio[name='lctreNum']").prop('checked', false);
            },
            ellipsisText: (text) => {
                return util.formmater.textLengthOverCut(text, null, null);
            },
            fnUserModalCancel: function () {
                $("input:checkbox[name='userKndCodeInUserSearch']").prop('checked', false);
            },
            userSearch: function () {
                $("#userSearchBtn").click();
            },
        }
    })
    vm = app.mount('#content');
}

let event = {
    init: () => {
        $(document).on("click", "#deleteKeyword", function (e) {
            if (e.target.tagName === 'BUTTON') {
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                e.target.parentElement.parentElement.remove();
            }
        });
        $(document).on("click", "#profsrName", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "profsr";
            event.userSearchModal();
        });
        $(document).on("click", "#profsrNameBtn", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "profsr";
            event.userSearchModal();
        });
        $(document).on("click", "#smtmIntrpr", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "smtmIntrpr";
            event.userSearchModalDefault();
        });
        $(document).on("click", "#smtmIntrprBtn", function (e) {
            util.changeCheckedStatus(false, "userNum");
            vm.userSearchModalType = "smtmIntrpr";
            event.userSearchModalDefault();
        });
        $('#userSearchBtn').on('click', function (e) {
            if(vm.searchData.searchText ==='') {
                if (vm.userSearchModalType === "smtmIntrpr") {
                    vm.userListFilteredByAuthor = vm.totalUserList;
                    vm.userList = vm.userListFilteredByAuthor;
                } else {
                    let currentAuthorType = userSearchObj[vm.userSearchModalType].authorType;
                    vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === currentAuthorType);
                    vm.userList = vm.userListFilteredByAuthor;
                }
            } else {
                vm.userListFilteredByKeyword = vm.userListFilteredByAuthor.filter(user => user.userEmail.includes(vm.searchData.searchText) || user.userNm.includes(vm.searchData.searchText));
                vm.userList = vm.userListFilteredByKeyword;
            }
        });
        $('#userSearchInput').on('enter', function (e) {
            if(vm.searchData.searchText ==='') {
                if (vm.userSearchModalType === "smtmIntrpr") {
                    vm.userListFilteredByAuthor = vm.totalUserList;
                    vm.userList = vm.userListFilteredByAuthor;
                } else {
                    let currentAuthorType = userSearchObj[vm.userSearchModalType].authorType;
                    vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === currentAuthorType);
                    vm.userList = vm.userListFilteredByAuthor;
                }
            } else {
                vm.userListFilteredByKeyword = vm.userListFilteredByAuthor.filter(user => user.userEmail.includes(vm.searchData.searchText) || user.userNm.includes(vm.searchData.searchText));
                vm.userList = vm.userListFilteredByKeyword;
            }
        });
        $('#userSearchModal').on('shown.bs.modal', function (e) {
            vm.searchData.searchText = "";
            $("input:checkbox[name='userKndCodeInUserSearch']").prop('checked', false);
        });
        $('#userSearchModal').on('hidden.bs.modal', function (e) {
            vm.searchData.searchText = "";
            $("input:checkbox[name='userKndCodeInUserSearch']").prop('checked', false);
        });
        $('#detailLctreCount').on('change', function (e) {
            vm.lctreData.lctreNum.length = e.target.value;
            vm.lctreData.lctreDetailSubjectArray = [];
            vm.lctreData.lctreDetailOutlineArray = [];
            for (let i = 0; i < e.target.value; i++) {
                vm.lctreData.lctreDetailSubjectArray.push(' ');
                vm.lctreData.lctreDetailOutlineArray.push(' ');
            }
        });
        $("input[name='lctreDayArray']").on('change', function (e) {
            event.makeDetailLctreCountCombo();
        });
        $("input[name='lctreDayArray']").on('click', function (e) {
            event.makeDetailLctreCountCombo();
        });
        $("input[name='lctreDayArray']").on('focusout', function (e) {
            event.makeDetailLctreCountCombo();
        });
        $("input[name='lctreDayArray']").on('mouseout', function (e) {
            event.makeDetailLctreCountCombo();
        });
        $('#detailLctreCount').on('click', function (e) {
            vm.lctreData.lctreCountArray = [];
            let diffDay = util.date.getDiffDay(vm.lctreData.startDt, vm.lctreData.endDt);

            //강의기간 시작일자도 계산에 포함
            let startDtDay = util.date.getDayOfWeek(vm.lctreData.startDt);
            let startDateDayIndex = event.getDayIndex(startDtDay);
            if (vm.lctreData.lctreDayArray.includes(startDateDayIndex.toString())) {
                vm.lctreData.lctreCountArray.push(vm.lctreData.startDt);
            }

            //강의기간 중의 일자들 계산하여 강좌일정에 담기
            for (let i = 1; i <= diffDay; i++) {
                let addedDate = util.date.addDate(vm.lctreData.startDt, i);
                let addedDateDay = util.date.getDayOfWeek(addedDate);
                let addedDateDayIndex = event.getDayIndex(addedDateDay);

                if (vm.lctreData.lctreDayArray.includes(addedDateDayIndex.toString())) {
                    vm.lctreData.lctreCountArray.push(addedDate);
                }

                //강좌는 최대 100개까지 등록 가능

                if (vm.lctreData.lctreCountArray.length > 100) {
                    break;
                }
            }

            if (vm.lctreData.lctreCountArray.length < 100) {
                // 강좌일정 개수 기준으로 강좌계수 제한
                vm.lctreData.lctreMax = vm.lctreData.lctreCountArray.length;
            }
            $("#detailLctreCount").removeAttr('disabled');
            return true;
        });
        $('#inputLctreImgFile').on('change', function (obj) {
            event.addFile(obj);
        });
        /* 모집기간 datepicker는 레벨이 달라서 수동으로 minDate, maxDate설정함 */
        $(document).on("change", "#startPeriodDt", function (e) {
            $("#endPeriodDt").datepicker("option", "minDate", this.value);
            vm.lctreData.startPeriodDt = this.value;
        });
        $(document).on("change", "#endPeriodDt", function (e) {
            $("#startPeriodDt").datepicker("option", "maxDate", this.value);
            vm.lctreData.endPeriodDt = this.value;
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
                    var allUserTypeTemp = document.getElementById("ALL");
                    if (vm.checkedAuthorList.size === (vm.authorList.length - 1)) {
                        allUserTypeTemp.checked = true;
                    }
                }
            } else if (e.target.checked === false) {
                var allUserTypeTemp = document.getElementById("ALL");
                allUserTypeTemp.checked = false;
                vm.checkedAuthorList.delete(e.target.id);
            }
        });
        $(document).on("click", "#btnUserSearchModalSave", function (e) {
            if (vm.userSearchModalType === "profsr") {
                let userEmail = "";
                let userNm = "";
                let userSeqEl = $("input[name='userNum']:checked");
                let userSeq = userSeqEl[0].value;
                for (let i = 0; i < vm.userList.length; i++) {
                    if (vm.userList[i].userSeq === userSeq) {
                        userEmail = vm.userList[i].userEmail;
                        userNm = vm.userList[i].userNm;
                        break;
                    }
                }
                $(".deleteProfessorUser").parent().remove();
                vm.lctreData.profsrUserSeq = userSeq;
                let userInfoTag = "<div>" + userNm + "&nbsp;&nbsp;&nbsp;" + userEmail + "<button class='deleteProfessorUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#professorUserContainer").append(userInfoTag);
                $('#userSearchModal').modal('hide');
                return true;
            } else if (vm.userSearchModalType === "smtmIntrpr") {
                let userEmail = "";
                let userNm = "";
                let userSeqEl = $("input[name='userNum']:checked");
                let userSeq = userSeqEl[0].value ;
                for (let i = 0; i < vm.userList.length; i++) {
                    if (vm.userList[i].userSeq === userSeq) {
                        userEmail = vm.userList[i].userEmail;
                        userNm = vm.userList[i].userNm;
                        break;
                    }
                }
                $(".deleteSmtmIntrprUser").parent().remove();
                vm.lctreData.smtmIntrprSeq = userSeq;
                let userInfoTag = "<div>" + userNm + "&nbsp;&nbsp;&nbsp;" + userEmail  + "<button class='deleteSmtmIntrprUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#smtmIntrprContainer").append(userInfoTag);
                $('#userSearchModal').modal('hide');
                return true;
            }
        });
        $(document).on("click", ".deleteProfessorUser", function (e) {
            if (e.target.tagName === 'BUTTON') {
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                e.target.parentElement.parentElement.remove();
            }
            vm.lctreData.profsrUserSeq = "";
        });
        $(document).on("click", ".deleteSmtmIntrprUser", function (e) {
            if (e.target.tagName === 'BUTTON') {
                e.target.parentElement.remove();
            } else if (e.target.tagName === 'IMG') {
                e.target.parentElement.parentElement.remove();
            }
            vm.lctreData.smtmIntrprSeq = "";
        });
        $(document).on("change", "input[name=userKndCodeInUserSearch]", function (e) {
            var currentCheckedUser = e.target.id.toString().substr(5, 8);
            let userKndCodeCheck = $("input:checkbox[name='userKndCodeInUserSearch']:checked");
            if (userKndCodeCheck.length > 1) {
                var firstIdxUser = userKndCodeCheck[0].id.toString().substr(5, 8);
                if (firstIdxUser < currentCheckedUser) {
                    userKndCodeCheck[0].checked = false;
                } else {
                    userKndCodeCheck[1].checked = false;
                }
            }
        });
        $(document).on("keydown", "#lctreStartDt", function (e) {
            if (e.keyCode == 13) {
                if (!util.date.datepickerValueValidation(e.target.value)) {
                    vm.lctreData.startDt = "";
                    return;
                }
                vm.lctreData.startDt = util.date.addDateDash(e.target.value);
            }
        });
        $(document).on("keydown", "#lctreEndDt", function (e) {
            if (e.keyCode == 13) {
                if (!util.date.datepickerValueValidation(e.target.value)) {
                    vm.lctreData.endDt = "";
                    return;
                }
                vm.lctreData.endDt = util.date.addDateDash(e.target.value);
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
        $(document).on("change", "#lctreName", function (e) {
            event.makeLctreDetailSubjectDefault();
            event.makeLctreDetailOutlineDefault();
        });
        $(document).on("change", "#detailLctreCount", function (e) {
            event.makeLctreDetailSubjectDefault();
            event.makeLctreDetailOutlineDefault();
        });
    },
    getUser: () => {
        $.sendAjax({
            url: "/lctreController/selectUser.api",
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
    makeDetailLctreCountCombo: () => {
        if (vm.lctreData.startDt !== "" && vm.lctreData.endDt !== "" && vm.lctreData.lctreDayArray.length > 0) {
            $("#detailLctreCount").removeAttr("disabled");
        } else {
            $("#detailLctreCount").attr("disabled", "true");
        }
        return true;
    },
    makeLctreDetailSubjectDefault: () => {
        for(var i=0; i<vm.lctreData.lctreDetailSubjectArray.length; i++) {
            if (vm.lctreData.lctreDetailSubjectArray[i].trim() === "" || vm.lctreData.lctreDetailSubjectArray[i].trim() === ((i+1) + " 강")) {
                vm.lctreData.lctreDetailSubjectArray[i] = vm.lctreData.lctreName + " " + (i+1) + " 강";
            }
        }
    },
    makeLctreDetailOutlineDefault: () => {
        for(var i=0; i<vm.lctreData.lctreDetailOutlineArray.length; i++) {
            if (vm.lctreData.lctreDetailOutlineArray[i].trim() === "" || vm.lctreData.lctreDetailOutlineArray[i].trim() === ((i+1) + " 강")) {
                vm.lctreData.lctreDetailOutlineArray[i] = vm.lctreData.lctreName + " " + (i+1) + " 강";
            }
        }
    },
    datepickerValueValidation: (val) => {
        var regex = /^[0-9]*$/; // 숫자만 체크
        if (regex.test(val)) {
            var temp = new Date(util.date.addDateDash(val));
            if (temp.toString() === 'Invalid Date') {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
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
            url: "/lctreController/insertLctre.api",
            data: vm.lctreData,
            contentType: "application/json",
            success: (res) => {
                $.alert("강의 개설이 완료되었습니다. 강의 개설 목록으로 이동합니다.", () => {
                    location.href = "lctreList.html";
                });
            },
            error: function (e) {
                $.alert(e.responseJSON.message);
            },
        });
    },
    getDayIndex: (startDtDay) => {
        return util.date.getDayIndex(startDtDay);
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
            vm.lctreData.lctreFile = compressedFile;
        });
    },
    validation: () => {
        if (vm.lctreData.lctreKndCode === "") {
            $.alert("강의 종류를 선택하세요.");
            return false;
        } else if (vm.lctreData.useLangCd === "") {
            $.alert("사용언어를 선택해주세요.");
            return false;
        } else if (vm.lctreData.lctreName.trim() === "") {
            $.alert("강의명을 입력해주세요.");
            return false;
        } else if (vm.lctreData.profsrUserSeq === "") {
            $.alert("교수님을 선택해주세요.");
            return false;
        } else if (vm.lctreData.startDt === "") {
            $.alert("강의 기간 시작일자를 입력해주세요.");
            return false;
        } else if (vm.lctreData.endDt === "") {
            $.alert("강의 기간 종료일자를 입력해주세요.");
            return false;
        } else if (vm.lctreData.startHour.trim() === "") {
            $.alert("강의 시작 시간을 입력해주세요.");
            return false;
        } else if (vm.lctreData.startMinute.trim() === "") {
            $.alert("강의 시작 분을 입력해주세요.");
            return false;
        } else if (vm.lctreData.endHour.trim() === "") {
            $.alert("강의 종료 시간을 입력해주세요.");
            return false;
        } else if (vm.lctreData.endMinute.trim() === "") {
            $.alert("강의 종료 분을 입력해주세요.");
            return false;
        } else if (vm.lctreData.lctreDayArray.length <= 0) {
            $.alert("강의 요일을 선택하세요.");
            return false;
        } else if (vm.lctreData.startPeriodDt === "") {
            $.alert("모집기간 시작일자를 입력해주세요.");
            return false;
        } else if (vm.lctreData.startPeriodHour.trim() === "") {
            $.alert("모집기간 시작시간을 입력해주세요.");
            return false;
        } else if (vm.lctreData.startPeriodMinute.trim() === "") {
            $.alert("모집기간 시작분을 입력해주세요.");
            return false;
        } else if (vm.lctreData.endPeriodDt === "") {
            $.alert("모집기간 종료일자를 입력해주세요.");
            return false;
        } else if (vm.lctreData.endPeriodHour.trim() === "") {
            $.alert("모집기간 종료시간을 입력해주세요.");
            return false;
        } else if (vm.lctreData.endPeriodMinute.trim() === "") {
            $.alert("모집기간 종료분을 입력해주세요.");
            return false;
        } else if (vm.lctreData.userKndCodeArray.length <= 0) {
            $.alert("회원 종류를 선택해주세요.");
            return false;
        } else if (vm.lctreData.studentCount === "") {
            $.alert("수강생 수를 선택해주세요.");
            return false;
        } else if (vm.lctreData.lctreNum.length <= 0) {
            $.alert("전체 강좌 수를 선택해주세요.");
            return false;
        }

        if (vm.lctreData.startPeriodHour > 23) {
            $.alert("모집기간 시작 시간을 올바르게 입력해주세요.");
            return false;
        } else if (vm.lctreData.startPeriodMinute > 59) {
            $.alert("모집기간 시작분을 올바르게 입력해주세요.");
            return false;
        } else if (vm.lctreData.endPeriodHour > 23) {
            $.alert("모집기간 종료 시간을 올바르게 입력해주세요.");
            return false;
        } else if (vm.lctreData.endPeriodMinute > 59) {
            $.alert("모집기간 종료분을 올바르게 입력해주세요.");
            return false;
        }

        if ((vm.lctreData.startHour + vm.lctreData.startMinute) >= (vm.lctreData.endHour + vm.lctreData.endMinute)) {
            $.alert("강의 시작시간은 종료시간보다 이른 시간이어야 합니다.");
            return false;
        }

        if ((vm.lctreData.startPeriodDt === vm.lctreData.endPeriodDt) && (vm.lctreData.startPeriodHour + vm.lctreData.startPeriodMinute) >= (vm.lctreData.endPeriodHour + vm.lctreData.endPeriodMinute)) {
            $.alert("모집기간 시작일시는 종료일시보다 이른 시간이어야 합니다.");
            return false;
        }

        return true;
    },
    detailModal: () => {
        $.sendAjax({
            url: "/lctreController/selectLctreList.api",
            data: vm.searchData,
            contentType: "application/json",
            success: (res) => {
                vm.totalCount = res.data.totalCount;
                vm.lctreList = res.data.list;
                let modalLen = vm.lctreList.length;
                for (let i = 0; i < modalLen; i++) {
                    let modalLctreWeekArrayStr = vm.lctreList[i].lctreWeekArray;
                    modalLctreWeekArrayStr = modalLctreWeekArrayStr.replace("1", "월")
                        .replace("2", "화")
                        .replace("3", "수")
                        .replace("4", "목")
                        .replace("5", "금")
                        .replace("6", "토")
                        .replaceAll(",", "/");
                    vm.lctreList[i].lctreWeekArray = modalLctreWeekArrayStr;
                }
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
        vm.lctreData.lctreKeywordArray = [];
        for (let i = 0; i < keywordList.length; i++) {
            vm.lctreData.lctreKeywordArray.push(keywordList[i].innerText);
        }
        if (keywordList.length === 0) {
            vm.lctreData.lctreKeywordArray.push('강의');
        }

        //회원종류 모델에 담기
        let userKndCode = $("input[name='userKndCode']");
        vm.lctreData.userKndCodeArray = [];
        for (let i = 0; i < userKndCode.length; i++) {
            if (userKndCode[i].checked === true) {
                vm.lctreData.userKndCodeArray.push(userKndCode[i].id);
            }
        }

        //특강여부 모델에 담기
        if (vm.lctreData.specialLctreYn === true) {
            vm.lctreData.specialLctreYnStr = 'Y';
        } else {
            vm.lctreData.specialLctreYnStr = 'N';
        }

        //강의요일 정렬하기
        vm.lctreData.lctreDayArray.sort();

        //모든 회원종류가 선택됬으면 ALL값으로 회원종류 저장
        if (vm.checkedAuthorList.has("ALL")) {
            vm.checkedAuthorList = new Set();
            vm.checkedAuthorList.add("ALL");
        }
        if (vm.lctreData.userKndCodeArray.includes('ALL')) {
            vm.lctreData.userKndCodeArray = [];
            vm.lctreData.userKndCodeArray.push('ALL');
        }

        //유저타입이 교수인경우 자동으로 본인으로 교수명 선택됨
        if (vm.userAuthor === 'PR') {
            vm.lctreData.profsrUserSeq = vm.userSeq;
        }

        event.makeLctreDetailSubjectDefault();
        event.makeLctreDetailOutlineDefault();
    },
    setCopyLctre: (resData) => {
        $("#keywordContainer").children().remove();

        vm.lctreData.startHour = resData.lctreBeginTime.substring(0, 2);			//강의 시작 시간
        vm.lctreData.startMinute = resData.lctreBeginTime.substring(2, 4);			//강의 시작 분
        vm.lctreData.endHour = resData.lctreEndTime.substr(0, 2);		//강의 종료 시간
        vm.lctreData.endMinute = resData.lctreEndTime.substr(2, 2);		//강의 종료 분
        vm.lctreData.startDt = util.date.addDateDash(resData.lctreBeginDe);			//강의 기간 시작일자
        vm.lctreData.endDt = util.date.addDateDash(resData.lctreEndDe);				//강의 기간 종료일자
        vm.lctreData.startPeriodDt = resData.rcritBeginDt.substring(0, 10);			//모집기간 시작일자
        vm.lctreData.endPeriodDt = resData.rcritEndDt.substring(0, 10);				//모집기간 종료일자
        vm.lctreData.startPeriodHour = resData.rcritBeginDt.substring(11, 13);		//모집기간 시작시간
        vm.lctreData.startPeriodMinute = resData.rcritBeginDt.substring(14, 16);	//모집기간 시작분
        vm.lctreData.endPeriodHour = resData.rcritEndDt.substring(11, 13);			//모집기간 종료시간
        vm.lctreData.endPeriodMinute = resData.rcritEndDt.substring(14, 16);		//모집기간 종료분
        vm.lctreData.lctreKndCode = resData.lctreKndCode;							//강의 종류
        vm.lctreData.useLangCd = resData.useLangCd;									//사용언어

        let tempSpecialLctreYnStr = resData.speclLctreAt;							//특강여부
        if (tempSpecialLctreYnStr === 'Y') {
            vm.lctreData.specialLctreYn = true;
        } else if (tempSpecialLctreYnStr === 'N') {
            vm.lctreData.specialLctreYn = false;
        }

        vm.lctreData.lctreName = resData.lctreNm;									//강의명
        vm.lctreData.profsrUserSeq = resData.profsrUserSeq; 						//교수이용자일련

        event.makeProfessorUserTag();
        vm.lctreData.lctreNum.length = resData.detailLctreCo;						//전체 강좌 수 -> length로 카운팅
        vm.lctreData.lctreDayArray = [];
        let tempLctreWeekArray = resData.lctreWeekArray.split(",");				    //강의요일
        for (let i = 0; i < tempLctreWeekArray.length; i++) {
            let dayValue = tempLctreWeekArray[i];
            vm.lctreData.lctreDayArray.push(dayValue);
            $("input:checkbox[value=dayValue]").prop("checked", true);
        }
        vm.lctreData.lctrePlace = resData.lctrePlaceNm;								//강의장소
        let tempUserKndCodeArray = resData.atnlcAuthorArray.split(","); 	//회원 종류
        let tempStr = "";
        vm.lctreData.userKndCodeArray = [];
        for (let i = 0; i < tempUserKndCodeArray.length; i++) {
            tempStr = tempUserKndCodeArray[i];
            vm.lctreData.userKndCodeArray.push(tempStr);
            $("input[id=" + tempStr + "]").prop("checked", true);
        }
        vm.lctreData.studentCount = resData.atnlcCo; 								//수강생 수
        vm.lctreData.lctreFile = resData.lctreImageCn;								//강의 대표 이미지
        vm.preview = resData.lctreImageCn;
        vm.lctreData.lctreFile = resData.lctreImageCn;
        vm.lctreData.lctreDescription = resData.lctreDc;							//강의설명

        vm.lctreData.lctreKeywordArray = [];										//검색 키워드
        let tempKeywordArray = resData.lctreKwrd.split("&");
        for (let i = 0; i < tempKeywordArray.length; i++) {
            let keyword = tempKeywordArray[i];

            let keywordTag = "<div class='keyword dp_center side-by-side'>" + keyword + "<button id='deleteKeyword' type='button' @click='deleteKeyword'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
            $("#keywordContainer").append(keywordTag);

            vm.lctreData.lctreKeywordArray.push(keyword);
        }

        vm.lctreData.lctreCountArray = [];

        $("#detailLctreCount").removeAttr("disabled");

        $('select option[value=' + resData.detailLctreCo + ']').attr('selected', 'selected');
    },
    setLctreFx: (resData) => {
        for (let i = 0; i < resData.length; i++) {
            vm.lctreData.lctreDetailSubjectArray.push(resData[i].lctreSj);
            vm.lctreData.lctreDetailOutlineArray.push(resData[i].lctreDtls);
            vm.lctreData.lctreCountArray.push(util.date.addDateDash(resData[i].lctreDt));
        }
    },
    fnDetail: (lctreSeq) => {
        location.href = "lctreDetail.html?lctreSeq=" + lctreSeq;
    },
    userSearchModal: () => {
        let currentAuthorType = userSearchObj[vm.userSearchModalType].authorType;

        vm.userListFilteredByAuthor = vm.totalUserList.filter(user => user.userAuthor === currentAuthorType);
        vm.userList = vm.userListFilteredByAuthor;
    },
    makeProfessorUserTag() {
        $(".deleteProfessorUser").parent().remove();
        for (let i = 0; i < vm.userList.length; i++) {
            if (vm.lctreData.profsrUserSeq === vm.userList[i].userSeq) {
                let userInfoTag = "<div>" + vm.userList[i].userEmail + "&nbsp;&nbsp;&nbsp;" + vm.userList[i].userNm + "<button class='deleteProfessorUser' type='button'><img src='/image/close.png' class='delete-keyword-btn'/></button></div>";
                $("#professorUserContainer").append(userInfoTag);
                break;
            }
        }
    },
    userSearchModalDefault() {
        vm.userListFilteredByAuthor = vm.totalUserList;
        vm.userList = vm.userListFilteredByAuthor;
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
        let oneMonthLater = util.date.getDateAfterOneMonthStr();

        $("#startPeriodDt").datepicker("setDate", "today");
        $("#endPeriodDt").datepicker("setDate", oneMonthLater);

        vm.lctreData.startPeriodDt = util.date.getCurrentDateStr();
        vm.lctreData.endPeriodDt = oneMonthLater;
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
    /* 강의기간 시작일자, 종료일자는 현재 일자 이후로 선택 가능 */
    $("#lctreStartDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));
    $("#lctreEndDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));
    /* 모집기간 시작일자, 종료일자는 현재 일자 이후로 선택 가능 */
    $("#startPeriodDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));
    $("#endPeriodDt").datepicker("option", "minDate", util.date.addDateDash(util.date.getToday()));

    event.setDefaultValues();
    event.getUser();
});
