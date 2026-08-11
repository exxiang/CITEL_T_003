/**
 * 区间管理面板逻辑（单页应用内嵌）
 * 面板首次显示时初始化表格，支持加载到查询页 / 删除
 */
layui.use(['table', 'layer', 'form'], function () {
    const table = layui.table;
    let inited = false;

    /** 由 app.js 面板切换时回调 */
    window.onPanelShow = function (name) {
        if (name === 'conditions') {
            if (!inited) {
                init();
                inited = true;
            }
            loadList();
        }
    };

    function init() {
        table.render({
            elem: '#condTable',
            text: { none: '暂无保存的区间条件' },
            cols: [[
                { field: 'id', title: 'ID', width: 80, align: 'center' },
                { field: 'conditionName', title: '条件名称', templet: function (d) {
                    return '<i class="layui-icon layui-icon-file" style="margin-right:6px;color:#6B7280;"></i>' + d.conditionName;
                } },
                { field: 'createdAt', title: '创建时间', width: 190, templet: function (d) {
                    return '<span class="cell-time">' + d.createdAt + '</span>';
                } },
                { title: '操作', width: 160, align: 'right', toolbar: '#opTpl' }
            ]],
            data: [],
            page: false
        });

        table.on('tool(condTable)', function (obj) {
            if (obj.event === 'load') {
                window.switchPanel('query');
                loadConditionById(obj.data.id);
                loadConditionOptions(obj.data.id);
            } else if (obj.event === 'del') {
                layui.layer.confirm('确定删除该条件？', { icon: 3 }, function (index) {
                    layui.layer.close(index);
                    api('/api/conditions/' + obj.data.id, 'DELETE').then(function () {
                        layui.layer.msg('删除成功', { icon: 1 });
                        loadList();
                        loadConditionOptions();
                    }).catch(function () {});
                });
            }
        });
    }

    /** 刷新区间列表 */
    function loadList() {
        api('/api/conditions').then(function (list) {
            table.reloadData('condTable', { data: list });
            const countEl = document.getElementById('condCount');
            if (countEl) {
                countEl.textContent = '共 ' + list.length + ' 条记录';
            }
        }).catch(function () {});
    }
});
