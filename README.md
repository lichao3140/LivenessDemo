# 活体
### 表格

| 常量名      | 常量值     | 常量说明     |
| ---------- | :-----------:  | :-----------: |
| MOK                                           |     0      |  成功  |   
| MERR_UNKNOWN                                  |     1      |  错误原因不明 |  
| MERR_INVALID_PARAM                            |     2      |  无效的参数   |
| MERR_UNSUPPORTED                              |     3      |  引擎不支持   |
| MERR_NO_MEMORY                                |     4      |  内存不足   |
| MERR_BAD_STATE                                |     5      |  状态错误    |
| MERR_USER_CANCEL                              |     6      |  用户取消相关操作    |
| MERR_EXPIRED                                  |     7      |  操作时间过期       |
| MERR_USER_PAUSE                               |     8      |  用户暂停操作       |
| MERR_BUFFER_OVERFLOW                          |     9      |  缓冲上溢       |
| MERR_BUFFER_UNDERFLOW                         |     10     |  缓冲下溢       |
| MERR_NO_DISKSPACE                             |     11     |  存贮空间不足     |
| MERR_COMPONENT_NOT_EXIST                      |     12     |  组件不存在   |
| MERR_GLOBAL_DATA_NOT_EXIST                    |     13     |  全局数据不存在   |
| MERR_FSDK_INVALID_APP_ID                      |    28673   |  无效的App Id    |
| MERR_FSDK_INVALID_SDK_ID                      |    28674   |  无效的SDK key    |
| MERR_FSDK_INVALID_ID_PAIR                     |    28675   |  AppId 和SDKKey 不匹配    |
| MERR_FSDK_MISMATCH_ID_AND_SDK                 |    28676   |  SDKKey 和使用的SDK 不匹配   |
| MERR_FSDK_SYSTEM_VERSION_UNSUPPORTED          |    28677   |  系统版本不被当前SDK 所支持   |
| MERR_FSDK_LICENCE_EXPIRED                     |    28678   |  SDK 有效期过期，需要重新下载更新    |
| MERR_FSDK_FACEFEATURE_ERROR_BASE              |    81920   |  人脸特征检测错误类型   |
| MERR_FSDK_FACEFEATURE_UNKNOWN                 |    81921   |  人脸特征检测错误未知   |
| MERR_FSDK_FACEFEATURE_MEMORY                  |    81922   |  人脸特征检测内存错误    |
| MERR_FSDK_FACEFEATURE_INVALID_FORMAT          |    81923   |  人脸特征检测格式错误    |
| MERR_FSDK_FACEFEATURE_INVALID_PARAM           |    81924   |  人脸特征检测参数错误    |
| MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL    |    81925   |  人脸特征检测结果置信度低    |
| MERR_AF_EX_BASE_FEATURE_UNSUPPORTED_ON_INIT   |    86017   |  Engine 不支持的检测属性    |
| MERR_AF_EX_BASE_FEATURE_UNINITED              |    86018   |  需要检测是属性未初始化    |
| MERR_AF_EX_BASE_FEATURE_UNPROCESSED           |    86019   |  待获取的属性未在PROCESS 中处理过  |
| MERR_AF_EX_BASE_FEATURE_UNSUPPORTED_ON_PROCES |    86020   |  PROCESS 不支持的检测属性    |
| MERR_AF_EX_BASE_INVALID_IMAGE_INFO            |    86021   |  无效的输入图像    |
| MERR_AF_EX_BASE_INVALID_FACE_INFO             |    86022   |  无效的脸部信息    |
| MERR_AL_BASE_ACTIVATION_FAIL                  |    90113   |  Liveness SDK 激活失败,请打开读写权限 |
| MERR_AL_BASE_ALREADY_ACTIVATED                |    90114   |  Liveness SDK 已激活     |
| MERR_AL_BASE_NOT_ACTIVATED                    |    90115   |  Liveness SDK 未激活     |
| MERR_AL_BASE_APPID_MISMATCH                   |    90116   |  APPID 不匹配     |
| MERR_AL_BASE_VERION_MISMATCH                  |    90117   |  SDK 版本不匹配     |
| MERR_AL_BASE_DEVICE_MISMATCH                  |    90118   |  设备不匹配     |
| MERR_AL_BASE_UNIQUE_IDENTIFIER_MISMATCH       |    90119   |  唯一标识不匹配     |
| MERR_AL_BASE_PARAM_NULL                       |    90120   |  参数为空     |
| MERR_AL_BASE_SDK_EXPIRED                      |    90121   |  SDK 已过期     |
| MERR_AL_BASE_VERSION_NOT_SUPPORT              |    90122   |  版本不支持     |
| MERR_AL_BASE_SIGN_ERROR                       |    90123   |  签名错误     |
| MERR_AL_BASE_DATABASE_ERROR                   |    90124   |  验证信息存储异常     |
| MERR_AL_NETWORK_BASE_COULDNT_RESOLVE_HOST     |    94209   |  无法解析主机地址      |
| MERR_AL_NETWORK_BASE_COULDNT_CONNECT_SERVER   |    94210   |  无法连接服务器      |
| MERR_AL_NETWORK_BASE_CONNECT_TIMEOUT          |    94211   |  网络连接超时      |
| MERR_AL_NETWORK_BASE_UNKNOWN_ERROR            |    94212   |  未知错误      |