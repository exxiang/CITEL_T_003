/**
 * 查询统计页逻辑：按年龄 / 按飞行里程 / 按飞行时间 三种模式
 */
let currentMode = 'age';
let viewType = 'list';
let statData = [];

const MODE_CONFIG = {
    age: {
        api: '/api/persons/query/age',
        stat: '/api/statistics/age',
        container: 'ageRangeRows',
        key: 'ageRanges',
        charts: ['bar', 'pie'],
        title: '年龄分布'
    },
    mileage: {
        api: '/api/persons/query/mileage',
        stat: '/api/statistics/mileage',
        container: 'mileageRangeRows',
        key: 'mileageRanges',
        charts: ['bar', 'pie'],
        title: '里程分布'
    },
    time: {
        api: '/api/persons/query/time',
        stat: '/api/statistics/time',
        container: 'timeRangeRows',
        key: 'timeRanges',
        charts: ['line'],
        title: '时间分布'
    }
};

function modeCfg() {
    return MODE_CONFIG[currentMode];
}

function buildWhere() {
    const cfg = modeCfg();
    const where = {};
    where[cfg.key] = getRanges(cfg.container);
    return where;
}

layui.use(['table', 'layer', 'element', 'form'], function () {
    const table = layui.table;
    const form = layui.form;
    window.__table = table;

    addRangeRow('ageRangeRows');
    addRangeRow('mileageRangeRows');
    addRangeRow('timeRangeRows');
    updateViewButtons();
    loadConditionOptions();

    table.render({
        elem: '#personTable',
        method: 'post',
        contentType: 'application/json',
        data: [],
        page: { limit: 20, limits: [10, 20] },
        request: { pageName: 'current', limitName: 'size' },
        parseData: function (res) {
            if (res.code !== 0) {
                layui.layer.msg(res.message || '请求失败', { icon: 2 });
                return { code: 1, msg: res.message || 'error', count: 0, data: [] };
            }
            return { code: 0, msg: res.message, count: res.data.total, data: res.data.records };
        },
        cols: [[
            { type: 'numbers', title: '序号', width: 80, fixed: 'left' },
            { field: 'id', title: '人员ID', width: 120 },
            { field: 'gender', title: '性别', width: 80, templet: function (d) { return d.gender === 1 ? '男' : '女'; } },
            { field: 'birthYear', title: '出生年份', width: 100 },
            { field: 'age', title: '年龄', width: 90 },
            { field: 'totalMileage', title: '总旅行里程(公里)', width: 160 },
            { field: 'totalTravelTime', title: '总旅行时间(小时)', width: 160 }
        ]]
    });

    layui.element.on('tab(modeTab)', function (data) {
        currentMode = ['age', 'mileage', 'time'][data.index];
        updateViewButtons();
        switchView('list');
        reloadTable();
    });

    form.on('select(savedConditionSelect)', function (data) {
        onSavedConditionSelect(data.value);
    });
});

/** 下拉框选中已保存区间（layui select 事件与原生 onchange 双保险） */
function onSavedConditionSelect(value) {
    if (value) {
        loadConditionById(value);
    }
}

/** 根据当前模式显示/隐藏图形按钮（列表始终显示） */
function updateViewButtons() {
    const cfg = modeCfg();
    document.querySelectorAll('.switch-btn').forEach(function (b) {
        const type = b.getAttribute('data-type');
        b.style.display = (type === 'list' || cfg.charts.indexOf(type) >= 0) ? '' : 'none';
    });
}

/** 刷新当前模式表格（无区间时展示空表，不发起请求） */
function reloadTable() {
    const ranges = getRanges(modeCfg().container);
    if (ranges.length === 0) {
        window.__table.reload('personTable', { data: [] });
        return;
    }
    window.__table.reload('personTable', {
        url: modeCfg().api,
        method: 'post',
        contentType: 'application/json',
        where: buildWhere(),
        page: { curr: 1 }
    });
}

/** 执行查询：刷新列表并同步统计 */
async function doQuery() {
    const ranges = getRanges(modeCfg().container);
    if (ranges.length === 0) {
        layui.layer.msg('请先添加查询区间', { icon: 0 });
        return;
    }
    try {
        reloadTable();
        await refreshStat();
    } catch (e) {
        // 错误提示已在 api 中处理
    }
}

