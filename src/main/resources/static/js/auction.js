function openInspectionPopup() {
    document.getElementById('inspectionPopup').classList.remove('hidden');
}

function closeInspectionPopup() {
    document.getElementById('inspectionPopup').classList.add('hidden');
}
// function loadFile(input) {
//     var file = input.files[0];	//선택된 파일 가져오기

//     //미리 만들어 놓은 div에 text(파일 이름) 추가
//     var name = document.getElementById('fileName');
//     name.textContent = file.name;

//   	//새로운 이미지 div 추가
//     var newImage = document.createElement("img");
//     newImage.setAttribute("class", 'img');

//     //이미지 source 가져오기
//     newImage.src = URL.createObjectURL(file);   

//     //이미지를 image-show div에 추가
//     var container = document.getElementById('image-show');
//     container.appendChild(newImage);
// };

function loadFile(input) {
    const file = input.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function(e) {
        // 로드 완료 후 미리보기 이미지 요소에 base64 데이터 할당
        const preview = document.getElementById('previewImg');
        preview.src = e.target.result;     // base64 인코딩된 이미지
        preview.style.display = 'block';   // 숨겨져 있던 img 태그 보이기

        const icon = document.getElementById('uploadIcon');
        if (icon) {
            icon.style.display = 'none';
        }
      };
      reader.readAsDataURL(file); // 이미지 파일을 base64로 읽음
    }
}
  
  