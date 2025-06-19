// 不再需要全局的 resultDiv 引用
// const resultDiv = document.getElementById('result');
const myDataInput = document.getElementById('myDataInput');
const gatewayUrl = 'http://localhost:8080'; // 网关地址

// Helper function to get the result area for a given button
function getResultArea(button) {
    const resultAreaId = button.getAttribute('data-result-id');
    return document.getElementById(resultAreaId);
}

// Helper function to log and scroll for a specific result area
function logToSpecificResult(resultArea, message) {
    if (resultArea) {
        resultArea.textContent = message;
        // resultArea.scrollTop = resultArea.scrollHeight; // Optional: scroll to top when new load starts
    }
}

// Helper function to append, log and scroll for a specific result area
function logAndAppendToSpecificResult(resultArea, message) {
    if (resultArea) {
        resultArea.textContent += message + '\n';
        // Scroll to the bottom of the specific result area
        resultArea.scrollTop = resultArea.scrollHeight;
    }
}


// 接收 event 参数并阻止默认行为，并获取对应的结果区域
function makeRequest(event, path, setCookie = false) {
    event.preventDefault(); // 阻止默认事件，防止页面滚动

    const button = event.target;
    const resultArea = getResultArea(button);

    logToSpecificResult(resultArea, '加载中...'); // 清空结果区域，显示加载状态
    const url = gatewayUrl + path;

    // Set cookie if required
    if (setCookie) {
        document.cookie = "name=testvalue;path=/"; // 为根路径设置 Cookie
        console.log("Cookie 'name=testvalue' 已为路径 / 设置");
    } else {
        // Optional: clear the cookie for comparison
        document.cookie = "name=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        console.log("Cookie 'name' 已清除。");
    }

    console.log(`正在发送 GET 请求到: ${url}`);

    fetch(url, {
        method: 'GET',
        // Cookies are automatically sent by the browser for the same origin
        headers: {
            // Add any other headers if needed
            'Cache-Control': 'no-cache' // 测试期间阻止浏览器缓存
        }
    })
        .then(response => {
            console.log('原始响应:', response);
            console.log('响应头:', Array.from(response.headers.entries()));

            // Read response body as text
            return response.text().then(text => ({
                status: response.status,
                statusText: response.statusText,
                headers: response.headers,
                text: text
            }));
        })
        .then(data => {
            // 使用特定的结果区域进行日志记录
            logToSpecificResult(resultArea, `状态: ${data.status} ${data.statusText}\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应头 ---`);
            Array.from(data.headers.entries()).forEach(entry => {
                logAndAppendToSpecificResult(resultArea, `${entry[0]}: ${entry[1]}`);
            });
            logAndAppendToSpecificResult(resultArea, `-------------\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应体 ---`);
            logAndAppendToSpecificResult(resultArea, data.text);
            logAndAppendToSpecificResult(resultArea, `------------`);
        })
        .catch(error => {
            logToSpecificResult(resultArea, `错误: ${error}`);
            console.error('Fetch error:', error);
        });
}

// 接收 event 参数并阻止默认行为，并获取对应的结果区域
function sendFilterTest1(event) {
    event.preventDefault(); // 阻止默认事件，防止页面滚动

    const button = event.target;
    const resultArea = getResultArea(button);

    const url = gatewayUrl + '/filtertest1'; // 通过网关调用 provider-test1 (然后通过网关调用 /filtertest2 -> provider-test2)
    const myData = myDataInput.value; // 从输入框获取 JSON 字符串

    logToSpecificResult(resultArea, '加载中...'); // 清空结果区域，显示加载状态
    console.log(`正在发送到 ${url}，请求体:`, myData);

    fetch(url, {
        method: 'POST', // 过滤器测试预期请求体，使用 POST
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-cache'
        },
        body: myData // 发送 JSON string作为请求体
    })
        .then(response => {
            console.log('原始响应:', response);
            console.log('响应头:', Array.from(response.headers.entries()));
            return response.text().then(text => ({
                status: response.status,
                statusText: response.statusText,
                headers: response.headers,
                text: text
            }));
        })
        .then(data => {
            // 使用特定的结果区域进行日志记录
            logToSpecificResult(resultArea, `状态: ${data.status} ${data.statusText}\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应头 ---`);
            Array.from(data.headers.entries()).forEach(entry => {
                logAndAppendToSpecificResult(resultArea, `${entry[0]}: ${entry[1]}`);
            });
            logAndAppendToSpecificResult(resultArea, `-------------\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应体 ---`);
            logAndAppendToSpecificResult(resultArea, data.text);
            logAndAppendToSpecificResult(resultArea, `------------`);
        })
        .catch(error => {
            logToSpecificResult(resultArea, `错误: ${error}`);
            console.error('Fetch error:', error);
        });
}

// 接收 event 参数并阻止默认行为，并获取对应的结果区域
function sendFilterTest2(event) {
    event.preventDefault(); // 阻止默认事件，防止页面滚动

    const button = event.target;
    const resultArea = getResultArea(button);

    const url = gatewayUrl + '/filtertest2'; // 通过网关调用 provider-test2 (如果需要则解密)
    const myData = myDataInput.value; // 从输入框获取 JSON 字符串

    logToSpecificResult(resultArea, '加载中...'); // 清空结果区域，显示加载状态
    console.log(`正在发送到 ${url}，请求体:`, myData);

    fetch(url, {
        method: 'POST', // 过滤器测试预期请求体，使用 POST
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-cache'
        },
        body: myData // 发送 JSON string作为请求体
    })
        .then(response => {
            console.log('原始响应:', response);
            console.log('响应头:', Array.from(response.headers.entries()));
            return response.text().then(text => ({
                status: response.status,
                statusText: response.statusText,
                headers: response.headers,
                text: text
            }));
        })
        .then(data => {
            // 使用特定的结果区域进行日志记录
            logToSpecificResult(resultArea, `状态: ${data.status} ${data.statusText}\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应头 ---`);
            Array.from(data.headers.entries()).forEach(entry => {
                logAndAppendToSpecificResult(resultArea, `${entry[0]}: ${entry[1]}`);
            });
            logAndAppendToSpecificResult(resultArea, `-------------\n`);
            logAndAppendToSpecificResult(resultArea, `--- 响应体 ---`);
            logAndAppendToSpecificResult(resultArea, data.text);
            logAndAppendToSpecificResult(resultArea, `------------`);
        })
        .catch(error => {
            logToSpecificResult(resultArea, `错误: ${error}`);
            console.error('Fetch error:', error);
        });
}