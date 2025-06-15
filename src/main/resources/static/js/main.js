// script.js

// Glide 슬라이더 초기화
new Glide('.glide', {
    type: 'carousel',
    startAt: 0,
    perView: 1,
    autoplay: 5000,
    hoverpause: true,
    gap: 0,
    animationDuration: 1000,
    animationTimingFunc: 'ease-in-out',
    breakpoints: {
      800: { perView: 1 }
    }
  }).mount();
  
  document.addEventListener('DOMContentLoaded', () => {
  const menuBtn = document.getElementById('mobile-menu-button');
  const mobileMenu = document.getElementById('mobile-menu');
  if (menuBtn && mobileMenu) {
    menuBtn.addEventListener('click', () => {
      mobileMenu.classList.toggle('hidden');
    });
  }

  // 스크롤 애니메이션 (Intersection Observer)
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('opacity-100', 'translate-y-0');
        entry.target.classList.remove('opacity-0', 'translate-y-10');
      } else {
        entry.target.classList.remove('opacity-100', 'translate-y-0');
        entry.target.classList.add('opacity-0', 'translate-y-10');
      }
    });
  }, {
    threshold: 0.3 // 뷰포트에 30% 들어오면
  });

  document.querySelectorAll('.scroll-section').forEach(section => {
    observer.observe(section);
  });
});
  
// document.addEventListener('DOMContentLoaded', () => {
//   const menuBtn = document.getElementById('mobile-menu-button');
//   const mobileMenu = document.getElementById('mobile-menu');
//   if(menuBtn && mobileMenu) {
//     menuBtn.addEventListener('click', () => {
//       mobileMenu.classList.toggle('hidden');
//     });
//   }
// });
  