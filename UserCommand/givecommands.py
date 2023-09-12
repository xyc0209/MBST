import requests
import json
import random
import tornado.ioloop
import tornado.web
import datetime
import uuid
import ast
from nacos import NacosClient, NacosException
import uuid

server_config = {
    'server_addresses': 'localhost:8848',
    'namespace': 'public',
    'username': 'nacos',
    'password': 'nacos'
}
client = NacosClient(server_addresses='localhost:8848', namespace='public', username='nacos', password='nacos')

def getServiceUrl(serviceName):
# 获取服务实例列表
    service_name = serviceName
    # try:
    #     service = client.get_service(service_name)
    #     instances = service['hosts']
    # except NacosException as e:
    #     # 处理获取服务实例列表失败的情况
    #     pass
    instances = client.list_naming_instance(service_name=service_name, namespace_id='public')
    print(instances)
    print(instances['hosts'])
    # 随机选择一个实例
    instance = random.choice(instances['hosts'])
    print(f"selected instance: {instance['ip']}:{instance['port']}")
    return 'http://'+ instance['ip'] + ':' + str(instance['port'])
# # 定义负载均衡算法（加权轮询）
# def weighted_round_robin(instances):
#     weights = [instance.get('weight', 1) for instance in instances]
#     max_weight = max(weights)
#     index = -1
#     current_weight = 0
#     while True:
#         index = (index + 1) % len(instances)
#         if index == 0:
#             current_weight -= max_weight
#             if current_weight < 0:
#                 current_weight = max(weights)
#         if instances[index].get('health', True) and instances[index].get('weight', 1) >= current_weight:
#             return instances[index]
#         if index == len(instances) - 1:
#             break
#
# # 使用负载均衡算法获取实例
# instance = weighted_round_robin(instances)
# host = instance['ip']
# port = instance['port']
# print(host+":"+port)

class Service:
    def __init__(self, serviceName, port, basic_url):
        self.serviceName = serviceName
        self.port = port
        self.base_url = basic_url

    def getbasicurl(self):
        # return 'http://'+self.serviceName + ":" +str(self.port) +"/" + self.base_url
        return getServiceUrl(self.serviceName)

    def geturl(self, str):
        return "http://" + self.base_url +"/" + str

    def __str__(self):
        return self.serviceName

# control the k8s cluster
url_apizipkin = 'http://192.168.1.102:30001/api/v2/trace/'

serviceList = {
    'ts-voucher-service' : Service('ts-voucher-service', 16101, 'api/v1/'),
    'ts-verification-code-service' : Service('ts-verification-code-service', 15678, '172.16.17.37:30019/api/v1/verifycode'),
    'ts-travel-service' : Service('ts-travel-service' , 12346, '172.16.17.37:30006/api/v1/travelservice'),                   #user
    'ts-travel-plan-service' : Service('ts-travel-plan-service', 14322, 'api/v1/travelplanservice'),      #user
    'ts-travel2-service' : Service('ts-travel2-service' , 16346, 'api/v1/travel2service'),                #user
    'ts-train-service' : Service('ts-train-service' , 14567, '172.16.17.37:30004/api/v1/trainservice'),
    'ts-ticketinfo-service' : Service('ts-ticketinfo-service', 15681, 'api/v1/ticketinfoservice'),        #user
    'ts-ticket-office-service' : Service('ts-ticket-office-service' , 16108, 'api/v1/'),                  #user
    'ts-station-service' : Service('ts-station-service', 12345, '172.16.17.37:30005/api/v1/stationservice'),
    'ts-auth-service' : Service('ts-auth-service' , 12340, '172.16.17.37:30000/api/v1/users'),                               #user
    'ts-security-service' : Service('ts-security-service' , 11188, 'api/v1/securityservice'),
    'ts-seat-service' : Service('ts-seat-service' , 18898, '172.16.17.37:30009/api/v1/seatservice'),
    'ts-route-service' : Service('ts-route-service' , 11178, '172.16.17.37:30003/api/v1/routeservice'),                    # user
    'ts-route-plan-service' : Service('ts-route-plan-service', 14578, 'api/v1/routeplanservice'),       # user
    'ts-rebook-service' : Service('ts-rebook-service' , 18886, 'api/v1/rebookservice'),                 # user
    'ts-price-service' : Service('ts-price-service' , 16579, '172.16.17.37:30008/api/v1/priceservice'),
    'ts-preserve-service' : Service('ts-preserve-service' , 14568, 'api/v1/preserveservice'),
    'ts-preserve-other-service' : Service('ts-preserve-other-service' , 14569, 'api/v1/preserveotherservice'),
    'ts-payment-service' : Service('ts-payment-service' , 19001, 'api/v1/paymentservice'),
    'ts-order-service' : Service('ts-order-service' , 12031, '172.16.17.37:30010/api/v1/orderservice'),
    'ts-order-other-service' : Service('ts-order-other-service' , 12032, 'api/v1/orderOtherService'),
    'ts-news-service' : Service('ts-news-service' , 12862, 'api/v1/'),
    'ts-notification-service' : Service('ts-notification-service' , 17853, 'api/v1/notifyservice'),
    'ts-user-service' : Service('ts-user-service' , 12340, '172.16.17.37:30018/api/v1/users'),
    'ts-inside-payment-service' : Service('ts-inside-payment-service' , 18673, 'api/inside_pay_service'),
    'ts-food-service' : Service('ts-food-service' , 18856, '172.16.17.37:30017/api/foodservice'),
    'ts-food-delivery-service' : Service('ts-food-delivery-service' , 18957, '172.16.17.37:30016/api/fooddeliveryservice'),
    'ts-execute-service' : Service('ts-execute-service' , 12386, 'api/v1/executeservice'),
    'ts-contacts-service' : Service('ts-contacts-service' , 12347, '172.16.17.37:30012/api/v1/contactservice'),
    'ts-consign-service' : Service('ts-consign-service' , 16111, '172.16.17.37:30013/api/v1/consignservice'),
    'ts-consign-price-sertavice' : Service('ts-consign-price-service', 16110, '172.16.17.37:30014/api/v1/consignpriceservice'),
    'ts-config-service' : Service('ts-config-service' , 15679, '172.16.17.37:30011/api/v1/configservice'),
    'ts-cancel-service' : Service('ts-cancel-service', 18885, 'api/v1/cancelservice'),
    'ts-basic-service' : Service('ts-basic-service' , 15680, '172.16.17.37:30007/api/v1/basicservice'),
    'ts-assurance-service' : Service('ts-assurance-service' , 18888, 'api/v1/assuranceservice'),
    'ts-admin-user-service' : Service('ts-admin-user-service' , 16115, 'api/v1/adminuserservice/users'), #admin
    'ts-admin-travel-service' : Service('ts-admin-travel-service' , 16114, 'api/v1/admintravelservice'), #admin
    'ts-admin-route-service' : Service('ts-admin-route-service' , 16113, 'api/v1/adminrouteservice'), # admin
    'ts-admin-order-service' : Service('ts-admin-order-service' , 16112, 'api/v1/adminorderservice'), # admin
    'ts-admin-basic-info-service' : Service('ts-admin-basic-info-service' , 18767, 'api/v1/adminbasicservice') # admin
}

