# 快速开始

OpenJDK v17.0.0.1

SDK: 目标36(Android 16.0) \ 最低26(Android 8.0)

镜面效果、导航&转场动效、增量更新不在本仓库内，被解耦为库独立更新，如需修改请联系开发者

## 全局架构概览

正在制作...



## 新增页面

### 新增Destination
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

### 引入容器共享动效
见SharedNav仓库开发文档：[跳转](https://github.com/Chiu-xaH/SharedNav?tab=readme-ov-file#%E6%B7%BB%E5%8A%A0%E5%AE%B9%E5%99%A8%E5%85%B1%E4%BA%AB%E5%8A%A8%E6%95%88)，写完后务必测试无问题

### 新增渐进式模糊
待书写...

### 新增液态玻璃效果
待书写...

### 增加下拉刷新
待书写...

## 新增持久化存储

### 数据库
待书写...

### 键值对
待书写...

### 大文本缓存（JSON、XML等）
待书写...

## 新增网络API
待书写...

## 新增CAS登录平台
待书写...

## 课程表适配器
待书写...

## 新增查询中心功能
待书写...

## 应用内部打开网页
待书写...

## 跳转至外部

### 打开App
待书写...

### 打开网页
待书写...

## 添加日志
待书写...

## 添加Toast
待书写...

## 多语言适配
待书写...

## 安全除法
待书写...

## 四舍五入
待书写...

## 应用外功能

### 新增桌面小组件
待书写...

### 新增控制中心磁贴
待书写...

### 新增Shortcut（长按桌面图标菜单）
待书写...

### 新增对外接口（AIDL）
待书写...

## 常用自定义组件
聚在工大拥有较长时间的维护周期，拥有完备的自定义组件，请优先使用已有组件，以保持应用内风格统一
### 进度条 LineProgressIndicator.kt
待书写...

### 滑块 CustomSlider.kt
待书写...
### 通用弹窗 Dialog.kt
待书写...
### 下推内容 HazeBottomSheet
待书写...
### 大按钮 LargeButton.kt
待书写...
### 卡片 & 卡片项目 & 大卡片 CustomCard.kt
待书写...
### 卡片按钮 BottomButton.kt & BottomTextButtonGroup.kt
待书写...
### 液态玻璃按钮 LiquidButton.kt
待书写...
### 底栏 BottomBar.kt
待书写...
### 副标题 DividerText.kt
待书写...
### 共享容器 SharedContainer.kt
待书写...
### 时间 & 日期选择器 DateRangePickerModal.kt
待书写...
### 分割线 PaddingDivider
待书写...
### 图标集 DepartmentIcons
待书写...
### 滚轮选择器 WheelPicker.kt
待书写...
### 输入框 & 搜索框 CustomTextField.kt
待书写...
### 网络图片显示 UrlImage,kt
待书写...
### 通用网络加载器 CommonNetworkScreen.kt
待书写...
### 分页器 CustomTabRow.kt
待书写...
### 翻页器 PageController.kt
待书写...
### 指示器 PageIndicator.kt
待书写...
### 礼花 Party.kt
待书写...
### 下拉刷新指示器 Material3RefreshIndicator.kt
待书写...
### 大图标状态 StatusIcon.kt
待书写...
### 加载图标 RotatingIcons.kt 
待书写...
### 加载态 LoadingIcon
待书写...
### 二级菜单 MenuChip.kt
待书写...
### 返回图标 CustomTopBarBackIcon.kt
待书写...
### 全局边距 AppDp.kt
待书写...
### 3D按压 OnCLick.kt
待书写...
### 安全区边距 /padding
待书写...
### 布局器 CustomRowColumn.kt
待书写...
### 小提示文本 BottomTip.kt
待书写...
### 滚动文本 ScrollText.kt
待书写...
### 亮色滚动效果 Shimmer.kt
待书写...
### 多选一开关 CustomSingleChoiceRow.kt
待书写...
### 图表 BarChart.kt LineChart.kt PieChart.kt RadarChart.kt
待书写...
### ...

## 未来适配

开发者将在2027年毕业，App大部分功能都是端侧处理，可以独立运行，如果学校抽风了换接口，可以联系我，应该可以修，或者有能力者提交PR

- **低于Android 8的设备**有使用需求可以**联系我**，可以向下适配
- **鸿蒙NEXT**可以用**卓易通**运行本App
- **iOS**，理解iOS用户的呼声，我自己主力机就是苹果，但是我没苹果本子，就为了开发一个App太贵了... iOS在大学某些方面不如安卓便利，建议**搞一台安卓**，日常很方便的
- **桌面端**，电脑用学校的网页体验也很好了，非要用也是可以的，Windows 10+可以用**WSA**运行本App，后续可能会出PC版集成一些WebVpn、校园网功能
- 唯一的跨平台适配可能性交给**Kotlin/Compose Multiplatform**，这是最低成本的方案了，可以将现有Android项目改造成跨平台项目，但是这其中需要完全重写网络层，由Retrofit&OkHttp换为Ktor，而且逻辑层很多内容都需要做分平台适配，好在这项技术可以让本App已有的Compose写的UI能够跨平台，UI层改动小一些

