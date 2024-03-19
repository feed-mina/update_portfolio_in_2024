document.addEventListener("DOMContentLoaded", function () {
  // Custom Slider Functionality
  function initSlider() {
    const sliderContainer = document.querySelector(".swiper-container");
    const sliderWrapper = sliderContainer.querySelector(
      ".swiper-wrapper-banner"
    );
    const slides = sliderWrapper.querySelectorAll(".swiper-slide");
    //  querySelector(일치하는 값이 있는 첫번째 element를 반환=> 일치하는 값이 여러개 이면 첫번째 element값을 반환하고 종료/없으면 null)
    // vs querySelectorAll(일치하는 element list(=nodelist)반환/없으면 비어있는 nodelist반환 => 따라서 함수 실행시 배열을 실행시키듯 해야함)

    const prevBtn = sliderContainer.querySelector(".swiper-button-prev");
    const nextBtn = sliderContainer.querySelector(".swiper-button-next");
    const pagination = sliderContainer.querySelector(".swiper-pagination");

    //slides 값은 querySelectorAll 즉, 배열로 저장되어 있음 배열의 길이(요소 갯수)를 slideCount변수에 넣음
    const slideCount = slides.length;

    //실제로 보여지고 있는 컨텐츠가 차지하는 공간을 확인하고자 clientWidth(clientHeight)를 사용: 보더와 스크롤바의 크기를 제외한 실제 컨텐츠 크기(패팅은 포함)
    // 즉, 슬라이드 0번째에 있는 컨텐츠의 크기를 확인하고자 size변수에 넣음
    const size = slides[0].clientWidth;

    //해당 참조사이트에서 swiper는 초기 화면이 총 인덱스[0], 인덱스[1], 인덱스[2] 3가지고 노출되고 있으며 이 중 메인컨텐츠는 인덱스[1]이 됨
    //currentIndex = 1로 초깃값을 설정해두면 해당 인덱스 컨텐츠가 메인 컨텐츠가 되며 또한, 마지막 인덱스에 도달 했을때 해당 컨텐츠로 초기화하는 무한 루프가 만들어짐
    //이 경우 마지막 슬라이드 컨텐츠에 도달했을 때 transitioned 이벤트 리스너가 트리거 됨
    let currentIndex = 1;

    // let offset = 0;
    function updateSliderPosition() {
      sliderWrapper.style.transform = `translateX(${
        -size * currentIndex + 190
      }px)`;
    }

    function updatePagination() {
      pagination
        .querySelectorAll(".swiper-pagination-bullet")
        .forEach((bullet, index) => {
          bullet.classList.toggle("active", index === currentIndex);
        });
    }

    //제공된 인덱스를 기준으로 슬라이드 이동시키는 함수
    function goToSlide(index) {
      //sliderWrapper의 전환 속성
      sliderWrapper.style.transition = "0.3s ease-in-out";
      //인덱스가 범위를 벗어 나는지 확인
      // 1. 0보다 작은지 / 2. 전체인덱스의 숫자보다 크거나 같은지
      // 해당 조건을 벗어나면 currentIndex를 통해 노출 슬라이드를 조정
      if (index < 0) {
        currentIndex = slideCount - 1;
      } else if (index >= slideCount) {
        currentIndex = 0;
      } else {
        currentIndex = index;
      }
      //노출되는 슬라이드가 설정됐으면, (위 조건문을 통해)
      //슬라이드 배치되는 위치를 조정하는 함수를 호출
      updateSliderPosition();
      //페이지네이션 버튼(bullet)조정
      updatePagination();
    }

    // 브라우저 크기가 조정될 때마다 트리거되는 이벤트 리스너, resize
    // 슬라이더의 위치가 다시 계산되고 새창 크기에 맞게 업데이트 되도록 하기위한 기능
    window.addEventListener("resize", function () {
      //슬라이드 너비 가져옴 offsetWidth(컨텐츠, 패팅, 테두리 등 요소의 너비)를 사용
      slideWidth = slides[0].offsetWidth;
      //업데이트 된 slideWidth 값을 확인하고 아래 함수를 호출해서 새너비를 기준으로 슬라이드 위치를 변경
      updateSliderPosition();
    });

    //슬라이드 무한루프를 위해
    //마지막 슬라이드에 도달했을때, transitioned 이벤트 리스너가 트리거 됨
    sliderWrapper.addEventListener("transitionend", () => {
      // currentIndex가 마지막일 인덱스일 경우(slides.length-1)
      if (currentIndex === slides.length - 1) {
        //첫번째 인덱스로 돌아감
        currentIndex = slides.length - (slides.length - 1);
        //슬라이더가 마지막 슬라이드에서 첫번째 슬라이드로 부드럽게 전환하기 위함
        sliderWrapper.style.transition = "0s";
        //무한 슬라이드에서 초기 슬라이드 값인 인덱스[1]의 위치로 재설정
        //translateX는 가로이동시 사용
        //-size * currentIndex는 현재 슬라이드의 위치이며, currentIndex[1]의 값이 190px 오프셋 위치에서 시작하기 때문임
        sliderWrapper.style.transform = `translateX(${
          -size * currentIndex + 190
        }px)`;
      }
      // 첫번째 인덱스일 경우
      if (currentIndex === 0) {
        // 마지막 슬라이드 이전 슬라이드
        currentIndex = slides.length - 2;
        sliderWrapper.style.transition = "0s";
        sliderWrapper.style.transform = `translateX(${
          -size * currentIndex + 190
        }px)`;
      }
    });

    // Initialize slider position
    updateSliderPosition();
    updatePagination();

    // Event listeners for navigation buttons
    prevBtn.addEventListener("click", () => goToSlide(currentIndex + 1));
    nextBtn.addEventListener("click", () => goToSlide(currentIndex - 1));

    // Event listeners for pagination bullets
    pagination
      .querySelectorAll(".swiper-pagination-bullet")
      .forEach((bullet, index) => {
        bullet.addEventListener("click", () => goToSlide(index));
      });
  }

  // Initialize the custom slider
  initSlider();
});
