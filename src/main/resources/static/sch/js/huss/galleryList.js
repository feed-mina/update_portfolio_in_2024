var vueData = {
  commentList: [],
  filesList : [],
  spotList : [],
  filesNo : 0,
};

var vm;
var vueInit = () => {
  const app = Vue.createApp({
    data() {
      return vueData;
    },
    methods: {
      fnCancel: () => {
        $.confirm("지금까지 입력한 내용이 모두 사라집니다. \n정말 취소하시겠습니까?", () => {
          vm.filesList = [];
          vm.spotList = [];
          window.location.reload();
        })
      },
      fnSave: () => {
        let object = {
          spotList: vm.spotList,
          commentList: vm.commentList,
        }

        let formData = new FormData();

        formData.append('paramMap', new Blob([JSON.stringify(object)], {type: 'application/json'}));
        for (let i = 0; i < vm.filesList.length; i++) {
          formData.append('fileList', vm.filesList[i]);
        }

        for (var pair of formData.entries()) { }

        if (confirm('반영하시겠습니까?')) {
          $.sendAjax({
            url: "/board/upsertGallery.api",
            data: formData,
            enctype: "multipart/form-data",
            contentType: false,
            processData: false,
            cache: false,
            success: (res) => {
            console.log(res)
              $.alert(" 반영이 완료되었습니다.", () => {
             //   location.reload();
              });
            }
          })
        }
      },
    }
  })
  vm = app.mount("#content");
};
let event = {
  init: () => {
    $("input[type='file']").on('change', function (obj) {
      let spotKey = $(this).parent().data('spotkey');
      event.addFiles(obj, spotKey);
    });

    $(".contents").on('click', function (obj) {
      $("#contentsModal").modal("show");
      let spotKey = $(this).parent().data('spotkey');

      console.log(`설명등록 클릭! spotKey ==> ${spotKey}`);
      $('#spotkey').val(spotKey);
      let sj = $(this).attr('data-sj');
      let cn = $(this).attr('data-cn');

      if (sj !== undefined && cn !== undefined) {
        $('#sj').val(sj);
        $('#cn').val(cn);
      } else {
        $('#sj').val('');
        $('#cn').val('');
      }


    });

    $('#contentsModalCancel').on('click', function (obj) {
      console.log('클릭');
      $('#sj').val('');
      $('#cn').val('');
      $('#contentsModal').modal('hide');
    });

    $('#contentsModalSave').on('click', function (obj) {
      let sj = $('#sj').val();
      let cn = $('#cn').val();
      let spotKey = $('#spotkey').val();

      if (util.sjChk(sj)) {
        $.alert("제목은 100자 이내로 작성해주세요.");
        return false;
      }

      if (util.cnChk(cn)) {
        $.alert("설명은 2000자 이내로 작성해주세요.");
        return false;
      }

      let object = {
        spotKey: spotKey,
        sj: sj,
        cn: cn,
      }

      let target = $('#spotkey-' + spotKey).find('.contents');
      $(target).attr('data-sj', sj);
      $(target).attr('data-cn', cn);

      vm.commentList.push(object);
      $('#contentsModal').modal('hide');
      $('#sj').val('');
      $('#cn').val('');
    });

  },
  getGalleryList : () => {
    let param = {}

    $.sendAjax({
      url: "/board/selectGalleryList.api",
      data: param,
      contentType: "application/json",
      success : (res) => {
        let result = res.data;
        if (result.length > 0) {
          result.forEach((e) => {
            let target = $('#spotkey-' + e.spotKey).find('.contents');
            $(target).attr('data-sj', e.sj);
            $(target).attr('data-cn', e.cn);
            if (e.fileSeq !== null) {
              let fileNm = util.getLastString(e.fileCours);
              $('#spotkey-' + e.spotKey).css(
                  {
                    'background-image': 'url(/UPLOAD_FILES/' + fileNm + ')'
                    , 'background-size': '100% 100%'
                  }
              );
            }

          })
        }
      }
    })
  },

  addFiles: function (obj, spotKey) {
    const _this = this;

    $.each(obj.target.files, function (i, val) {
      // 첨부파일 검증
      if (_this.fileValidation(val)) {
        // 파일 배열에 담기
        let reader = new FileReader();
        reader.onload = function (e) {
          val.src = e.target.result;
          val.fileType = val.type.split('/')[0];
          val.spotKey = spotKey;
          $('#spotkey-' + spotKey).css(
              {
                'background-image': 'url(' + e.target.result + ')'
                , 'background-size': '100% 100%'
              }
          );
          let object = {
            spotKey: spotKey,
            fileObj: val
          }
          vm.spotList.push(spotKey);
          vm.filesList.push(val);
          //_this.imageSave(val, spotKey);

        };
        reader.readAsDataURL(val);
        vm.filesNo++;
      }
    });
  },
  imageSave: function (val, spotKey) {
    let object = {
      spotKey: spotKey,
    }

    let formData = new FormData();
    formData.append('paramMap', new Blob([JSON.stringify(object)], {type: 'application/json'}));
    vm.filesList.push(val);
    for (let i = 0; i < vm.filesList.length; i++) {
      formData.append('bbsFiles', vm.filesList[i]);
    }
    $.sendAjax({
      url: "/board/upsertGallery.api",
      data: formData,
      enctype: "multipart/form-data",
      contentType: false,
      processData: false,
      cache: false,
      success: () => {
        $.alert("공자루 반영이 완료되었습니다.", () => {
          location.reload();
        });
      }
    })
  },
  contentsUpsert: (param) => {

    $.sendAjax({
      url: "/board/usertContents.api",
      data: param,
      contentType: "application/json",
      success: () => {
        $.alert("설명 등록이 완료되었습니다.", () => {
          location.reload();
        });
      }
    })
  },
  fileValidation: function (obj) {
    const fileTypes = ['image/png', 'image/jpeg', 'image/jpg'];
    if (obj.name.length > 100) {
      $.alert("파일명이 100자 이상인 파일은 제외되었습니다.");
      return false;
    } else if (obj.size > (2 * 1024 * 1024)) {
      $.alert("최대 파일 용량인 2MB를 초과한 파일은 제외되었습니다.");
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
};


$(document).ready(() => {
  vueInit();
  event.init();
  event.getGalleryList();
});