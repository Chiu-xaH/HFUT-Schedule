# 深度链接 

> v4.20.5.4及其以上可用

## 集成到Web
```html
<!DOCTYPE html>
<html>
<body>

<p>
    <a href="intent://admission#Intent;scheme=hfut_schedule;end">
        在聚在工大中查看本科招生
    </a>
</p>

</body>
</html>
```

## 使用Shell指令打开：
```bash
adb shell am start -a android.intent.action.VIEW -d "hfut_schedule://admission"
```

## 当前开放的Uri

1. 本科招生

```
hfut_schedule://admission
```

2. 全校考试安排

```
hfut_schedule://all_exam
```

3. 校车

```
hfut_schedule://bus
```


4. 学院汇总

```
hfut_schedule://all_department
```


5. 寝室

```
hfut_schedule://dormitory
```


6. 学费

```
hfut_schedule://fee
```


7. 海乐生活

```
hfut_schedule://washing
```


8. 假期

```
hfut_schedule://holiday
```


9. 生活服务

```
hfut_schedule://life
```


10. 通知中心

```
hfut_schedule://notification_center
```


11. 办事大厅

```
hfut_schedule://office_hall
```


12. 个人信息

```
hfut_schedule://person_info
```


13. 扫一扫

```
hfut_schedule://scan_qr_code
```


14. 设置-维护与关于

```
hfut_schedule://settings_about
```


15. 关于开发者

```
hfut_schedule://settings_about_developer
```


16. 设置-外观

```
hfut_schedule://settings_appearance
```


17. 设置-备份与恢复

```
hfut_schedule://settings_backup
```


18. 设置-偏好与配置

```
hfut_schedule://settings_configurations
```


19. 设置-深度链接

```
hfut_schedule://settings_deeplink
```


21. 设置-网络

```
hfut_schedule://settings_network
```


22. 学期报告

```
hfut_schedule://term_report
```


23. 提案板

```
hfut_schedule://track
```


24. 本版本新特性

```
hfut_schedule://version_info
```


25. 网址导航

```
hfut_schedule://web_folder
```


26. WebVpn

```
hfut_schedule://webvpn
```


27. 就业

```
hfut_schedule://work
```


28. 图书馆

```
hfut_schedule://library
```


29. 借阅图书记录

```
hfut_schedule://library_borrowed
```



30. 教室

```
hfut_schedule://classroom
```


31. 平均成绩

```
hfut_schedule://average_grade
```


32. 全校培养方案

```
hfut_schedule://all_program
```


33. 考试

```
hfut_schedule://exam
```


34. 成绩


```
hfut_schedule://grade
```


35. 培养方案完成情况

```
hfut_schedule://program_competition
```


36. 培养方案

```
hfut_schedule://program
```


37. 课程汇总

```
hfut_schedule://all_course
```


38. 作息

```
hfut_schedule://work_and_rest
```


39. 日程添加

```
hfut_schedule://add_event
```


40. 通知公告

```
# keyword 为 String
hfut_schedule://news?keyword=XX

hfut_schedule://news
```


41. 教师检索

```
# name 为 String
hfut_schedule://teacher_search?name=XX

hfut_schedule://teacher_search
```


42. 挂科率

```
# course_name 为 String，lesson_code 为 String
hfut_schedule://fail_rate?course_name=XX&lesson_code=XX

hfut_schedule://fail_rate?course_name=XX

hfut_schedule://fail_rate
```



43. 教学班同班同学

```
# lesson_id 为 Int
hfut_schedule://course_classmates?lesson_id=XX

hfut_schedule://course_classmates
```


44. 主页
```
# 等同于打开聚焦
hfut_schedule://home

hfut_schedule://home?page=calendar

hfut_schedule://home?page=focus

hfut_schedule://home?page=functions

hfut_schedule://home?page=settings
```