/** 拉取当前模式的统计并渲染图表 */
async function refreshStat() {
    const cfg = modeCfg();
    const body = {};
    body[cfg.key] = getRanges(cfg.container);
    statData = await api(cfg.stat, 'POST', body);
    if (viewType !== 'list') {
        renderChart(statData, viewType, cfg.title);
    }
}

/** 切换展示方式：列表 / 柱状图 / 饼图 / 折线图 */
function switchView(type) {
    const cfg = modeCfg();
    const listBox = document.getElementById('listBox');
    const chartBox = document.getElementById('chartBox');

    if (type === 'list') {
        listBox.style.display = 'block';
        chartBox.style.display = 'none';
        viewType = 'list';
    } else {
        if (cfg.charts.indexOf(type) < 0) {
            const names = { bar: '柱状图', pie: '饼图', line: '折线图' };
            layui.layer.msg('当前模式不支持' + (names[type] || type) + '，可切换到' +
                (currentMode === 'time' ? '年龄/里程' : '时间') + '模式查看', { icon: 0 });
            return;
        }
        listBox.style.display = 'none';
        chartBox.style.display = 'block';
        viewType = type;
        const ranges = getRanges(cfg.container);
        if (ranges.length === 0) {
            layui.layer.msg('请先填写查询区间，再查看图表', { icon: 0 });
            renderChart([], type, cfg.title);
        } else {
            refreshStat().catch(function () {});
        }
    }

    document.querySelectorAll('.switch-btn').forEach(function (b) {
        b.classList.remove('layui-btn-normal');
    });
    const btn = document.querySelector('.switch-btn[data-type="' + type + '"]');
    if (btn) {
        btn.classList.add('layui-btn-normal');
    }
}

/** 保存当前三种模式的查询区间 */
function saveCondition() {
    layui.layer.prompt({ title: '请输入条件名称', formType: 0 }, function (value, index) {
        layui.layer.close(index);
        if (!value) {
            return;
        }
        const payload = {
            conditionName: value,
            ageRanges: getRanges('ageRangeRows'),
            mileageRanges: getRanges('mileageRangeRows'),
            timeRanges: getRanges('timeRangeRows')
        };
        api('/api/conditions', 'POST', payload).then(function (id) {
            layui.layer.msg('保存成功', { icon: 1 });
            loadConditionOptions(id);
        }).catch(function () {});
    });
}

/** 加载已保存条件到下拉框；保存后调用可选中新条件，?id= 进入时自动加载 */
async function loadConditionOptions(selectedId) {
    const list = await api('/api/conditions').catch(function () { return []; });
    const select = document.getElementById('savedConditionSelect');
    const urlId = selectedId || new URLSearchParams(location.search).get('id') || '';
    select.innerHTML = '<option value="">选择已保存区间...</option>' +
        list.map(function (c) {
            return '<option value="' + c.id + '">' + escapeHtml(c.conditionName) + '</option>';
        }).join('');
    select.value = urlId;
    layui.form.render('select');
    if (urlId && !selectedId) {
        loadConditionById(urlId, true);
    }
}

/** 按ID加载已保存条件并回填区间输入框；autoQuery 为 true 时（条件管理页跳转）自动查询 */
async function loadConditionById(id, autoQuery) {
    try {
        const cond = await api('/api/conditions/' + id);
        const param = typeof cond.queryParam === 'string' ? JSON.parse(cond.queryParam) : (cond.queryParam || {});
        fillRanges('ageRangeRows', param.ageRanges || []);
        fillRanges('mileageRangeRows', param.mileageRanges || []);
        fillRanges('timeRangeRows', param.timeRanges || []);
        if (autoQuery) {
            layui.layer.msg('已加载条件：' + cond.conditionName, { icon: 1 });
            await doQuery();
        } else {
            layui.layer.msg('已加载条件：' + cond.conditionName + '，可编辑后点击查询', { icon: 0 });
        }
    } catch (e) {
        // 错误提示已在 api 中处理
    }
}

/** HTML 转义，防止条件名包含特殊字符时破坏下拉选项 */
function escapeHtml(text) {
    return String(text == null ? '' : text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
