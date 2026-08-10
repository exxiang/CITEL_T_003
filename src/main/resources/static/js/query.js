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

layui.use(['table', 'layer', 'element'], function () {
    const table = layui.table;
    window.__table = table;

    addRangeRow('ageRangeRows');
    addRangeRow('mileageRangeRows');
    addRangeRow('timeRangeRows');

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
        switchView('list');
        reloadTable();
    });

    loadSavedCondition();
});

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
        listBox.style.display = '';
        chartBox.style.display = 'none';
    } else {
        if (cfg.charts.indexOf(type) < 0) {
            const names = { bar: '柱状图', pie: '饼图', line: '折线图' };
            layui.layer.msg('当前模式不支持' + (names[type] || type) + '，可切换到' +
                (currentMode === 'time' ? '年龄/里程' : '时间') + '模式查看', { icon: 0 });
            return;
        }
        listBox.style.display = 'none';
        chartBox.style.display = '';
        renderChart(statData, type, cfg.title);
    }
    viewType = type;

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
        api('/api/conditions', 'POST', payload).then(function () {
            layui.layer.msg('保存成功', { icon: 1 });
        }).catch(function () {});
    });
}

/** 从条件管理页跳转而来时，加载并回填保存的区间 */
async function loadSavedCondition() {
    const params = new URLSearchParams(location.search);
    const id = params.get('id');
    if (!id) {
        return;
    }
    try {
        const cond = await api('/api/conditions/' + id);
        const param = typeof cond.queryParam === 'string' ? JSON.parse(cond.queryParam) : (cond.queryParam || {});
        fillRanges('ageRangeRows', param.ageRanges || []);
        fillRanges('mileageRangeRows', param.mileageRanges || []);
        fillRanges('timeRangeRows', param.timeRanges || []);
        layui.layer.msg('已加载条件：' + cond.conditionName, { icon: 1 });
        await doQuery();
    } catch (e) {
        // 错误提示已在 api 中处理
    }
}
