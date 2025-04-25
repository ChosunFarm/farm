document.addEventListener("DOMContentLoaded", function () {
    const uploadBtn = document.querySelector('.upload-btn');
    uploadBtn.addEventListener('click', () => {
        alert('사진 변경 기능은 추후 지원됩니다!');
    });

    const bioBtn = document.querySelector('.bio button');
    bioBtn.addEventListener('click', () => {
        const introText = document.querySelector('.bio textarea').value;
        alert(`한줄소개가 변경되었습니다: ${introText}`);
    });
});
