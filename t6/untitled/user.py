import requests
from collections import Counter
import time

# Gateway 的地址和端口
GATEWAY_URL = "http://127.0.0.1:8080"

# --- 选择你要测试的负载均衡组 ---
# 根据你的 application.yml:
# /ord/** 路由到 order-group (8081: 权重 3, 8083: 权重 7)
# /user/** 路由到 user-group (8084: 权重 3, 8085: 权重 7)

# ##################################################################
# # 测试 order-group，使用一个真实的 ord 请求路径示例: (当前被注释，测试 user-group)
# ##################################################################
# TEST_PATH = "/ord/item/1" # 使用实际的 ord 请求路径格式
# EXPECTED_WEIGHTS = {
#     "来自端口 8081 的响应内容": 3, # !!请替换成 8081 后端实际返回的用于区分的响应体或标识!!
#     "来自端口 8083 的响应内容": 7  # !!请替换成 8083 后端实际返回的用于区分的响应体或标识!!
# }
# # 如果你的后端服务只返回端口号，则可以这样设置 EXPECTED_WEIGHTS：
# # EXPECTED_WEIGHTS = {"8081": 3, "8083": 7}
# # 或者如果后端返回 "Hello from backend on port XXXX!" 这种，就设置为：
# # EXPECTED_WEIGHTS = {"Hello from backend on port 8081!": 3, "Hello from backend on port 8083!": 7}


# ##################################################################
# # 测试 user-group，使用一个真实的用户请求路径示例: (当前被启用，测试 user-group)
# ##################################################################
TEST_PATH = "/user/profile/1" # 使用实际的 user 请求路径格式
EXPECTED_WEIGHTS = {
    "来自端口 8084 的响应内容": 3, # !!请替换成 8084 后端实际返回的用于区分的响应体或标识!!
    "来自端口 8085 的响应内容": 7  # !!请替换成 8085 后端实际返回的用于区分的响应体或标识!!
}
# 如果你的后端服务只返回端口号，则可以这样设置 EXPECTED_WEIGHTS：
# EXPECTED_WEIGHTS = {"8084": 3, "8085": 7}


# 发送请求的总数
NUM_REQUESTS = 123 # 发送足够多的请求才能看到比较接近权重的分布

# 每个请求之间的延迟（秒）
# Gateway 配置的限流是 10次/10秒 (即平均 1次/秒)，为了避免触发限流，间隔需要大于 0.1 秒
# 你将间隔设置为 0.05 秒，这可能会比较容易触发限流，测试时请留意 429 错误
REQUEST_DELAY_SECONDS = 0.1

def test_load_balancer(gateway_url, test_path, num_requests, expected_weights, delay):
    print(f"正在测试 Gateway 上的负载均衡...")
    print(f"目标 Gateway: {gateway_url}")
    print(f"测试路径: {test_path} (应匹配 Gateway 路由规则并路由到加权组)")
    print(f"发送请求总数: {num_requests}")
    print(f"每个请求间隔: {delay} 秒")
    print(f"期望的权重分布 (基于后端响应内容): {expected_weights}")
    print("-" * 40)

    results = Counter()
    failed_requests = 0
    start_time = time.time()

    print("\n--- 单个请求结果 ---") # 添加头部提示

    for i in range(num_requests):
        try:
            # 发送 GET 请求到 Gateway
            response = requests.get(f"{gateway_url}{test_path}", timeout=10) # 设置超时

            # 检查响应状态码
            if response.status_code == 200:
                # 假设后端服务返回的内容可以标识自己
                backend_info = response.text.strip()
                results[backend_info] += 1
                # 打印每次请求的结果
                print(f"请求 {i+1}: 由后端 '{backend_info}' 处理")
            elif response.status_code == 429:
                print(f"请求 {i+1}: 收到 429 Too Many Requests，限流被触发。请考虑增加请求间隔。")
                failed_requests += 1
            else:
                print(f"请求 {i+1}: 收到非 200 或 429 状态码: {response.status_code}")
                failed_requests += 1

        except requests.exceptions.RequestException as e:
            print(f"请求 {i+1}: 发生请求错误: {e}")
            failed_requests += 1
        except Exception as e:
            print(f"请求 {i+1}: 发生未知错误: {e}")
            failed_requests += 1

        # 加入时间间隔
        time.sleep(delay)

    end_time = time.time()
    elapsed_time = end_time - start_time

    print("\n" + "=" * 40) # 添加分隔线
    print("--- 测试总结 ---")

    print(f"总共发送请求数: {num_requests}")
    print(f"失败请求数: {failed_requests}")

    total_handled = sum(results.values())
    print(f"成功由后端处理的请求数: {total_handled}")

    if total_handled == 0:
        print("没有请求成功到达后端服务并返回响应。请检查 Gateway 和后端服务是否正常运行。")
        return

    print("\n后端处理请求分布统计:")

    # 打印统计结果和观察到的百分比
    print("--- 观察到的分布 ---")
    for backend_info, count in results.items():
        percentage = (count / total_handled) * 100
        print(f"- 后端标识 '{backend_info}': {count} 次 ({percentage:.2f}%)")

    # 打印期望的分布 (注意，此处只打印了观察到的分布，根据你提供的代码去掉了期望分布的打印)
    # 如果需要，可以取消注释下面的代码块来重新打印期望分布
    # print("\n--- 期望的分布 (基于配置的权重) ---")
    # total_weight = sum(expected_weights.values())
    # if total_weight > 0:
    #     for backend_info, weight in expected_weights.items():
    #         expected_percentage = (weight / total_weight) * 100
    #         print(f"- 后端标识 '{backend_info}': 期望约 {expected_percentage:.2f}%")
    # else:
    #     print("错误: 期望的权重总和为零。请检查 EXPECTED_WEIGHTS 配置。")

    print(f"\n测试总耗时: {elapsed_time:.2f} 秒")
    print("=" * 40)


if __name__ == "__main__":
    # 在运行前，请务必根据你的后端服务实际返回的内容，更新 EXPECTED_WEIGHTS 字典的键！
    # 例如，如果后端服务 8084 返回文本 "User Svc A", 8085 返回 "User Svc B", 则设置为 {"User Svc A": 3, "User Svc B": 7}
    # 如果你不确定后端返回什么，可以先用浏览器或 curl 请求一次 Gateway 的测试路径 (例如 http://127.0.0.1:8080/user/profile/1)，看看响应内容是什么，然后用来更新 EXPECTED_WEIGHTS 的键。

    print("重要提示：请检查并根据你的后端服务实际返回的内容修改脚本中的 'EXPECTED_WEIGHTS' 字典的键！")
    print(f"当前设置的测试路径是: {TEST_PATH}")
    print(f"当前设置的期望权重是: {EXPECTED_WEIGHTS}")
    print(f"当前设置的每个请求间隔是: {REQUEST_DELAY_SECONDS} 秒")
    # 暂停几秒，给用户阅读提示的时间
    # time.sleep(5)

    test_load_balancer(GATEWAY_URL, TEST_PATH, NUM_REQUESTS, EXPECTED_WEIGHTS, REQUEST_DELAY_SECONDS)