serviceList1 = {}
for i in serviceList:
    serviceList1[i] = [serviceList[i]]
serviceList1['ts-travels-service'] = Service('ts-travels-service', 12346, 'api/v1/travelsservice')
serviceList1['ts-travels2-service'] = Service('ts-travels2-service', 16346, 'api/v1/travels2service')

stations = [{'id': 'shanghai', 'name': 'Shang Hai', 'stayTime': 10}, {'id': 'shanghaihongqiao', 'name': 'Shang Hai Hong Qiao', 'stayTime': 10}, {'id': 'taiyuan', 'name': 'Tai Yuan', 'stayTime': 5}, {'id': 'beijing', 'name': 'Bei Jing', 'stayTime': 10}, {'id': 'nanjing', 'name': 'Nan Jing', 'stayTime': 8}, {'id': 'shijiazhuang', 'name': 'Shi Jia Zhuang', 'stayTime': 8}, {'id': 'xuzhou', 'name': 'Xu Zhou', 'stayTime': 7}, {'id': 'jinan', 'name': 'Ji Nan', 'stayTime': 5}, {'id': 'hangzhou', 'name': 'Hang Zhou', 'stayTime': 9}, {'id': 'jiaxingnan', 'name': 'Jia Xing Nan', 'stayTime': 2}, {'id': 'zhenjiang', 'name': 'Zhen Jiang', 'stayTime': 2}, {'id': 'wuxi', 'name': 'Wu Xi', 'stayTime': 3}, {'id': 'suzhou', 'name': 'Su Zhou', 'stayTime': 3}]
trains = [{'id': 'GaoTieOne', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 250}, {'id': 'GaoTieTwo', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 200}, {'id': 'DongCheOne', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 180}, {'id': 'ZhiDa', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 120}, {'id': 'TeKuai', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 120}, {'id': 'KuaiSu', 'economyClass': 50, 'confortClass': 50, 'averageSpeed': 90}]
routes = [{'id': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'stations': ['shanghai', 'nanjing', 'shijiazhuang', 'taiyuan'], 'distances': [0, 350, 1000, 1300], 'startStationId': 'shanghai', 'terminalStationId': 'taiyuan'}, {'id': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'stations': ['nanjing', 'xuzhou', 'jinan', 'beijing'], 'distances': [0, 500, 700, 1200], 'startStationId': 'nanjing', 'terminalStationId': 'beijing'}, {'id': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'stations': ['taiyuan', 'shijiazhuang', 'nanjing', 'shanghai'], 'distances': [0, 300, 950, 1300], 'startStationId': 'taiyuan', 'terminalStationId': 'shanghai'}, {'id': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'stations': ['shanghai', 'taiyuan'], 'distances': [0, 1300], 'startStationId': 'shanghai', 'terminalStationId': 'taiyuan'}, {'id': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'stations': ['shanghaihongqiao', 'jiaxingnan', 'hangzhou'], 'distances': [0, 150, 300], 'startStationId': 'shanghaihongqiao', 'terminalStationId': 'hangzhou'}, {'id': '92708982-77af-4318-be25-57ccb0ff69ad', 'stations': ['nanjing', 'zhenjiang', 'wuxi', 'suzhou', 'shanghai'], 'distances': [0, 100, 150, 200, 250], 'startStationId': 'nanjing', 'terminalStationId': 'shanghai'}, {'id': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'stations': ['nanjing', 'shanghai'], 'distances': [0, 250], 'startStationId': 'nanjing', 'terminalStationId': 'shanghai'}, {'id': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'stations': ['nanjing', 'suzhou', 'shanghai'], 'distances': [0, 200, 250], 'startStationId': 'nanjing', 'terminalStationId': 'shanghai'}, {'id': '084837bb-53c8-4438-87c8-0321a4d09917', 'stations': ['suzhou', 'shanghai'], 'distances': [0, 50], 'startStationId': 'suzhou', 'terminalStationId': 'shanghai'}, {'id': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'stations': ['shanghai', 'suzhou'], 'distances': [0, 50], 'startStationId': 'shanghai', 'terminalStationId': 'suzhou'}]
travel_data = [{0: {'trip_data': {'startPlace': 'taian', 'endPlace': 'weihai', 'departureTime': '2023-10-01 09:05:00', 'documentType':0, 'phone': '17865326452', 'weight': 10, 'isWithin': True}}}, {1: {'trip_data': {'startPlace': 'taian', 'endPlace': 'weihai', 'departureTime': '2023-10-01 09:05:00', 'documentType':0, 'phone': '17865326452', 'weight': 10, 'isWithin': True}}}, {2: {'trip_data': {'startPlace': 'taian', 'endPlace': 'weihai', 'departureTime': '2023-10-01 09:05:00', 'documentType':0, 'phone': '17865326452', 'weight': 10, 'isWithin': True}}}]
price_train_route = [{'id': '426d9494-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.44, 'firstClassPriceRate': 1.0}, {'id': '426d9495-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.33, 'firstClassPriceRate': 1.0}, {'id': '426d9496-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.56, 'firstClassPriceRate': 1.0}, {'id': '426d9497-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.42, 'firstClassPriceRate': 1.0}, {'id': '426d9498-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.07, 'firstClassPriceRate': 1.0}, {'id': '426d9499-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '0b23bd3e-876a-4af3-b920-c50a90c90b04', 'basicPriceRate': 0.57, 'firstClassPriceRate': 1.0}, {'id': '426d949a-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.2, 'firstClassPriceRate': 1.0}, {'id': '426d949b-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.6, 'firstClassPriceRate': 1.0}, {'id': '426d949c-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.04, 'firstClassPriceRate': 1.0}, {'id': '426d949d-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.57, 'firstClassPriceRate': 1.0}, {'id': '426d949e-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.79, 'firstClassPriceRate': 1.0}, {'id': '426d949f-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '9fc9c261-3263-4bfa-82f8-bb44e06b2f52', 'basicPriceRate': 0.62, 'firstClassPriceRate': 1.0}, {'id': '426d94a0-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.65, 'firstClassPriceRate': 1.0}, {'id': '426d94a1-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.38, 'firstClassPriceRate': 1.0}, {'id': '426d94a2-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.96, 'firstClassPriceRate': 1.0}, {'id': '426d94a3-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.21, 'firstClassPriceRate': 1.0}, {'id': '426d94a4-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.92, 'firstClassPriceRate': 1.0}, {'id': '426d94a5-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': 'd693a2c5-ef87-4a3c-bef8-600b43f62c68', 'basicPriceRate': 0.63, 'firstClassPriceRate': 1.0}, {'id': '426d94a6-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.5, 'firstClassPriceRate': 1.0}, {'id': '426d94a7-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.95, 'firstClassPriceRate': 1.0}, {'id': '426d94a8-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.0, 'firstClassPriceRate': 1.0}, {'id': '426d94a9-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.73, 'firstClassPriceRate': 1.0}, {'id': '426d94aa-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.67, 'firstClassPriceRate': 1.0}, {'id': '426d94ab-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '20eb7122-3a11-423f-b10a-be0dc5bce7db', 'basicPriceRate': 0.9, 'firstClassPriceRate': 1.0}, {'id': '426d94ac-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.59, 'firstClassPriceRate': 1.0}, {'id': '426d94ad-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.57, 'firstClassPriceRate': 1.0}, {'id': '426d94ae-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.56, 'firstClassPriceRate': 1.0}, {'id': '426d94af-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.24, 'firstClassPriceRate': 1.0}, {'id': '426d94b0-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.78, 'firstClassPriceRate': 1.0}, {'id': '426d94b1-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '1367db1f-461e-4ab7-87ad-2bcc05fd9cb7', 'basicPriceRate': 0.92, 'firstClassPriceRate': 1.0}, {'id': '426d94b2-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.47, 'firstClassPriceRate': 1.0}, {'id': '426d94b3-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.4, 'firstClassPriceRate': 1.0}, {'id': '426d94b4-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.82, 'firstClassPriceRate': 1.0}, {'id': '426d94b5-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.17, 'firstClassPriceRate': 1.0}, {'id': '426d94b6-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.29, 'firstClassPriceRate': 1.0}, {'id': '426d94b7-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '92708982-77af-4318-be25-57ccb0ff69ad', 'basicPriceRate': 0.96, 'firstClassPriceRate': 1.0}, {'id': '426d94b8-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.01, 'firstClassPriceRate': 1.0}, {'id': '426d94b9-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.06, 'firstClassPriceRate': 1.0}, {'id': '426d94ba-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.72, 'firstClassPriceRate': 1.0}, {'id': '426d94bb-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.97, 'firstClassPriceRate': 1.0}, {'id': '426d94bc-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.46, 'firstClassPriceRate': 1.0}, {'id': '426d94bd-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': 'aefcef3f-3f42-46e8-afd7-6cb2a928bd3d', 'basicPriceRate': 0.86, 'firstClassPriceRate': 1.0}, {'id': '426d94be-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.29, 'firstClassPriceRate': 1.0}, {'id': '426d94bf-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.12, 'firstClassPriceRate': 1.0}, {'id': '426d94c0-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.52, 'firstClassPriceRate': 1.0}, {'id': '426d94c1-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.62, 'firstClassPriceRate': 1.0}, {'id': '426d94c2-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.67, 'firstClassPriceRate': 1.0}, {'id': '426d94c3-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': 'a3f256c1-0e43-4f7d-9c21-121bf258101f', 'basicPriceRate': 0.11, 'firstClassPriceRate': 1.0}, {'id': '426d94c4-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.74, 'firstClassPriceRate': 1.0}, {'id': '426d94c5-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.57, 'firstClassPriceRate': 1.0}, {'id': '426d94c6-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.19, 'firstClassPriceRate': 1.0}, {'id': '426d94c7-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.46, 'firstClassPriceRate': 1.0}, {'id': '426d94c8-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.99, 'firstClassPriceRate': 1.0}, {'id': '426d94c9-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': '084837bb-53c8-4438-87c8-0321a4d09917', 'basicPriceRate': 0.58, 'firstClassPriceRate': 1.0}, {'id': '426d94ca-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieOne', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.22, 'firstClassPriceRate': 1.0}, {'id': '426d94cb-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'GaoTieTwo', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.13, 'firstClassPriceRate': 1.0}, {'id': '426d94cc-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'DongCheOne', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.03, 'firstClassPriceRate': 1.0}, {'id': '426d94cd-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'ZhiDa', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.07, 'firstClassPriceRate': 1.0}, {'id': '426d94ce-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'TeKuai', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.76, 'firstClassPriceRate': 1.0}, {'id': '426d94cf-307d-11eb-9b8e-af7fe23a8d86', 'trainType': 'KuaiSu', 'routeId': 'f3d4d4ef-693b-4456-8eed-59c0d717dd08', 'basicPriceRate': 0.75, 'firstClassPriceRate': 1.0}]
train_id =[{'id':'G6983','stations':['jinan','taian','qingdao','yantai','weihai']}]
# login to get the token
url_getToken = 'http://ts-auth-service:12340/api/v1/users/login'

