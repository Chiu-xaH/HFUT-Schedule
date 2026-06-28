# 聚在工大iOS适配设计

## 背景
外界呼声
## 条件
1. Mac ×
2. 时间充裕，无紧要事 ×
## 方案1：原生iOS开发 ×
不行，不会Swift，而且后期打算适配PC端
## 方案2：新建Kotlin单端iOS项目 ×
维护成本高，每次有更新还要同步iOS仓
## 方案3：改造现有Android项目为KMP √
### 前期铺垫（必要条件）
1. mirror模块迁移回主仓(已完成)
2. 探索skia shader在KMP的适配写法（参考AndroidLiquidGlass库）
3. SharedNav适配KMP
4. 模块整理：shared模块改名common-logic(已完成)，common模块改名common-ui(已完成)，新建network-api模块，承载repo和解析层。network改成network-core，model整理，RepoInf
5. 所有页面管理归一化(Card、Shower、Xwx、Supabase、回归SharedNav管理，WebView、Fix、WidgetConfig仍保持，Success并入主逻辑) ，完成后对Starter类进行重构
6. 彻底去掉SP
7. 彻底去掉LiveData
### 开始改造
1. 所有依赖中，能适配KMP的，换为**同版本号**的KMP依赖。不能适配KMP的，如果是自己的库就适配，不是自己的库就在iOS端找平替库，独自实现，不要影响到安卓端
2. 传统View替换为Compose，不能替换的各自实现（目前大头就剩WebView) 
3. Compose中使用安卓特有的地方都需要重新封装except fun，例如LocalContext.current LocalActicity.current LocalConfiguration.current
4. 安卓Resourses迁移为Compose Resourses
5. 网络层安卓端保持不变Retrofit/OkHttp+Gson+Jsoup，iOS端使用Ktor/Ktorfit+Kotlin.Serialized+Ksoup，只将Repo变成公共接口，在两端分别实现
6. 剩余业务逻辑层无法统一的，分别两端实现

### 预期时间
目前91000+行代码，秋招后开始，乐观预计2个月左右（不算前期铺垫），但完成度几乎很高