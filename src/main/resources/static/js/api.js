/**
 * 统一请求封装
 * @param {string} url    接口地址
 * @param {string} method 请求方法
 * @param {object} body   请求体
 * @returns {Promise<any>} 业务数据 data
 */
async function api(url, method, body) {
    const resp = await fetch(url, {
        method: method || 'GET',
        headers: { 'Content-Type': 'application/json' },
        body: body ? JSON.stringify(body) : undefined
    });
    const json = await resp.json();
    if (json.code !== 0) {
        if (typeof layui !== 'undefined' && layui.layer) {
            layui.layer.msg(json.message || '请求失败', { icon: 2 });
        }
        throw new Error(json.message || '请求失败');
    }
    return json.data;
}