# create
url_createUser = 'http://ts-admin-user-service:16115/api/v1/adminuserservice/users'
# create connect
url_createConnect = 'http://ts-admin-basic-info-service:18767/api/v1/adminbasicservice/adminbasic/contacts'
# create inside_payment
url_createInside = 'http://ts-inside-payment-service:18673/api/v1/inside_pay_service/inside_payment/account'
''''''
# route control, get: get info, post: insert route
url_getrouteinfo= 'http://ts-admin-route-service:16113/api/v1/adminrouteservice/adminroute'
# get train info
url_train = 'http://ts-train-service:14567/api/v1/trainservice/trains'
# travel control, get: get info, post, add travel
url_travel = 'http://ts-admin-travel-service:16114/api/v1/admintravelservice/admintravel'
# create price
url_createPrice = 'http://ts-admin-basic-info-service:18767/api/v1/adminbasicservice/adminbasic/prices'


ip_master = "192.168.1.129"
coll = 'http://'+ip_master+':32223/api/v1/collectdata'
k8s_url = 'http://'+ip_master+':32222/api/v1/controlk8s'


class Login:
    def __init__(self, username, password, ver, headers):
        # self.__url = serviceList['ts-auth-service'].geturl('login')
        self.__url = " http://172.16.17.37:30000/api/v1/users/login"
        # if ver == None:
        #     self.__data  = {"username": "admin", "password": "222222"}
        # else:
        #     self.__data = {"username": username, "password": password, "verificationCode": ver}
        self.__data = {"username": username, "password": password, "verificationCode": ver}
        req = requests.post(self.__url, json=self.__data, headers=headers)
        print(req.status_code)
        print(json.loads(req.text))
        self.__res = json.loads(req.text)['data']
        self.__token = json.loads(req.text)['data']['token']

    def getHeaders(self):
        heads = {
            # 'Content-Type': 'applon/json',
            'Authorization': 'Bearer ' + self.__token
        }
        return heads
    def getResponse_data(self):
        return self.__res


