/**
 * ECharts 图表渲染封装
 */
let chartInstance = null;

/**
 * 渲染图表
 * @param {Array} data  [{label, value}]
 * @param {string} type bar | pie | line
 * @param {string} title 图表标题
 */
function renderChart(data, type, title) {
    const el = document.getElementById('chartBox');
    if (!data || data.length === 0) {
        if (chartInstance) {
            chartInstance.clear();
        }
        return;
    }
    if (!chartInstance) {
        chartInstance = echarts.init(el);
    }
    const labels = data.map(function (d) { return d.label; });
    const values = data.map(function (d) { return d.value; });

    let option;
    if (type === 'pie') {
        option = {
            title: { text: title, left: 'center' },
            tooltip: { trigger: 'item' },
            legend: { bottom: 10 },
            series: [{
                type: 'pie',
                radius: '60%',
                data: data.map(function (d) { return { name: d.label, value: d.value }; })
            }]
        };
    } else if (type === 'line') {
        option = {
            title: { text: title, left: 'center' },
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: labels },
            yAxis: { type: 'value' },
            series: [{ type: 'line', data: values, smooth: true }]
        };
    } else {
        option = {
            title: { text: title, left: 'center' },
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: labels },
            yAxis: { type: 'value' },
            series: [{ type: 'bar', data: values, barWidth: '50%' }]
        };
    }
    chartInstance.setOption(option, true);
}
