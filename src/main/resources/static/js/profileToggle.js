document.addEventListener('DOMContentLoaded', () => {
    // PC
    const desktopBtn = document.getElementById('notificationBtn-nav');
    const desktopDropdown = document.getElementById('notificationDropdown-nav');
    const profileBtnFull = document.getElementById('profileBtn-full');
    const dropdownMenuFull = document.getElementById('dropdownMenu-full');
  
    // Mobile
    const mobileBtn = document.getElementById('notificationBtn-mobile');
    const mobileDropdown = document.getElementById('notificationDropdown-mobile');
    const profileBtnHalf = document.getElementById('profileBtn-half');
    const dropdownMenuHalf = document.getElementById('dropdownMenu-half');
  
    // PC 종 클릭 시
    if (desktopBtn && desktopDropdown) {
      desktopBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        desktopDropdown.classList.toggle('hidden');
        if (dropdownMenuFull) dropdownMenuFull.classList.add('hidden'); // 프로필 드롭다운 닫기
      });
    }
  
    // PC 프로필 클릭 시
    if (profileBtnFull && dropdownMenuFull) {
      profileBtnFull.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdownMenuFull.classList.toggle('hidden');
        if (desktopDropdown) desktopDropdown.classList.add('hidden'); // 종 드롭다운 닫기
      });
    }
  
    // 모바일 종 클릭 시
    if (mobileBtn && mobileDropdown) {
      mobileBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        mobileDropdown.classList.toggle('hidden');
        if (dropdownMenuHalf) dropdownMenuHalf.classList.add('hidden');
      });
    }
  
    // 모바일 프로필 클릭 시
    if (profileBtnHalf && dropdownMenuHalf) {
      profileBtnHalf.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdownMenuHalf.classList.toggle('hidden');
        if (mobileDropdown) mobileDropdown.classList.add('hidden');
      });
    }
  
    // 바깥 클릭 시 모든 드롭다운 닫기
    document.addEventListener('click', (e) => {
      if (
        desktopDropdown && !desktopBtn.contains(e.target) && !desktopDropdown.contains(e.target)
      ) {
        desktopDropdown.classList.add('hidden');
      }
  
      if (
        dropdownMenuFull && !profileBtnFull.contains(e.target) && !dropdownMenuFull.contains(e.target)
      ) {
        dropdownMenuFull.classList.add('hidden');
      }
  
      if (
        mobileDropdown && !mobileBtn.contains(e.target) && !mobileDropdown.contains(e.target)
      ) {
        mobileDropdown.classList.add('hidden');
      }
  
      if (
        dropdownMenuHalf && !profileBtnHalf.contains(e.target) && !dropdownMenuHalf.contains(e.target)
      ) {
        dropdownMenuHalf.classList.add('hidden');
      }
    });
  });
  