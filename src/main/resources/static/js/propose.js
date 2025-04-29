document.addEventListener("DOMContentLoaded", function () {
    // 가격 제안 버튼 기능
    const decreaseBtn = document.querySelector(
        ".ri-arrow-left-s-line",
    ).parentElement;
    const increaseBtn = document.querySelector(
        ".ri-arrow-right-s-line",
    ).parentElement;
    const priceDisplay = document.querySelector(
        ".text-xl.font-bold.text-primary",
    );
    let currentPrice = 14000;
    decreaseBtn.addEventListener("click", function () {
        if (currentPrice > 8000) {
        currentPrice -= 1000;
        priceDisplay.textContent = currentPrice.toLocaleString() + "원";
        }
    });
    increaseBtn.addEventListener("click", function () {
        currentPrice += 1000;
        priceDisplay.textContent = currentPrice.toLocaleString() + "원";
    });
    });