
class StatusCode:
    OK = 200
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    NOT_FOUND = 404
    SERVER_ERROR = 500
    # 可以根据需要添加更多状态码


class ResultEntity:
    @staticmethod
    def success(msg: str = "success", data: any = None) -> dict:
        return {
            "state": StatusCode.OK,
            "msg": msg,
            "data": data
        }

    @staticmethod
    def fail(state: int, msg: str, data: any = None) -> dict:
        return {
            "state": state,
            "msg": msg,
            "data": data
        }


