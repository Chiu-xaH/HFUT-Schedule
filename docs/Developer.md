# 快速开始

## 全局架构概览

！：镜面效果、导航&转场动效、增量更新不在本仓库内，被解耦为库独立更新，如需修改请联系开发者

## 新增全屏界面
1. 新增一个新界面

继承NavDestination
```Kotlin
object NewPageDestination : NavDestination() {
    override val key = "new_page"
    override val title = res(R.string.navigation_label_new_page)

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
用SharedContainer包裹，以Destination的key作为key传入，传入容器的Shape、ContainerColor，并将内容器形状置为Rectangle
```Kotlin
@Composable
fun FromScreen() {
    val navController = LocalNavController.current
    val dest = NewPageDestination
    
    SharedContainer(
        key = dest.key,
        shape = Material.shapes.medium,
        containerColor = Material.colorScheme.primaryContainer
    ) {
        ListItem(
            onClick = {
                navController.push(dest)
            }
        )
    }
}
```
写完后务必测试无问题
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

