var vueData = {
	noticeData: {
		division: '',
		tag: 'NOTICE',
		noticeSj: '',
		useAt: '',
		noticeCn: '',
		userNm : util.getuserNm(),
		userSeq  : util.getUserSeq(), 
		registId : util.getUserSeq()
	},
	cmmnCode: [],
	filesList: [],
	noticeFiles: [],
	filesNo: 0,
	fLabel: '파일찾기'
}

var oEditors = [];
var vm;

var vueInit = () => {

	const app = Vue.createApp({
		data() {
			return vueData;
		},
		methods: { 
			fnSave: () => {
				//에디터값을 -> textarea에 저장 -> vue에 저장
			  event.editorToTextarea();
 
				if (util.sjChk(vm.noticeData.noticeSj)) {
					alert("제목은 100자 이내로 작성해주세요.");
					return false;
				} 
				
				let valiChk = event.removeHtml(vm.noticeData.noticeCn);
				if (util.cnChk(valiChk)) {
					alert("내용은 2000자 이내로 작성해주세요.");
					return false;
				}

				if (vm.noticeData.useAt === 'Y') {
					vm.noticeData.noticeSe = 'H' // 숨김
				} else if (vm.noticeData.useAt === 'N') {
					vm.noticeData.noticeSe = 'B' // 보임
				}
				var text = vm.noticeData.noticeCn;

				text = text.replace(/(?:\r\n|\r|\n)/g, '<br>');
				vm.noticeData.noticeCn = text

				let formData = new FormData();
				formData.append('paramMap', new Blob([JSON.stringify(vm.noticeData)], { type: 'application/json' }));

				for (let i = 0; i < vm.filesList.length; i++) {
					let j = $('div[name=fileSn]')[i].dataset.value
					vm.noticeFiles[i] = vm.filesList[j]
					formData.append('noticeFiles', vm.noticeFiles[i]);
				}

					console.log(formData)
				for (var pair of formData.entries()) { }

				$.sendAjax({
					url: "/noticeController/insertNotice.api",
					data: formData,
					enctype: "multipart/form-data",
					contentType: false,
					processData: false,
					cache: false,
					success: (res) => {
					console.log(res)
						 alert("등록이 완료되었습니다. 목록으로 이동합니다.", () => {
						 	location.href = "noticeList.html";
						});
					}
				})
			},
			fnCancel: () => {
				confirm("지금까지 입력한 내용이 모두 사라집니다. 정말 취소하시겠습니까?", () => {
					vm.noticeData = {};
					vm.noticeData.division = "";
					vm.noticeData.tag = "NOTICE";
					vm.noticeData.useAt = "";
					vm.filesList = [];
					oEditors.getById["noticeCn"].exec("SET_IR", [""]); //내용초기화
					alert("입력한 내용이 취소되어 목록으로 이동합니다.", () => {
						location.href = "noticeList.html"
					})
				})
			},
			delFiles: (idx) => {
				vm.filesList.splice(idx, 1);
				vm.filesNo--;
				document.getElementById("fnAddFiles").form.reset();
			},
		}
	})
	vm = app.mount('#content');
}

let event = {
	init: () => {
		$('#fnAddFiles').on('change', function(obj) {
			event.addFiles(obj);
		})

		nhn.husky.EZCreator.createInIFrame({
			oAppRef: oEditors,
			elPlaceHolder: "noticeCn",		//textarea에서 지정한 id와 일치해야 합니다.
			//SmartEditor2Skin.html 파일이 존재하는 경로
			sSkinURI: "/js/SE2/SmartEditor2Skin.html",	// Editor HTML파일 위치로 변경
			htParams: {
				// 툴바 사용 여부 (true:사용/ false:사용하지 않음)
				bUseToolbar: true,
				// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
				bUseVerticalResizer: true,
				// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
				bUseModeChanger: true,
			},
			fOnAppLoad: function() {
				oEditors.getById['noticeCn'].setDefaultFont("돋움", 12);  // 기본 글꼴 설정 추가
				//예제 코드
				//oEditors.getById["noticeCn"].exec("PASTE_HTML", ["내용을 입력해 주세요."]);
			},
			fCreator: "createSEditor2",
		})

		const columns = document.querySelectorAll(".fileList");
		columns.forEach((item) => {
			new Sortable(item, {
				group: "shared",
				animation: 150,
				ghostClass: "blue-background-class"
			});
		});
	},

	editorToTextarea: () => {
		oEditors.length && oEditors.getById["noticeCn"].exec("UPDATE_CONTENTS_FIELD", []);
		$("#noticeCn")[0].dispatchEvent(new Event("input"));
	},

	removeHtml: function(text) {
		text = text.replace(/<br>/ig, '');
		text = text.replace(/&nbsp;/ig, ''); // 공백제거
		text = text.replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, ''); // html태그 삭제
		return text;
	},
	addFiles: function(obj) {
		const _this = this;
		let maxCnt = 12; // 첨부파일 최대 갯수
		let attachFileCnt = vm.filesNo; // 기존 추가된 첨부파일 갯수
		let ableFileCnt = maxCnt - attachFileCnt; // 추가 가능한 첨부파일 갯수
		let currentFileCnt = obj.target.files.length; // 현재 선택된 첨부파일 갯수

		//첨부파일 갯수 확인
		if (currentFileCnt > ableFileCnt) {
			alert('첨부파일은 최대 12개까지만 가능합니다.');
		} else {
			$.each(obj.target.files, function(i, val) {
				// 첨부파일 검증
				if (_this.fileValidation(val)) {
					// 파일 배열에 담기
					let reader = new FileReader();
					reader.onload = function(e) {
						val.src = e.target.result
						val.fileType = val.type.split('/')[0]
						vm.filesList.push(val);
					};
					reader.readAsDataURL(val);
					vm.filesNo++;
				} else {
					$("#fnAddFiles").val("")
				}
			});
		}
	},
	fileValidation: function(obj) {
		const fileTypes = ['image/jpeg', 'image/jpg', 'image/png', 'video/mp4'];
		if (obj.name.length > 100) {
			$.alert("파일명이 100자 이상인 파일은 제외되었습니다.");
			return false;
		} else if (obj.size > (200 * 1024 * 1024)) {
			$.alert("최대 파일 용량인 200MB를 초과한 파일은 제외되었습니다.");
			return false;
		} else if (obj.name.lastIndexOf('.') == -1) {
			$.alert("확장자가 없는 파일은 제외되었습니다.");
			return false;
		} else if (!fileTypes.includes(obj.type)) {
			$.alert("등록할 수 없는 파일타입 입니다.");
			return false;
		} else {
			return true;
		}
	}
}

$(document).ready(() => {
	vueInit();
	event.init(); 
})