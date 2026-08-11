/**
 * 应用外壳：左侧导航切换、顶栏时钟
 */
(function () {
    const PANELS = {
        query: 'queryPanel',
        conditions: 'conditionsPanel'
    };

    /** 切换主面板（同时更新侧边栏选中态） */
    window.switchPanel = function (name) {
        const target = PANELS[name] || PANELS.query;
        Object.keys(PANELS).forEach(function (key) {
            document.getElementById(PANELS[key]).style.display = key === name ? '' : 'none';
        });
        document.querySelectorAll('.side-menu-item').forEach(function (el) {
            el.classList.toggle('active', el.getAttribute('data-panel') === name);
        });
        if (window.onPanelShow) {
            window.onPanelShow(name);
        }
    };

    /** 顶栏时钟 */
    function renderClock() {
        const el = document.getElementById('headerClock');
        if (!el) {
            return;
        }
        const now = new Date();
        const pad = function (n) { return n < 10 ? '0' + n : '' + n; };
        el.textContent = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) +
            ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds());
    }

    renderClock();
    setInterval(renderClock, 1000);
})();
