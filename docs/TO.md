# 对外能力
借助AIDL，聚在工大对外开放了几个函数，供外部App调用（只读）
## getJxglstuCourses
按日期返回课表分类 （教务数据源）
## getJxglstuExams() : List<Exam>
返回考试
## startAppRoute(route : String)
传入功能的Route，直接打开界面 
## startWebView(url : String,title : String?,cookies : String)
调用打开网页
## ...更多接口可联系开发者开放