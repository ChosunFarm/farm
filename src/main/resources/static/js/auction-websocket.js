// auction-websocket.js
class AuctionWebSocket {
    constructor(productId) {
        this.productId = productId;
        this.stompClient = null;
        this.connected = false;
        this.init();
    }

    init() {
        // SockJS와 STOMP 연결 설정
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        // 연결 시도
        this.connect();
    }

    connect() {
        const self = this;

        this.stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            self.connected = true;

            // 특정 상품의 입찰 정보 구독
            self.stompClient.subscribe('/topic/auction/' + self.productId, function(message) {
                const bidUpdate = JSON.parse(message.body);
                self.handleBidUpdate(bidUpdate);
            });

            // 개인 메시지 구독
            self.stompClient.subscribe('/user/queue/auction/' + self.productId, function(message) {
                const update = JSON.parse(message.body);
                self.handlePersonalUpdate(update);
            });

            // 경매 참여 신호 전송
            self.joinAuction();

        }, function(error) {
            console.log('Connection error: ' + error);
            self.connected = false;
            // 5초 후 재연결 시도
            setTimeout(() => {
                self.connect();
            }, 5000);
        });
    }

    // 경매 참여
    joinAuction() {
        if (this.connected && this.stompClient) {
            this.stompClient.send('/app/auction/join', {}, JSON.stringify({
                productId: this.productId
            }));
        }
    }

    // 입찰하기
    placeBid(bidAmount) {
        if (this.connected && this.stompClient) {
            this.stompClient.send('/app/bid', {}, JSON.stringify({
                productId: this.productId,
                bidAmount: bidAmount
            }));
        } else {
            alert('연결이 끊어졌습니다. 페이지를 새로고침해주세요.');
        }
    }

    // 입찰 업데이트 처리
    handleBidUpdate(bidUpdate) {
        if (bidUpdate.status === 'success') {
            // 현재 입찰가 업데이트
            const currentPriceElement = document.getElementById('current-price');
            if (currentPriceElement) {
                currentPriceElement.textContent = this.formatPrice(bidUpdate.bidAmount);
            }

            // 입찰 횟수 업데이트
            const bidCountElement = document.getElementById('bid-count');
            if (bidCountElement) {
                bidCountElement.textContent = bidUpdate.bidCount;
            }

            // 입찰 히스토리 추가
            this.addBidToHistory(bidUpdate);

            // 성공 메시지 표시
            this.showMessage(bidUpdate.message, 'success');

        } else if (bidUpdate.status === 'error') {
            // 에러 메시지 표시
            this.showMessage(bidUpdate.message, 'error');
        }
    }

    // 개인 업데이트 처리
    handlePersonalUpdate(update) {
        if (update.status === 'joined') {
            console.log('경매 참여 완료');
            // 현재 상태로 UI 업데이트
            if (update.bidAmount) {
                const currentPriceElement = document.getElementById('current-price');
                if (currentPriceElement) {
                    currentPriceElement.textContent = this.formatPrice(update.bidAmount);
                }
            }
            if (update.bidCount) {
                const bidCountElement = document.getElementById('bid-count');
                if (bidCountElement) {
                    bidCountElement.textContent = update.bidCount;
                }
            }
        }
    }

    // 입찰 히스토리에 추가
    addBidToHistory(bidUpdate) {
        const historyContainer = document.getElementById('bid-history');
        if (historyContainer) {
            const bidItem = document.createElement('div');
            bidItem.className = 'bid-item';
            bidItem.innerHTML = `
                <div class="bid-info">
                    <span class="bidder-name">${bidUpdate.bidderName}</span>
                    <span class="bid-amount">${this.formatPrice(bidUpdate.bidAmount)}원</span>
                    <span class="bid-time">${this.formatTime(bidUpdate.bidTime)}</span>
                </div>
            `;

            // 최신 입찰을 맨 위에 추가
            historyContainer.insertBefore(bidItem, historyContainer.firstChild);

            // 최대 10개까지만 표시
            while (historyContainer.children.length > 10) {
                historyContainer.removeChild(historyContainer.lastChild);
            }
        }
    }

    // 메시지 표시
    showMessage(message, type) {
        const messageContainer = document.getElementById('message-container');
        if (messageContainer) {
            const messageElement = document.createElement('div');
            messageElement.className = `alert alert-${type === 'success' ? 'success' : 'danger'}`;
            messageElement.textContent = message;

            messageContainer.appendChild(messageElement);

            // 3초 후 메시지 제거
            setTimeout(() => {
                messageContainer.removeChild(messageElement);
            }, 3000);
        }
    }

    // 가격 포맷팅
    formatPrice(price) {
        return new Intl.NumberFormat('ko-KR').format(price);
    }

    // 시간 포맷팅
    formatTime(timeString) {
        const date = new Date(timeString);
        return date.toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    // 연결 해제
    disconnect() {
        if (this.stompClient && this.connected) {
            this.stompClient.disconnect();
            this.connected = false;
            console.log('Disconnected');
        }
    }
}

// 전역 변수로 웹소켓 인스턴스 관리
let auctionWebSocket = null;

// 페이지 로드 시 웹소켓 연결
function initializeAuctionWebSocket(productId) {
    auctionWebSocket = new AuctionWebSocket(productId);
}

// 입찰 버튼 클릭 시 호출
function placeBid() {
    const bidAmountInput = document.getElementById('bid-amount');
    const bidAmount = parseInt(bidAmountInput.value);

    if (!bidAmount || bidAmount <= 0) {
        alert('올바른 입찰가를 입력해주세요.');
        return;
    }

    if (bidAmount % 1000 !== 0) {
        alert('1,000원 단위로 입찰해주세요.');
        return;
    }

    if (auctionWebSocket && auctionWebSocket.connected) {
        auctionWebSocket.placeBid(bidAmount);
        bidAmountInput.value = '';
    } else {
        alert('연결이 끊어졌습니다. 페이지를 새로고침해주세요.');
    }
}

// 페이지 언로드 시 연결 해제
window.addEventListener('beforeunload', function() {
    if (auctionWebSocket) {
        auctionWebSocket.disconnect();
    }
});