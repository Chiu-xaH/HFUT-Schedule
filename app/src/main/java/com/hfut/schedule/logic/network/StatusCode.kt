package com.hfut.schedule.logic.network

enum class StatusCode(val code: Int,val description: String) {
    OK(200,"成功得到用户请求的数据"),
    CREATED(201,"新建或修改数据成功"),
    ACCEPTED(202,"一个请求已经进入后台排队(异步任务)"),
    NO_CONTENT(204,"删除数据成功"),
    MOVED(301,"接口变更"),
    REDIRECT(302,"重定向"),
    NOT_MODIFIED(304,"未修改"),
    BAD_REQUEST(400,"错误非法请求，例如缺少必须参数"),
    UNAUTHORIZED(401,"未登录，需要登录"),
    FORBIDDEN(403,"禁止访问，权限不足"),
    NOT_FOUND(404,"资源未找到"),
    METHOD_NOT_ALLOWED(405,"方法不允许，例如POST用GET"),
    NOT_ACCEPTABLE(406,"请求的格式不可得，例如用户请求JSON格式，但是只有XML格式"),
    GONE(410,"请求的资源被永久删除，且不会再得到"),
    UNPROCESSABLE_ENTITY(422,"创建对象时，发生验证错误"),
    INTERNAL_SERVER_ERROR(500,"服务器内部错误"),
    BAD_GATEWAY(502,"上游服务器不可用或响应无效"),
    SERVICE_UNAVAILABLE(503,"服务器维护或过载"),
}


fun isNotBadRequest(code : Int) : Boolean = code < StatusCode.BAD_REQUEST.code