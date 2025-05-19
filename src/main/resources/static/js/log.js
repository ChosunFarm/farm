document.addEventListener('DOMContentLoaded', function() {
    var imgBtn = document.querySelector('.img__btn');

    if (imgBtn) {
        imgBtn.addEventListener('click', function() {
            document.querySelector('.cont').classList.toggle('s--signup');
        });
    } else {
        console.error('img__btn element not found');
    }
});