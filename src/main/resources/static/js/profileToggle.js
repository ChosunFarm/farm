document.addEventListener('DOMContentLoaded', function() {
    // PC용 드롭다운
    const profileBtnFull = document.getElementById('profileBtn-full');
    const dropdownMenuFull = document.getElementById('dropdownMenu-full');

    // 모바일용 드롭다운
    const profileBtnHalf = document.getElementById('profileBtn-half');
    const dropdownMenuHalf = document.getElementById('dropdownMenu-half');

    // PC용 버튼 클릭 시 드롭다운 토글
    if (profileBtnFull && dropdownMenuFull) {
        profileBtnFull.addEventListener('click', function(e) {
            e.stopPropagation();  // 클릭 이벤트 전파 방지
            dropdownMenuFull.classList.toggle('hidden');  // 드롭다운 보이기/숨기기
        });
    }

    // 모바일용 버튼 클릭 시 드롭다운 토글
    if (profileBtnHalf && dropdownMenuHalf) {
        profileBtnHalf.addEventListener('click', function(e) {
            e.stopPropagation();  // 클릭 이벤트 전파 방지
            dropdownMenuHalf.classList.toggle('hidden');  // 드롭다운 보이기/숨기기
        });
    }

    // 다른 곳 클릭 시 드롭다운 숨기기
    document.addEventListener('click', function(e) {
        // 프로필 버튼이나 드롭다운 메뉴가 아닌 경우 드롭다운 숨김
        if (
            (!profileBtnFull.contains(e.target) && !dropdownMenuFull.contains(e.target)) &&
            (!profileBtnHalf.contains(e.target) && !dropdownMenuHalf.contains(e.target))
        ) {
            dropdownMenuFull.classList.add('hidden');  // PC용 드롭다운 숨기기
            dropdownMenuHalf.classList.add('hidden');  // 모바일용 드롭다운 숨기기
        }
    });
});
