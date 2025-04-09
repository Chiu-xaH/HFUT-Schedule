# 特性对比

## 层级实时模糊
![1](/img/blur.jpg)
![2](/img/blur2.jpg)
![3](/img/blur3.jpg)

## 动态取色
![1](/img/dynamic_color.jpg)

## 预测式返回
![1](/img/back.jpg)

## 16KB页大小
摘录 Android Developer 网

页面大小是 OS 管理内存的精细程度。如今，大多数 CPU 都支持 4 KB 页面大小，因此 Android OS 和应用一直以来都是针对 4 KB 页面大小进行构建和优化的。ARM CPU 支持更大的 16 KB 页面大小，从 Android 15 开始，AOSP 也支持构建具有 16 KB 页面大小的 Android。此选项会使用额外的内存，但可以提高系统性能。从 Android 15 开始，此选项默认处于停用状态，但作为开发者模式或开发者选项提供，以便 OEM 和应用开发者为日后在所有设备上切换到 16 KB 模式做好准备。

Android 15 及更高版本支持使用 16 KB ELF 对齐方式构建 Android，该对齐方式自 android14-6.1 开始适用于 4 KB 和 16 KB 内核。与 16 KB 内核搭配使用时，此配置会使用额外的内存，但可以提高系统性能。