
document.addEventListener('DOMContentLoaded', () => {
  // -- 버튼 및 드롭다운 요소
  const desktopBtn        = document.getElementById('notificationBtn-nav');
  const desktopDropdown   = document.getElementById('notificationDropdown-nav');
  const mobileBtn         = document.getElementById('notificationBtn-mobile');
  const mobileDropdown    = document.getElementById('notificationDropdown-mobile');
  const profileBtnFull    = document.getElementById('profileBtn-full');
  const dropdownMenuFull  = document.getElementById('dropdownMenu-full');
  const profileBtnHalf    = document.getElementById('profileBtn-half');
  const dropdownMenuHalf  = document.getElementById('dropdownMenu-half');
  const clearReadBtn   = document.getElementById('clearReadBtn');

  // -- 멤버 ID, 알림 요소
  const memberId         = desktopBtn.dataset.memberId;
  const desktopCountEl   = document.getElementById('alertCount');
  const mobileCountEl    = document.getElementById('alertCountMobile');
  const desktopListEl    = document.getElementById('alertList');
  const mobileListEl     = document.getElementById('alertListMobile');
  

  // -- 초기 알림 개수/미확인 리스트 로드
  fetch(`/api/alerts/count?memberId=${memberId}`)
    .then(res => res.ok ? res.text() : Promise.reject(res.status))
    .then(count => {
      desktopCountEl.textContent = count;
      mobileCountEl.textContent  = count;
    })
    .catch(err => console.error('Failed to load alert count:', err));

  fetch(`/api/alerts/unread?memberId=${memberId}`)
    .then(res => res.ok ? res.json() : Promise.reject(res.status))
    .then(list => list.reverse().forEach(alert => {
      const li = document.createElement('li');
      li.className = 'px-4 py-2 hover:bg-gray-100 text-sm text-gray-800 border-b';
      li.textContent = alert.message;
      desktopListEl.prepend(li);
      mobileListEl.prepend(li.cloneNode(true));
    }))
    .catch(err => console.error('Failed to load alerts:', err));

  // -- 뱃지(알림카운트) 초기화 함수
  function resetBadges() {
    desktopCountEl.textContent = '0';
    mobileCountEl.textContent  = '0';
  }

  // -- 알림 드롭다운 클릭 시: 전체 알림 로드 + 읽음 처리 + 배지 초기화
  if (desktopBtn && desktopDropdown) {
    desktopBtn.addEventListener('click', e => {
      e.stopPropagation();
      const isHidden = desktopDropdown.classList.toggle('hidden');
      resetBadges();
      if (dropdownMenuFull) dropdownMenuFull.classList.add('hidden');
      // 드롭다운 열리면 전체 알림 로드
      if (!isHidden) {
        desktopListEl.innerHTML = '';
        mobileListEl.innerHTML  = '';
        fetch(`/api/alerts/all?memberId=${memberId}`)
          .then(res => res.ok ? res.json() : Promise.reject(res.status))
          .then(list => list.forEach(alert => {
            const li = document.createElement('li');
            li.className = 'px-4 py-2 hover:bg-gray-100 text-sm text-gray-800 border-b';
            li.textContent = alert.message;
            desktopListEl.append(li);
            mobileListEl.append(li.cloneNode(true));
          }))
          .catch(err => console.error('Failed to load all alerts:', err));
      }
    });
  }

  if (mobileBtn && mobileDropdown) {
    mobileBtn.addEventListener('click', e => {
      e.stopPropagation();
      const isHidden = mobileDropdown.classList.toggle('hidden');
      resetBadges();
      if (dropdownMenuHalf) dropdownMenuHalf.classList.add('hidden');
      if (!isHidden) {
        desktopListEl.innerHTML = '';
        mobileListEl.innerHTML  = '';
        fetch(`/api/alerts/all?memberId=${memberId}`)
          .then(res => res.ok ? res.json() : Promise.reject(res.status))
          .then(list => list.forEach(alert => {
            const li = document.createElement('li');
            li.className = 'px-4 py-2 hover:bg-gray-100 text-sm text-gray-800 border-b';
            li.textContent = alert.message;
            mobileListEl.append(li);
            desktopListEl.append(li.cloneNode(true));
          }))
          .catch(err => console.error('Failed to load all alerts:', err));
      }
    });
  }
  if (clearReadBtn) {
    clearReadBtn.addEventListener('click', e => {
      e.stopPropagation();
      fetch(`/api/alerts/clear-read?memberId=${memberId}`, {
        method: 'DELETE'
      })
      .then(res => {
        if (!res.ok) throw new Error(res.status);
        // 화면에서 읽은 알림(현재는 전체)을 제거
        desktopListEl.innerHTML = '';
        mobileListEl.innerHTML = '';
      })
      .catch(err => console.error('Failed to clear read alerts:', err));
    });
  }
  

  // -- 프로필 드롭다운 토글 (기존 로직 유지)
  function setupToggle(btn, dropdown, otherMenu) {
    if (!btn || !dropdown) return;
    btn.addEventListener('click', e => {
      e.stopPropagation();
      dropdown.classList.toggle('hidden');
      if (otherMenu) otherMenu.classList.add('hidden');
    });
  }
  setupToggle(profileBtnFull, dropdownMenuFull, desktopDropdown);
  setupToggle(profileBtnHalf, dropdownMenuHalf, mobileDropdown);

  // -- 읽음 처리 API 호출(배지만 리셋)
  [desktopBtn, mobileBtn].forEach(btn => {
    btn.addEventListener('click', () => {
      fetch(`/api/alerts/mark-read?memberId=${memberId}`, { method: 'POST' })
        .then(() => resetBadges())
        .catch(err => console.error('Failed to mark all read:', err));
    });
  });

  // -- 바깥 클릭 시 모든 드롭다운 닫기
  document.addEventListener('click', () => {
    [desktopDropdown, mobileDropdown, dropdownMenuFull, dropdownMenuHalf]
      .forEach(drop => drop && drop.classList.add('hidden'));
  });

  // -- STOMP 연결 및 실시간 알림 처리
  let stompClient;
  function connectWebSocket() {
    const socket = new SockJS('/ws-alert');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/alerts/${memberId}`, frame => {
        const alert = JSON.parse(frame.body);
        desktopCountEl.textContent = (parseInt(desktopCountEl.textContent,10)||0) + 1;
        mobileCountEl.textContent  = (parseInt(mobileCountEl.textContent,10)||0) + 1;
        const li = document.createElement('li');
        li.className = 'px-4 py-2 hover:bg-gray-100 text-sm text-gray-800 border-b';
        li.textContent = alert.message;
        desktopListEl.prepend(li);
        mobileListEl.prepend(li.cloneNode(true));
      });
    }, () => {
      console.warn('WebSocket disconnected, retry in 5s');
      setTimeout(connectWebSocket, 5000);
    });
  }
  connectWebSocket();
});
