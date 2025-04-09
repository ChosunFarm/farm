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
  
// document.addEventListener('DOMContentLoaded', () => {
//   const menuBtn = document.getElementById('mobile-menu-button');
//   const mobileMenu = document.getElementById('mobile-menu');
//   if(menuBtn && mobileMenu) {
//     menuBtn.addEventListener('click', () => {
//       mobileMenu.classList.toggle('hidden');
//     });
//   }
// });
  