document.addEventListener("DOMContentLoaded", function () {
  const editBtn = document.getElementById("editBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const viewMode = document.getElementById("viewMode");
  const editMode = document.getElementById("editMode");

  const profileImageInput = document.getElementById("profileImageInput");
  const profilePreview = document.getElementById("profilePreview");

  editBtn.addEventListener("click", () => {
    viewMode?.classList?.add("hidden");
    editMode.classList.remove("hidden");
  });

  cancelBtn.addEventListener("click", () => {
    editMode.classList.add("hidden");
    viewMode?.classList?.remove("hidden");
    profilePreview.src = "/images/default-profile.png"; // 수정 전으로 돌리기 (원래 이미지로 바꿔도 됨)
  });

  profileImageInput.addEventListener("change", () => {
    const file = profileImageInput.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        profilePreview.src = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  });
});