# 需求队列，不进行更改的
commands = []
commands1 = []
commands2 = []
def dorequests(i):
    commands_ofThis_user = commands[i]
    print("请求队列为" + str(commands[i]))
    headers = {'Content-Type':'application/json'}
    # adminLogin = Login('xyc', '111111', None,
    #                    {'Content-Type':'application/json'})
    nowTime = str(datetime.datetime.now()).replace(' ', 'T')
    username = commands_ofThis_user['username']
    trip_data = commands_ofThis_user['theTrip_data']
    # tripId = trip_data['tripId']
    startPlace = commands_ofThis_user['startPlace']
    endPlace = commands_ofThis_user['endPlace']
    travel_base = serviceList['ts-travel-service']
    order_base = serviceList['ts-order-service']
    order_base_name = 'order'
    # 1. login
    password = '111111'
    userLogin = Login(username, password, None,
                       {'Content-Type':'application/json'})
    header_users = userLogin.getHeaders()

    res = userLogin.getResponse_data()
    if res['username'] != username:
        return
    userId = res['userId'] #用户的uuid
    print(" 登陆成功， userId为 ： " + userId)
    headers['Authorization'] = 'Bearer ' +res['token']
    # 2. get connacts
    # headers['traceId'] = commands_ofThis_user['getConnactsuuid']
    # res_connacts = json.loads(requests.get(serviceList['ts-contacts-service'].geturl('contacts/account/' + userId), headers=headers).text)
    # connact = res_connacts['data'][0]
    # print("获取联系人成功：， 联系人为： " + str(res_connacts))

    #
    # 4. 查看trip

    TripInfo = {
        'startPlace' : startPlace,
        'endPlace' : endPlace,
        'departureTime': commands_ofThis_user['departureTime']
    }
    res_seeTrip = requests.post(
        url=travel_base.geturl('trips/left'),
        headers=headers,
        json=TripInfo)
    # print("plt.legend() : " + str(res_seeTrip.text))
    #

    # 6.3 create order

    price = 'economyClass'
    coachNum = i%10
    seatNum =str(i%20)+random.choice('ABCDE')
    print(coachNum)
    print(seatNum)
    order = {
        "boughtDate" : nowTime,
         "travelDate":"2023-10-01 09:34:00",
        "travelTime":"2023-10-01 09:34:00",
        "accountId":"3e5a07f4-4a9c-44cd-9997-1ce77883fc45",
        "contactsName":"xyc",
        "documentType":0,
        "contactsDocumentNumber":"371302199202064332",
        "trainNumber":"G6983",
        "coachNumber":coachNum,
        "seatClass":2,
        "seatNumber":seatNum,
        "from":"jinan",
        "to":"weihai",
        "status":0,
        "price":"450"
    }
    # header_users['traceId'] = commands_ofThis_user['createOrderuuid']
    res_buyorder = requests.post(
        url=order_base.geturl(order_base_name),
        headers=headers,
        json=order)
    print("创造订单成功：" + str(res_buyorder.text))
    # if json.loads(res_buyorder.text)['status'] == 1:
    ResponseOrder = json.loads(res_buyorder.text)['data']
    orderId = str(ResponseOrder['id'])
    # 6.4 pay for order
    # headers['traceId'] = commands_ofThis_user['payforOrder']
    res_payfororder = requests.get(
        url=order_base.geturl(order_base_name+"/orderPay/" + orderId),
        headers=headers
    )
    print("付款成功 " + str(res_payfororder.text))
    ResponsePay = json.loads(res_payfororder.text)['data']
    orderId =  str(uuid.uuid4())
    # 8. 托运服务
    # handleDate替换为ResponsePay['boughtDate']
    if i%2  == 0:
        res_consign = requests.post(
            url=serviceList['ts-consign-service'].geturl('consigns'),
            headers=headers,
            json={
                "orderId": orderId,
                "accountId": "3e5a07f4-4a9c-44cd-9997-1ce77883fc45",
                "handleDate": "2023-04-07 09:34:00",
                "targetDate": "2023-10-01 09:34:00",
                "from": startPlace,
                "to": endPlace,
                "consignee": username,
                "phone": trip_data['phone'],
                "weight": trip_data['weight'],
                "within": trip_data['isWithin']
            }
        )
        print("ORDER ID"+orderId)
        print("START"+startPlace)
        print("END"+endPlace)
        print("USERNAME"+username)
        print(trip_data['phone'])
        print(trip_data['weight'])
        print(trip_data['isWithin'])

        print("托运服务成功： " + str(res_consign.text))
    if i % 2 == 0:
        print(headers)
        print(serviceList['ts-food-service'].geturl('orders'))
        res_foodOrder = requests.post(
            url=serviceList['ts-food-service'].geturl('orders'),
            headers=headers,
            json={
                "orderId": orderId,
                "foodType": 2,
                "stationName": "yantai",
                "storeName": "KFC",
                "foodName": "Hamburger",
                "price": 5.0
            }
        )
        print("食物下单成功" + str(res_foodOrder.text))
        print(res_foodOrder.text)
        responseFoodOrder = json.loads(res_foodOrder.text)['data']
        foodOrderId = str(responseFoodOrder['id'])
        res_foodDelivery = requests.post(
            url=serviceList['ts-food-delivery-service'].geturl('orders'),
            headers=headers,
            json={
                "foodOrderId":foodOrderId,
                "stationFoodStoreId": "46454b57-73e1-4fd1-bff4-04bc79c3a6fa",
                "foodList": [{"foodName": "Hamburger", "price": 5.0}],
                "tripId": "3ccb6246-c69c-49fa-9aa2-15dd9fac6861",
                "seatNumber":seatNum,
                "createdTime": "2023-04-07 09:34:00",
                "deliveryTime": "2023-10-01 09:34:00",
                "deliveryFee":10
            }
        )
        print("食物递送成功" + str(res_foodDelivery.text))

