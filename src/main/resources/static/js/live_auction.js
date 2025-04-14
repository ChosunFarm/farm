document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll('.offer-button');
    const modals = document.querySelectorAll('.modal');
    const closes = document.querySelectorAll('.close');
  
    buttons.forEach((btn, i) => {
      btn.addEventListener('click', () => {
        modals[i].style.display = 'block';
      });
    });
  
    closes.forEach((btn, i) => {
      btn.addEventListener('click', () => {
        modals[i].style.display = 'none';
      });
    });
  
    window.addEventListener('click', (e) => {
      modals.forEach((modal) => {
        if (e.target === modal) {
          modal.style.display = 'none';
        }
      });
    });
  });
  