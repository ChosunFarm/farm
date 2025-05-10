document.addEventListener('DOMContentLoaded', function() {
    // 성공/오류 메시지 타임아웃 설정
    const message = document.querySelector('[data-message]');
    if (message) {
        setTimeout(function() {
            message.style.opacity = '0';
            setTimeout(function() {
                message.style.display = 'none';
            }, 500);
        }, 5000);
    }

    // 승인/거부 액션 확인 다이얼로그
    const approveButtons = document.querySelectorAll('[data-action="approve"]');
    const rejectButtons = document.querySelectorAll('[data-action="reject"]');

    approveButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('이 상품의 검수를 승인하시겠습니까?')) {
                e.preventDefault();
            }
        });
    });

    rejectButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('이 상품의 검수를 거부하시겠습니까?')) {
                e.preventDefault();
            }
        });
    });
});