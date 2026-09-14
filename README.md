# 外卖运营管理平台
基于SpringBoot + MyBatis + MySQL + Redis + Vue2 的前后端分离外卖管理系统。

## ✨项目介绍
面向中小餐饮商家的外卖运营平台，分为商家端、管理端，实现菜品管理、套餐管理、订单流转。
独立开发商家AI智能客服模块，对接大模型API，实现订单咨询问答，增加异常降级处理。

## 🛠技术栈
后端：SpringBoot、MyBatis、MySQL、Redis、JWT
前端：Vue2、ElementUI
中间件：WebSocket
工具：Maven、Git、Nginx

## 📌核心功能
1. Redis缓存商铺信息，处理缓存穿透、击穿、雪崩，Redis生成全局唯一ID
2. WebSocket实现来单实时推送；SpringTask定时任务实现订单超时自动取消、释放库存
3. AI智能客服模块：RestTemplate封装第三方大模型API，超时捕获、限流异常降级；前端Vue+TS流式对话页面
4. JWT身份认证，完成菜品、套餐CRUD，订单状态流转
5. Nginx部署前端静态资源，反向代理后端接口

## 🚀启动方式
1. 创建MySQL数据库，导入sql脚本
2. 修改application.yml数据库连接（本地开发使用application-dev.yml）
3. SpringBoot启动主类
4. 启动Vue前端项目，Nginx配置前端资源

## 📂项目结构
sky-take-out                 # Maven 父工程
├── sky-common               # 公共模块：工具类、全局异常、通用返回结果
├── sky-pojo                 # 实体模块：entity、DTO、VO
├── sky-server               # SpringBoot 主模块，业务接口实现
│   ├── controller           # 接口层：菜品、套餐、订单、AI 客服接口
│   ├── service              # 业务逻辑层
│   ├── mapper               # MyBatis 数据访问层
│   ├── config               # WebSocket、Redis、JWT 配置类
│   └── resources            # 配置文件与 MyBatis 映射文件
├── pom.xml                  # 父工程依赖管理
└── README.md                # 项目文档