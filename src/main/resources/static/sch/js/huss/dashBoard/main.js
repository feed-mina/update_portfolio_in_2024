     let noticeSeq = 'CNSLT_0001'
		let paramMap = {'noticeSeq': noticeSeq};
      
      $.sendAjax({
			type: "post",
			url: "/noticeController/selectNoticeList.api",
			data: paramMap,
			// data:JSON.stringify({searchText:'',pageNo:1,pageLEngth:10}) ,
			contentType: "application/json",
			success: (res) => {
				// vm.totalCount = res.data.totalCount;
				//vm.noticeList = res.data.list;
				
				console.log(res.data) 
			
			}
			, error: function(e) {
				$.alert(e.responseJSON);
			}
		}); 

let event = {
	init: () => { 
	}, 
	/**
	 * 
	mainQna : () => {
		$.sendAjax({
				url: "/noticeController/selectNoticeList.api", // 최근기준으로 9개만 받는 api로 수정 필요
				data:{},
				contentType: 'application/json',
				success: function (res) {
					console.log(res.data)
					console.log(res.data.totalCount)
					let totalCount = res.data.totalCount;
					let arr = []
					let swiper= {
						seq : [],
						sj : [],
						cn : [],
						date : []
					}
					for(let i=0; i<totalCount; i++){
						swiper.seq.push(res.data.list[i].noticeSeq)
						swiper.sj.push(res.data.list[i].noticeSj)
						swiper.cn.push(res.data.list[i].noticeCn)
						swiper.date.push(res.data.list[i].registDt) 
					}
					// console.log(swiper.sj)
					// console.log(swiper.cn)
					// console.log(swiper.seq)
				},
		});
	}, 
			
	 */
}
/**
 * 
$(document).ready( () => {
	event.mainQna();
});

 */
document.addEventListener("DOMContentLoaded", function () {
	const swiperContainer = document.querySelectorAll(".carousel slide")[1];
	//document.querySelectorAll(".swiper-container")는 HTML 문서에서 모든 swiper-container 클래스를 가진 요소들을 선택하고, 그중에서 두번째에 해당(인덱스[1])하는 부분을 선택
	//console.log(swiperContainer.childNodes);

	const swiperWrapper = swiperContainer.childNodes[1];
	//너비를 계산하기 위해 가져옴
	//swiperContainer.childNodes: 이 속성은 swiperContainer 요소의 모든 자식 노드를 포함하는 NodeList를 반환(텍스트 노드와 주석 노드도 포함)
	const nextButton = document.querySelector(".carousel-control-prev");
	const prevButton = document.querySelector(".carousel-control-next");
	const recommened = document.querySelector(".recommend_container");
	const contentSets = recommened.children.length;
	//이거는 슬라이드로 쓰여질 세트의 갯수

	const contentSetWidth = swiperWrapper.clientWidth; // 각 슬라이드의 너비

	let currentSet = 0;
	//초기값을 0으로 정함

	// 다음 슬라이드로 이동하는 함수
	function nextSet(e) {
		if (currentSet < contentSets - 1) {
			currentSet++;
			recommened.style.transform = `translateX(-${
				contentSetWidth * currentSet
			}px)`;
		}
	}

	// 이전 슬라이드로 이동하는 함수
	function prevSet() {
		if (currentSet > 0) {
			currentSet--;
			recommened.style.transform = `translateX(-${
				contentSetWidth * currentSet
			}px)`;
		}
	}

	// 네비게이션 버튼에 이벤트 리스너 추가
	prevButton.addEventListener("click", prevSet);
	nextButton.addEventListener("click", nextSet);
});

