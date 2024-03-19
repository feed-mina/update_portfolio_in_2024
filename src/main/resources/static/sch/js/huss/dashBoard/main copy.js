var vueData = { 
	qnaList : []
};   

let dataPerPage = 10;
let pagePerBar = 10;
let pageCount = 10;
let vm;
let vueInit = () => {
	const app = Vue.createApp({
			data() {
					return vueData;
			},
			methods: {
			},
	});
	vm = app.mount("#content");
}
 

let event = {
	init: () => { 
		event.mainQna();
	}, 
	mainQna : () => {
		$.sendAjax({
				url: "/main/selectMainQna.api",
				data:{},
				contentType: 'application/json',
				success: function (res) {
					console.log(res.data)
					console.log(res.data.length)
					let arr = []
					for(let i=0; i<res.data.length; i++){
						console.log(res.data[i]) 
						arr.push(res.data[i])
					}
					console.log(arr)
				vm.qnaList = arr
				},
		});
	}, 
			
}

$(document).ready( () => {
	vueInit();
	//vm.initData();
	event.init();
	//event.mainNotice();
	//event.mainQna();
});
