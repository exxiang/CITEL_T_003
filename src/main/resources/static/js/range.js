/**
 * 查询区间编辑器
 */

/** 添加一行区间输入 */
function addRangeRow(containerId) {
    const container = document.getElementById(containerId);
    const row = document.createElement('div');
    row.className = 'range-row';
    row.innerHTML =
        '<span class="range-index"></span>' +
        '<input type="number" class="layui-input range-min" placeholder="最小值">' +
        '<span class="range-sep">~</span>' +
        '<input type="number" class="layui-input range-max" placeholder="最大值">' +
        '<button type="button" class="btn-delete range-del" onclick="delRangeRow(this)">' +
        '<i class="layui-icon layui-icon-delete"></i>删除</button>';
    container.appendChild(row);
    refreshRangeIndex(container);
}

/** 删除一行区间 */
function delRangeRow(btn) {
    btn.parentElement.remove();
    refreshRangeIndex(btn.parentElement.parentElement);
}

/** 刷新区间行序号 */
function refreshRangeIndex(container) {
    Array.from(container.children).forEach(function (row, i) {
        row.querySelector('.range-index').textContent = '区间' + (i + 1) + '：';
    });
}

/** 收集当前容器内已填写的区间 */
function getRanges(containerId) {
    const container = document.getElementById(containerId);
    const list = [];
    Array.from(container.children).forEach(function (row) {
        const min = row.querySelector('.range-min').value;
        const max = row.querySelector('.range-max').value;
        if (min === '' || max === '') {
            return;
        }
        list.push({ min: Number(min), max: Number(max) });
    });
    return list;
}

/** 回填区间（重新加载保存条件时使用） */
function fillRanges(containerId, ranges) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    (ranges || []).forEach(function (r) {
        addRangeRow(containerId);
        const row = container.lastElementChild;
        row.querySelector('.range-min').value = r.min;
        row.querySelector('.range-max').value = r.max;
    });
    refreshRangeIndex(container);
}