def createCommand():
    # todo
    # 生成一个需求队列，在n此部署结构中不更改, 在此对应1000个用户进行相应的
    # 生成需求的同时，生成traceId，表示此次请求的生成id
    #清洗一下 travel data
    h = 0
    for q in travel_data:
        h = h + 1
    tripNUm = 0
    j = 1
    seatNumber = 1
    for i in range(0, 150):
        commands_ofThis_user = {}
        commands_ofThis_user['username'] = 'xyc'
        if (i + 1) > (j * 50):
            j = j+1
            tripNUm = tripNUm + 1
            seatNumber = 1
        theTrip_data = travel_data[tripNUm][tripNUm]['trip_data']
        commands_ofThis_user['theTrip_data'] = theTrip_data
        # 设置随机+起始地点以及 随机的终点
        stationsId = train_id[0]['stations']
        startId = random.randint(0, len(stationsId) - 2)
        startStation = stationsId[startId]
        a = True
        endStationId = 0
        while a:
            h = random.randint(startId + 1, len(stationsId) - 1)
            if h != startId:
                endStationId = h
                a = False

        endStation = stationsId[endStationId]
        commands_ofThis_user['startPlace'] = train_id[0]['stations'][startId]
        commands_ofThis_user['endPlace'] = train_id[0]['stations'][endStationId]

        commands_ofThis_user['departureTime'] = theTrip_data['departureTime']
        commands.append(commands_ofThis_user)


