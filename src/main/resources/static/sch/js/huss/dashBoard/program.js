

var vueData = {
    isLogin: false,
    myProgrmReqst: [],

    key1: {},
    key2: {},
    key3: {},
    key4: {},
    key5: {},
}
let vm;

var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;			
		},
		methods: {
			fnReqst : (progrm, reqstYn) => {
                events.toggleProgrmReqst(progrm, reqstYn);
			},
		}
	})
	vm = app.mount('#container');
}

var events = {
	init : () => {
        const urlParam = util.getUrlParamJson();
        const tab = urlParam.tab;

        if(tab == 1){
            $(".active_back").css("left", "115px");
        }
        $("#tabs").tabs({active : tab});

        // 탭 배경
        const tabs = $(".program_tabs li");
        tabs.click(function () {
            if (this.classList.contains("tabs_right")) {
                $(".active_back").css("left", "115px");

            } else if (this.classList.contains("tabs_left")) {
                $(".active_back").css("left", "0px");
            }
        });

        const token = util.getToken();
        vm.isLogin = token != null;
    },
	selectMyProgrmReqst: () => {
        $.sendAjax({
            url: "/progrm/selectMyProgrmReqst.api",
            data: {userSeq: util.getUserSeq()},
            contentType: "application/json",
            success: async (res) => {
                console.log("🚀 ~ success: ~ res:", res);
                vm.myProgrmReqst = res.data

                vm.key1 = res.data.find(progrm => progrm.progrmKey == "key1")
                vm.key2 = res.data.find(progrm => progrm.progrmKey == "key2")
                vm.key3 = res.data.find(progrm => progrm.progrmKey == "key3")
                vm.key4 = res.data.find(progrm => progrm.progrmKey == "key4")
                vm.key5 = res.data.find(progrm => progrm.progrmKey == "key5")

                $(".pro_hide").removeClass("pro_hide");
            },
            error: function (e) {
                // $.alert(e.responseJSON.message);
            },
        })
	},
	toggleProgrmReqst: (progrm, reqstYn) => {
        const data = {
            userSeq: util.getUserSeq(),
            progrmSeq: progrm.progrmSeq,
            reqstYn: reqstYn,
        }
        $.sendAjax({
            url: "/progrm/toggleProgrmReqst.api",
            data: data,
            contentType: "application/json",
            success: async (res) => {
                events.selectMyProgrmReqst();
                if(reqstYn === "Y"){
                    alert("신청이 완료되었습니다.")
                }else{
                    alert("신청이 취소되었습니다.")
                }
            },
            error: function (e) {
                // $.alert(e.responseJSON.message);
            },
        })
	}
}


$(document).ready( () => {
	vueInit();
    events.init();

    // if only logged in
    const token = util.getToken();
    if(token){
        events.selectMyProgrmReqst();
    }
})



