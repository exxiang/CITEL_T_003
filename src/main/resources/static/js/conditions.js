/**
 * 条件管理页逻辑：保存的查询区间列表、加载、删除
 */
layui.use(['table', 'layer'], function () {
    const table = layui.table;

    table.render({
        elem: '#condTable',
        cols: [[
            { field: 'id', title: 'ID', width: 80 },
            { field: 'conditionName', title: '条件名称' },
            { field: 'createdAt', title: '创建时间', width: 190 },
            { title: '操作', width: 160, toolbar: '#opTpl' }
        ]],
        data: [],
        page: false
    });

    table.on('tool(condTable)', function (obj) {
        if (obj.event === 'load') {
            location.href = 'query.html?id=' + obj.data.id;
        } else if (obj.event === 'del') {
            layui.layer.confirm('确定删除该条件？', { icon: 3 }, function (index) {
                layui.layer.close(index);
                api('/api/conditions/' + obj.data.id, 'DELETE').then(function () {
                    layui.layer.msg('删除成功', { icon: 1 });
                    loadList();
                }).catch(function () {});
            });
        }
    });

    loadList();

    /** 加载条件列表 */
    function loadList() {
        api('/api/conditions').then(function (list) {
            table.reloadData('condTable', { data: list });
        }).catch(function () {});
    }
});