# web app : create user, users' connect, users' inside_payment
def createUsersAndConnects(name, times):
    adminLogin = Login('admin', '222222', None, {'ex_name':name, 'ex_times':times})
    heads = adminLogin.getHeaders()
    heads['ex_name'] = name +"datainit"
    # heads['ex_name'] = name
    heads['ex_times'] = times
    for i in range(0, 1000):
        user_data = {
            # 'userId': uuid.uuid1(),
            'userName': 'microserivce_userName' + str(i),
            'password': '111111',
            'gender': random.randint(0, 1),
            'documentType': 1,
            'documentNum': str(int(random.random()*1000000)) + str(int(random.random()*10000000)) + 'X',
            'email': 'microserivce_userName' + str(i) + "@163.com"
        }
        print("%d 's people is registered", i)
        q = requests.post(url_createUser, json=user_data, headers=heads)
        q_data = json.loads(q.text)
        if json.loads(q.text)['status'] == 1:
            # 联系人生成
            connect_data = {
                'id' : str(uuid.uuid1()),
                'accountId' : q_data['data']['userId'],
                'name' : 'Contacts_One' + str(i),
                'documentType' : 1,
                'documentNumber' : 'DocumentNumber_One' + str(i),
                'phoneNumber' : 'ContactsPhoneNum_One' + str(i)
            }
            q1 = requests.post(url_createConnect, json=connect_data, headers=heads)
            print("%d 's people's connect is registered", str(q1.text))
            q1.close()
           # inside_payment生成
            inside_payment_data = {
                'userId' : q_data['data']['userId'],
                'money' : '10000',
            }
            q2 = requests.post(url_createInside, json=inside_payment_data, headers=heads)
            print("%d 's people's insidepayment is registered", str(q2.text))
            q2.close()
            q.close()

# web app 创造travel
def createTravel(name, times):
    adminLogin = Login('admin', '222222', None, {'ex_name':name, 'ex_times':times})
    heads = adminLogin.getHeaders()
    heads['ex_name'] = name + "datainit"
    # heads['ex_name'] = name
    heads['ex_times'] = times
    h = 0
    now = datetime.datetime.now()
    datechange = {}
    for i in travel_data:
        trip_data = i[h]['trip_data']
        cost_time = i[h]['cost_time']
        h = h+1
        # 更改日期即可
        a = now + datetime.timedelta(hours=+random.randint(2, 4))
        startTime = a + datetime.timedelta(days=1)
        # startTime = now + datetime.timedelta(hours=+random.randint(2, 4))
        endTime = startTime + datetime.timedelta(minutes=+cost_time)
        trip_data['startingTime'] = str(startTime).replace(' ', 'T')
        trip_data['endTime'] = str(endTime).replace(' ', 'T')
        # 添加行程
        addTravel = requests.post(url=url_travel, headers=heads, json=trip_data)
        print(addTravel.text)
        datechange[trip_data['tripId']] = {'startingTime':trip_data['startingTime'], 'endTime':trip_data['endTime']}

    print("开始更新需求数据")
    for i in range(0, 1000):
        j = commands[i]
        theTrip_data = j['theTrip_data']
        if theTrip_data['tripId'] not in datechange.keys():
            print("不在datachange当中")
            continue
        theTrip_data['startingTime'] = datechange[theTrip_data['tripId']]['startingTime']
        theTrip_data['endTime'] = datechange[theTrip_data['tripId']]['endTime']
        j['theTrip_data'] = theTrip_data
        commands[i] = j
        print(commands[i]['theTrip_data'])
    # 全部数据更新完毕
    for i in range(1, 6):
        port = 32002 + i
        requests.get("http://"+ip_master+":"+str(port)+"/updateCommand", json={'data':str(commands)})
    print("更新需求数据完毕")


