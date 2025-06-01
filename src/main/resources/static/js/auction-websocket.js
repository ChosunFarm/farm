class AuctionWebSocket {
    constructor(productId) {
      this.productId = productId;
      this.stompClient = null;
      this.connected = false;
      this.init();
    }
  
    init() {
      const socket = new SockJS('/ws');
      this.stompClient = Stomp.over(socket);
      this.connect();
    }
  
    connect() {
      this.stompClient.connect({}, frame => {
        this.connected = true;
        console.log('Connected:', frame);
  
        // 1) 전체 입찰 토픽 구독
        this.stompClient.subscribe(
          '/topic/auction/' + this.productId,
          msg => this.handleBidUpdate(JSON.parse(msg.body))
        );
  
        // 2) 개인(joined) 큐 구독
        this.stompClient.subscribe(
          '/user/queue/auction/' + this.productId,
          msg => this.handlePersonalUpdate(JSON.parse(msg.body))
        );
  
        // 3) 경매 참여 알림
        this.joinAuction();
      }, err => {
        console.warn('Connection error:', err);
        this.connected = false;
        setTimeout(() => this.connect(), 5000);
      });
    }
  
    joinAuction() {
      if (this.connected) {
        this.stompClient.send(
          '/app/auction/join', {}, 
          JSON.stringify({ productId: this.productId })
        );
      }
    }
  
    placeBid(bidAmount) {
      if (this.connected) {
        this.stompClient.send(
          '/app/bid', {}, 
          JSON.stringify({ productId: this.productId, bidAmount })
        );
      } else {
        alert('연결이 끊어졌습니다. 새로고침해주세요.');
      }
    }
  
    handleBidUpdate(bidUpdate) {
      if (bidUpdate.status === 'success') {
        // 최고가 갱신
        const priceEl = document.getElementById('price-value');
        if (priceEl) {
          priceEl.textContent = this.formatPrice(bidUpdate.bidAmount) + '원';
        }
        // **입찰자 수가 넘어왔을 때만** 갱신
        if (bidUpdate.bidCount != null) {
          const cntEl = document.getElementById('bid-count');
          if (cntEl) {
            cntEl.textContent = bidUpdate.bidCount;
          }
        }

        const inputEl = document.getElementById('bid-amount');
        if (inputEl) {
          inputEl.value = '';          
        }
  
        // 내역, 메시지, 최소입찰가
        this.addBidToHistory(bidUpdate);
        this.showMessage(bidUpdate.message, 'success');
        this.updateMinHint(bidUpdate.bidAmount + 1000);
  
      } else {
        this.showMessage(bidUpdate.message, 'error');
      }
    }
  
    handlePersonalUpdate(update) {
      if (update.status === 'joined') {
        // 초기 최고가
        if (update.bidAmount != null) {
          const priceEl = document.getElementById('price-value');
          if (priceEl) {
            priceEl.textContent = this.formatPrice(update.bidAmount) + '원';
          }
    
        }
        // 초기 입찰자 수
        if (update.bidCount != null) {
          const cntEl = document.getElementById('bid-count');
          if (cntEl) {
            cntEl.textContent = update.bidCount;
          }
        }
        // 최소입찰가 힌트
        const base = update.bidAmount ??
          Number(document.getElementById('bid-amount').min);
        this.updateMinHint(base + 1000);
      }
    }
  
    updateMinHint(nextMin) {
      const input = document.getElementById('bid-amount');
      if (!input) return;
      input.min = nextMin;
      const hint = input.closest('.bid-form')
                        .querySelector('.form-text span');
      if (hint) hint.textContent =
        new Intl.NumberFormat('ko-KR').format(nextMin) + '원';
    }
  
    addBidToHistory(bidUpdate) {
      const hist = document.getElementById('bid-history');
      if (!hist) return;
      const item = document.createElement('div');
      item.className = 'bid-item';
      item.innerHTML = `
        <div class="bid-info">
          <span class="bidder-name">${bidUpdate.bidderName}</span>
          <span class="bid-amount">${this.formatPrice(bidUpdate.bidAmount)}원</span>
          <span class="bid-time">${this.formatTime(bidUpdate.bidTime)}</span>
        </div>
      `;
      hist.insertBefore(item, hist.firstChild);
      while (hist.children.length > 10) hist.removeChild(hist.lastChild);
    }
  
    showMessage(message, type) {
      const c = document.getElementById('message-container');
      if (!c) return;
    
      const el = document.createElement('div');
    
      // Tailwind용 기본 스타일 (위치, 패딩, 그림자, rounded 등)
      const baseClasses =
        'fixed top-4 right-4 max-w-md px-4 py-2 rounded-lg shadow-md mb-2 text-sm flex items-center';
    
      // type에 따라 색상만 분기
      const colorClasses =
        type === 'success'
          ? 'bg-green-100 text-green-800'
          : 'bg-red-100 text-red-800';
    
      // 최종 클래스 합치기
      el.className = `${baseClasses} ${colorClasses}`;
    
      // 메시지 텍스트 삽입
      el.textContent = message;
    
      c.appendChild(el);
    
      // 3초 뒤 제거
      setTimeout(() => {
        if (c.contains(el)) {
          c.removeChild(el);
        }
      }, 3000);
    }
    
  
    formatPrice(p) {
      return new Intl.NumberFormat('ko-KR').format(p);
    }
    formatTime(ts) {
      const date = new Date(ts);

      const datePart = date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
  
      const timePart = date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
      return `${datePart} ${timePart}`;
      // return new Date(ts).toLocaleTimeString('ko-KR', {
      //   hour: '2-digit', minute: '2-digit', second: '2-digit'
      // });
    }
  
    disconnect() {
      if (this.stompClient && this.connected) {
        this.stompClient.disconnect();
        this.connected = false;
      }
    }
  }
  
  // 전역 노출
  let auctionWebSocket = null;
  function initializeAuctionWebSocket(productId) {
    auctionWebSocket = new AuctionWebSocket(productId);
  }
  function placeBid() {
    const inputEl = document.getElementById('bid-amount');
    const v = parseInt(document.getElementById('bid-amount').value, 10);
    if (!v || v <= 0) {
      alert('올바른 입찰가를 입력해주세요.');
      return;
    }
    if (v % 1000 !== 0) {
      alert('1,000원 단위로 입찰해주세요.');
      return;
    }

    const minAllowed = Number(inputEl.min); // inputEl.min은 문자열이므로 숫자로 변환
    if (v < minAllowed) {
      // auctionWebSocket이 생성된 상태라고 가정
      auctionWebSocket.showMessage(
        '최소 입찰금액보다 낮습니다. 다시 시도해주세요',
        'error'
      );
      return;
    }
    if (auctionWebSocket?.connected) {
      auctionWebSocket.placeBid(v);

    } else {
      alert('연결이 끊어졌습니다. 새로고침해주세요.');
    }
  }
  window.addEventListener('beforeunload', () => {
    auctionWebSocket?.disconnect();
  });
  document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('bid-button')?.addEventListener('click', placeBid);
  });
