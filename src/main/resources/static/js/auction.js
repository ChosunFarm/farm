// 기존 팝업 관련 함수들
function openInspectionPopup() {
  document.getElementById('inspectionPopup').classList.remove('hidden');
}

function closeInspectionPopup() {
  document.getElementById('inspectionPopup').classList.add('hidden');
}


document.addEventListener('DOMContentLoaded', function() {
    const categorySelect = document.getElementById('category');
    const dynamicField = document.getElementById('dynamicField');
    const dynamicLabel = document.getElementById('dynamicLabel');
    const dynamicInput = document.getElementById('dynamicInput');

    if(categorySelect) {
        categorySelect.addEventListener('change', function() {
            const selectedCategory = this.value;

            if (selectedCategory === 'F') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '과일명';
                dynamicInput.name = 'fruitName';
                dynamicInput.placeholder = '과일명을 입력하세요';
            } else if (selectedCategory === 'V') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '채소명';
                dynamicInput.name = 'vegetableName';
                dynamicInput.placeholder = '채소명을 입력하세요';
            } else if (selectedCategory === 'G') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '곡물명';
                dynamicInput.name = 'grainName';
                dynamicInput.placeholder = '곡물명을 입력하세요';
            } else {
                dynamicField.style.display = 'none';
            }
        });
    }
});

// 기존 파일 업로드 함수 (단일 이미지 업로드)
function loadFile(input) {
  const preview = document.getElementById('previewImg');
  const icon = document.getElementById('uploadIcon');
  const previewContainer = document.getElementById('previewContainer');

  previewContainer.innerHTML = '';

  const file = input.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = function(e) {
      preview.src = e.target.result;
      preview.style.display = 'block';

      if (icon) {
        icon.style.display = 'none';
      }
    };
    reader.readAsDataURL(file);
  }
}
// 이미지 파일 선택 시 미리보기 생성
function loadFiles(input) {
    const previewContainer = document.getElementById('previewContainer');
    const uploadIcon = document.getElementById('uploadIcon');

    if (input.files && input.files.length > 0) {
        uploadIcon.style.display = 'none';
        previewContainer.innerHTML = '';

        for (let i = 0; i < input.files.length; i++) {
            const file = input.files[i];
            const reader = new FileReader();

            reader.onload = function(e) {
                const previewImg = document.createElement('img');
                previewImg.src = e.target.result;
                previewImg.className = 'w-32 h-32 object-cover border rounded-lg';
                previewContainer.appendChild(previewImg);
            }

            reader.readAsDataURL(file);
        }
    } else {
        uploadIcon.style.display = 'block';
        previewContainer.innerHTML = '';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const categorySelect = document.getElementById('category');
    const dynamicField = document.getElementById('dynamicField');
    const dynamicLabel = document.getElementById('dynamicLabel');
    const dynamicInput = document.getElementById('dynamicInput');
    const fileInput = document.getElementById('file');
    const uploadIcon = document.getElementById('uploadIcon');
    const previewContainer = document.getElementById('previewContainer');

    // 카테고리 선택에 따라 추가 필드 표시
    if (categorySelect) {
        categorySelect.addEventListener('change', function() {
            const selectedCategory = this.value;

            if (selectedCategory === 'F') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '과일명';
                dynamicInput.name = 'fruitName';
                dynamicInput.placeholder = '과일 종류를 입력하세요';
            } else if (selectedCategory === 'V') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '채소명';
                dynamicInput.name = 'vegetableName';
                dynamicInput.placeholder = '채소 종류를 입력하세요';
            } else if (selectedCategory === 'G') {
                dynamicField.style.display = 'block';
                dynamicLabel.textContent = '곡물명';
                dynamicInput.name = 'grainName';
                dynamicInput.placeholder = '곡물 종류를 입력하세요';
            } else {
                dynamicField.style.display = 'none';
            }
        });
    }

    // 이미지 미리보기
    window.loadFiles = function(input) {
        if (input.files && input.files.length > 0) {
            // 아이콘 숨기기
            uploadIcon.style.display = 'none';

            // 기존 미리보기 이미지 제거
            previewContainer.innerHTML = '';

            // 각 파일에 대해 미리보기 생성
            for (let i = 0; i < input.files.length; i++) {
                const file = input.files[i];

                if (file.type.match('image.*')) {
                    const reader = new FileReader();

                    reader.onload = function(e) {
                        const imgDiv = document.createElement('div');
                        imgDiv.className = 'preview-item';

                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.className = 'h-32 w-32 object-cover rounded-md';
                        img.alt = 'preview';

                        imgDiv.appendChild(img);
                        previewContainer.appendChild(imgDiv);
                    };

                    reader.readAsDataURL(file);
                }
            }
        } else {
            // 선택된 파일이 없을 경우 기본 아이콘 표시
            uploadIcon.style.display = 'block';
            previewContainer.innerHTML = '';
        }
    };
});
/*// 여러 이미지 업로드 및 미리보기 처리
function loadFiles(input) {
  const previewContainer = document.getElementById('previewContainer');
  const icon = document.getElementById('uploadIcon');
  previewContainer.innerHTML = '';

  Array.from(input.files).forEach(file => {
    const reader = new FileReader();

    reader.onload = function (e) {
      // 이미지와 삭제 버튼을 감싸는 wrapper
      const wrapperDiv = document.createElement('div');
      wrapperDiv.classList.add('relative', 'inline-block', 'm-2');

      // 이미지 엘리먼트
      const imgElement = document.createElement('img');
      imgElement.src = e.target.result;
      imgElement.alt = '미리보기';
      imgElement.classList.add('preview-img');
      imgElement.style.maxWidth = '100%';
      imgElement.style.height = 'auto';
      imgElement.style.maxHeight = '200px';
      imgElement.style.borderRadius = '0.5rem';

      // 삭제 버튼 (X 아이콘)
      const deleteButton = document.createElement('button');
      deleteButton.innerHTML = '&times;';
      deleteButton.classList.add(
        'absolute', 'top-1', 'right-1',
        'text-white', 'bg-red-500', 'rounded-full',
        'w-4', 'h-4', 'flex', 'items-center', 'justify-center',
        'hover:bg-red-600', 'text-sm', 'font-bold', 'shadow-md'
      );
      deleteButton.onclick = () => {
        wrapperDiv.remove();
        if (previewContainer.children.length === 0 && icon) {
          icon.style.display = 'block';
        }
      };

      imgElement.onclick = () => {
        input.click(); // 이미지 클릭하면 파일 선택
      };

      // 요소 조립
      wrapperDiv.appendChild(imgElement);
      wrapperDiv.appendChild(deleteButton);
      previewContainer.appendChild(wrapperDiv);

      if (icon) {
        icon.style.display = 'none';
      }
    };

    reader.readAsDataURL(file);
  });
}*/