# web app创造 每个火车在每条路线上的价格
def create_price_train(name, times):
    adminLogin = Login('admin', '222222', None, {'ex_name':name, 'ex_times':times})
    heads = adminLogin.getHeaders()
    heads['ex_name'] = name+"datainit"
    # heads['ex_name'] = name
    heads['ex_times'] = times
    for i in price_train_route:
        add_price = requests.post(url_createPrice, headers=heads, json=i)


class InitDataUser(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        head = self.request.headers
        name = head['name']
        times = head['times']
        # print("初始化user and connnects" + str(name) + "_" +str(times))
        createUsersAndConnects(name, times)
        # print("初始化user成功")


class InitDataTravel(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        head = self.request.headers
        name = head['name']
        times = head['times']
        print("创造travel中" + str(name) + "_" +str(times))
        createTravel(name, times)
        print("创造travel成功")


class InitDataPrice(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        head = self.request.headers
        name = head['name']
        times = head['times']
        print("初始化price" + str(name) + "_" +str(times))
        create_price_train(name, times)
        print("初始化price成功")


class givecommand(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        # headers = self.request.headers
        # i = headers['i']
        # ex_name = headers['ex_name']
        # ex_times = headers['ex_times']
        for i in range(2):
            dorequests(int(i))


class updateCommand(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        body = self.request.body
        data = body.decode('utf-8')
        time_commands = ast.literal_eval(json.loads(data)['data'])
        # headers = self.request.headers
        # ex_name = headers['ex_name']
        # ex_times = headers['ex_times']
        # q = requests.get(coll + "/getCommand/" + str(ex_name) + "/" + str(ex_times))
        # c = json.loads(q.text)['command']
        # time_commands = ast.literal_eval(c)
        for i in range(0, 1000):
            j = commands[i]
            theTrip_data = j['theTrip_data']
            theTrip_data['startingTime'] = time_commands[i]['theTrip_data']['startingTime']
            theTrip_data['endTime'] = time_commands[i]['theTrip_data']['endTime']
            j['theTrip_data'] = theTrip_data
            commands[i] = j
        print("更新完毕")


class RequestForCircle(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        headers = self.request.headers
        i = int(headers['i'])
        ex_name = headers['ex_name']
        ex_times = headers['ex_times']

        # 请求
        commands_ofThis_user = commands[i]
        print("请求队列为" + str(commands[i]))
        adminLogin = Login('admin', '222222', None,
                           {'traceId': commands_ofThis_user['adminuuid'], 'ex_name': ex_name, 'ex_times': ex_times})
        nowTime = str(datetime.datetime.now()).replace(' ', 'T')
        username = commands_ofThis_user['username']
        trip_data = commands_ofThis_user['theTrip_data']
        tripId = trip_data['tripId']
        startStationsId = commands_ofThis_user['startStation']
        endStationsId = commands_ofThis_user['endStation']
        startStationsName = commands_ofThis_user['startStationName']
        endStationsName = commands_ofThis_user['endStationName']
        travel_base = None
        order_base = None
        order_base_name = None
        if str(tripId)[0] == 'G' or str(tripId)[0] == 'D':
            travel_base = serviceList['ts-travel-service']
            order_base = serviceList['ts-order-service']
            order_base_name = 'order'
        else:
            travel_base = serviceList['ts-travel2-service']
            order_base = serviceList['ts-order-other-service']
            order_base_name = 'orderOther'

        # 1. login
        password = '111111'
        userLogin = Login(username, password, '1234',
                          {'traceId': commands_ofThis_user['loginuuId'], 'ex_name': ex_name, 'ex_times': ex_times})
        header_users = userLogin.getHeaders()
        header_users['ex_name'] = ex_name
        header_users['ex_times'] = ex_times
        headers = adminLogin.getHeaders()
        headers['ex_name'] = ex_name
        headers['ex_times'] = ex_times
        res = userLogin.getResponse_data()
        if res['username'] != username:
            return
        userId = res['userId']  # 用户的uuid
        print(" 登陆成功， userId为 ： " + userId)

        # 4. 查看trip
        headers['traceId'] = commands_ofThis_user['seeTripuuid']
        TripInfo = {
            'startingPlace': startStationsName,
            'endPlace': endStationsName,
            'departureTime': str(trip_data['startingTime'])
        }
        res_seeTrip = requests.post(
            url=travel_base.geturl('trips/left'),
            headers=headers,
            json=TripInfo)
        print("plt.legend() : " + str(res_seeTrip.text))




class travelAdmin(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        headers = self.request.headers
        i = int(headers['i'])
        ex_name = headers['ex_name']
        ex_times = headers['ex_times']
        # todo : login
        adminLogin = Login('admin', '222222', None,
                       {'traceId': str(uuid.uuid1()), 'ex_name': ex_name, 'ex_times': str(ex_times)})
        headers1 = adminLogin.getHeaders()
        headers1['ex_name'] = ex_name
        headers1['ex_times'] = ex_times
        # todo : insert or delete or update travel
        start = i * 20
        # 先进行添加
        for h in range(start, start+20):
            trip_data = commands1[h]['add' + str(h) + '_travelData'][h]['trip_data']
            headers1['traceId'] = commands1[h]['addTraceId']
            requests.post(url=url_travel, headers=headers1, json=trip_data)
            if not i == 0:
                if h % 2 ==0:
                    #  delete
                    headers1['traceId'] = commands1[h]['deleteTraceId']
                    requests.delete(url=url_travel + "/" + str(commands1[h-20]['add'+str(h-20)+'_travelData'][h-20]['trip_data']['tripId']), headers=headers1)
                else:
                    u = commands1[h-20]['add' + str(h-20) + '_travelData'][h-20]['trip_data']
                    a = datetime.datetime.strptime(str(u['startingTime']).replace("T", " "), "%Y-%m-%d %H:%M:%S.%f")
                    b = datetime.datetime.strptime(str(u['endTime']).replace("T", " "), "%Y-%m-%d %H:%M:%S.%f")
                    u['startingTime'] = str(a + datetime.timedelta(hours=+1))
                    u['endTime'] = str(b + datetime.timedelta(hours=+1))
                    headers1['traceId'] = commands1[h]['updateTraceId']
                    requests.put(url=url_travel, headers=headers1, json=u)


class travelUser(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        headers = self.request.headers
        i = int(headers['i'])
        ex_name = headers['ex_name']
        ex_times = headers['ex_times']
        # login
        adminLogin = Login('admin', '222222', None,
                           {'traceId': str(uuid.uuid1()), 'ex_name': ex_name, 'ex_times': str(ex_times)})
        headersAdmin = adminLogin.getHeaders()
        headersAdmin['ex_name'] = ex_name
        headersAdmin['ex_times'] = ex_times


        # todo : insert or delete or update travel
        start = i * 20
        for h in range(start, start + 20):
            user_Number = 2000 + h
            user_data = {
                # 'userId': uuid.uuid1(),
                'userName': 'microserivce_userName' + str(user_Number),
                'password': '111111',
                'gender': random.randint(0, 1),
                'documentType': 1,
                'documentNum': str(int(random.random() * 1000000)) + str(int(random.random() * 10000000)) + 'X',
                'email': 'microserivce_userName' + str(i) + "@163.com"
            }
            headersAdmin['traceId'] =commands2[h]['a']
            q = requests.post(url_createUser, json=user_data, headers=headersAdmin)
            q_data = json.loads(q.text)
            if json.loads(q.text)['status'] == 1:
                # 联系人生成
                connect_data = {
                    'id': str(uuid.uuid1()),
                     'accountId': q_data['data']['userId'],
                    'name': 'Contacts_One' + str(i),
                    'documentType': 1,
                    'documentNumber': 'DocumentNumber_One' + str(i),
                    'phoneNumber': 'ContactsPhoneNum_One' + str(i)
                }
                headersAdmin['traceId'] = commands2[h]['b']
                q1 = requests.post(url_createConnect, json=connect_data, headers=headersAdmin)
                print("%d 's people's connect is registered", str(q1.text))
                q1.close()
                # inside_payment生成
                inside_payment_data = {
                    'userId': q_data['data']['userId'],
                    'money': '10000',
                }
                headersAdmin['traceId'] = commands2[h]['c']
                q2 = requests.post(url_createInside, json=inside_payment_data, headers=headersAdmin)
                print("%d 's people's insidepayment is registered", str(q2.text))
                q2.close()
                q.close()


class welcome(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        print("welcome")

def make_app():
    return tornado.web.Application([
        (r"/welcome", welcome),
        (r"/giveCommand", givecommand),
        (r"/initDataUser", InitDataUser),
        (r"/initDataTravel", InitDataTravel),
        (r"/initDataPrice", InitDataPrice),
        (r"/updateCommand", updateCommand),
        (r"/requestCircle", RequestForCircle),
        (r"/travelAdmin", travelAdmin),
        (r"/travelUser", travelUser)
    ])


if __name__ == '__main__':
    filename = 'a.txt'
    f = open(filename, 'r')
    commands = ast.literal_eval(f.read())
    #
    # filename1 = 'b.txt'
    # f1 = open(filename1, 'r')
    # commands1 = ast.literal_eval(f1.read())
    #
    # filename2 = 'c.txt'
    # f2 = open(filename2 , 'r')
    # commands2 = ast.literal_eval(f2.read())
    #
    app = make_app()
    app.listen(32002)
    tornado.ioloop.IOLoop.current().start()


    # h = []
    # for i in range(200):
    #     a = {}
    #     a['a'] = str(uuid.uuid1())
    #     a['b'] = str(uuid.uuid1())
    #     a['c'] = str(uuid.uuid1())
    #     h.append(a)
    #
    # print(h)
    # createCommand()
    # filename = 'a.txt'
    # with open(filename, 'w') as name:
    #     name.write(str(commands))
    #     name.close()
