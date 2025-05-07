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

  categorySelect.addEventListener('change', function() {
      const selectedValue = this.value;
      
      switch (selectedValue) {
          case 'F':
              dynamicField.style.display = 'block';
              dynamicLabel.textContent = '과일명';
              dynamicInput.placeholder = '과일명을 입력하세요';
              dynamicInput.name = 'fruitName';
              break;
          case 'V':
              dynamicField.style.display = 'block';
              dynamicLabel.textContent = '채소명';
              dynamicInput.placeholder = '채소명을 입력하세요';
              dynamicInput.name = 'vegetableName';
              break;
          case 'G':
              dynamicField.style.display = 'block';
              dynamicLabel.textContent = '곡물명';
              dynamicInput.placeholder = '곡물명을 입력하세요';
              dynamicInput.name = 'grainName';
              break;
          default:
              dynamicField.style.display = 'none';
              dynamicLabel.textContent = '';
              dynamicInput.placeholder = '';
              dynamicInput.name = '';
              break;
      }
  });
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

// 여러 이미지 업로드 및 미리보기 처리
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
}
