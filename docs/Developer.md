# 快速开始

OpenJDK v17.0.0.1

SDK: 目标36(Android 16.0) \ 最低26(Android 8.0)

镜面效果、导航&转场动效、增量更新不在本仓库内，被解耦为库独立更新，如需修改请联系开发者

## 全局架构概览

正在制作...

## 新增页面
继承NavDestination
```Kotlin
object NewPageDestination : NavDestination() {
    override val key = "new_page"
    override val title = text("新页面")

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewPageScreen(vm)
    }
}
```
在需要进入本界面的地方调用
```Kotlin
@Composable
fun FromScreen() {
    val navController = LocalNavController.current
    
    ListItem(
        onClick = {
            navController.push(NewPageDestination)
        }
    )
}
```

## 引入容器共享动效
见SharedNav仓库开发文档：[跳转](https://github.com/Chiu-xaH/SharedNav?tab=readme-ov-file#%E6%B7%BB%E5%8A%A0%E5%AE%B9%E5%99%A8%E5%85%B1%E4%BA%AB%E5%8A%A8%E6%95%88)，写完后务必测试无问题

## 新增网络API

## 新增持久化存储
### 数据库
### 键值对
### 大文本缓存（JSON、XML等）

## 新增弹窗

## 新增渐进式模糊

## 新增液态玻璃效果

## 右上角新增按钮

## 新增CAS登录平台

## 新增课程表类型

## 新增查询中心新卡片

## 新增桌面小组件

## 常用自定义组件


## ...

## 未来适配

开发者将在2027年毕业，App大部分功能都是端侧处理，可以独立运行，如果学校抽风了换接口，可以联系我，应该可以修，或者有能力者提交PR

- **低于Android 8的设备**有使用需求可以**联系我**，可以向下适配
- **鸿蒙NEXT**可以用**卓易通**运行本App
- **iOS**，理解iOS用户的呼声，我自己主力机就是苹果，但是我没苹果本子，就为了开发一个App太贵了... iOS在大学某些方面不如安卓便利，建议**搞一台安卓**，日常很方便的
- **桌面端**，电脑用学校的网页体验也很好了，非要用也是可以的，Windows 10+可以用**WSA**运行本App，后续可能会出PC版集成一些WebVpn、校园网功能
- 唯一的跨平台适配可能性交给**Kotlin/Compose Multiplatform**，这是最低成本的方案了，可以将现有Android项目改造成跨平台项目，但是这其中需要完全重写网络层，由Retrofit&OkHttp换为Ktor，而且逻辑层很多内容都需要做分平台适配，好在这项技术可以让本App已有的Compose写的UI能够跨平台，UI层改动小一些

