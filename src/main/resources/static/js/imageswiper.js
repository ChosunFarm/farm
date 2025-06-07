document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.mySwiper').forEach((el) => {
    const nextBtn = el.querySelector('.custom-next');
    const prevBtn = el.querySelector('.custom-prev');

    new Swiper(el, {
      loop: true,
      navigation: {
        nextEl: nextBtn,
        prevEl: prevBtn
      }
    });
  });
});
