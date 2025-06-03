document.addEventListener("DOMContentLoaded", function () {
  const editBtn = document.getElementById("editBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const viewMode = document.getElementById("viewMode");
  const editMode = document.getElementById("editMode");

  const profileImageInput = document.getElementById("profileImageInput");
  const profilePreview = document.getElementById("profilePreview");

  // 기본 이미지 경로 저장
  const defaultImage = profilePreview.dataset.default || "/images/profile.png";

  editBtn.addEventListener("click", () => {
    viewMode?.classList?.add("hidden");
    editMode.classList.remove("hidden");
  });

  cancelBtn.addEventListener("click", () => {
    editMode.classList.add("hidden");
    viewMode?.classList?.remove("hidden");

    // 프로필 미리보기 이미지 초기화
    profilePreview.src = defaultImage;

    // 파일 인풋 비우기
    profileImageInput.value = "";
  });

  profileImageInput.addEventListener("change", () => {
    const file = profileImageInput.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        profilePreview.src = e.target.result;
      };
      reader.readAsDataURL(file);
    } else {
      // 파일 선택 안 했으면 기본 이미지로
      profilePreview.src = defaultImage;
    }
  });

  // 폼 제출 시 파일이 비어있으면 기본 이미지로 미리보기 설정
  const form = document.querySelector("form");
  form?.addEventListener("submit", function () {
    const file = profileImageInput.files[0];
    if (!file) {
      profilePreview.src = defaultImage;
    }
  });
});
