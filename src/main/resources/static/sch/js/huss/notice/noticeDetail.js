var vueData = {
	noticeDetail :{},
	filesList : [],
	useAt : false
}

var vm;

var vueInit = () => {
	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: {
			fnUpdtMov : () => {
				location.href="noticeUpdt.html?noticeSeq=" + vm.noticeDetail.noticeSeq
			},
			fnDel : () => {
			 
				 confirm("정말 삭제하시겠습니까? 삭제한 자료는 다시 복구하지 못합니다.") 
						let param = {
						//	noticeCode : "NOTICE",
							noticeSeq : vm.noticeDetail.noticeSeq
						}
						
					//	if(util.emptyCheck(vm.noticeDetail.fileSeq) !== '' ){
					//		param.fileSeq = vm.noticeDetail.fileSeq;
					//		param.delFileSeq = vm.noticeDetail.fileSeq;
					//	}
						console.log('진입');
						$.sendAjax({
						url: "/noticeController/deleteNoticeOne.api",
						data : param,
						contentType: "application/json",
						success : () => { 
								alert("삭제가 완료되었습니다.")
								location.href="noticeList.html"	
									}			
							});	 
				 
			},
			noticeDetailOne: () => {
				const urlParams = new URL(location.href).searchParams;
				let param = {
					noticeSeq : urlParams.get('noticeSeq')
				}
				
				$.sendAjax({
					url: "/noticeController/selectNoticetestDetail.api",
					data : param,
					contentType: "application/json",
					success : (res) => {
						vm.noticeDetail = res.data;
					//	vm.useAt = util.useAt(vm.noticeDetail.registId)
						$('#noticeCn').append(res.data.noticeCn);
						if(res.data.fileList){
							vm.filesList = res.data.fileList;
							for(var i=0; i < vm.filesList.length; i++){
								var cours = vm.filesList[i].fileCours
								vm.filesList[i].fileNm = util.getLastString(cours)
								var fileNm = vm.filesList[i].orignlFileNm
								vm.filesList[i].fileType = util.chkType(fileNm)
							}
						}
					}			
				})
			},
			downloadFile : (fileSeq, fileDetailSn) =>{
				fileDownload(fileSeq, fileDetailSn);
			}
		}
	})
	vm = app.mount('#content');
}

var event = {
	
    init: () => {
        	const updateButton = document.getElementById("btnUpdateNotice");
        	const deleteButton = document.getElementById("btnDeleteNotice");
       		updateButton.style.display = "none";
        	deleteButton.style.display = "none";
    },
	
}


$(document).ready( () => {
	vueInit();
	vm.noticeDetailOne();
	event.init();
})

