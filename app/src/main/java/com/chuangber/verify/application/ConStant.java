package com.chuangber.verify.application;

public class ConStant {
	public static String publicFilePath;
	// 授权码，由云从科技提供，已经过期

    public static String sLicence= "MzQ0MTEwbm9kZXZpY2Vjd2F1dGhvcml6Zf3k5+bn5+Ti3+fg5efm5Of/5Obn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+ep4OXl5+fi6/Xn6+fr5+fk++bn5+Y=";




    
    public static final String PACKAGE_NAME = "com.chuangber.verify";
	public static final String URL_SERVER = "http://118.31.38.73/";
	//public static final String URL_SERVER = "http://192.168.0.234/";
	public static final int HTTP_FAIL_MAX_COUNT = 5;//最大http失败次数

	public static final String SUCCESSFUL = "1";
	public static final String FAILURE = "0";

	public static final class CARD_READER{
		// 读卡器类型
		public static final int TYPE_HS = 1; // 华视
		public static final int TYPE_HD = TYPE_HS + 1;// 华大
	}

	public static final class BEEP{
		public static final int SUCCESS = 1;
		public static final int FAIL = 2;
		public static final int TIME_OUT = 3;
		public static final int BEEP = 4;
	}
	public static final class STORAGE{
		// 存储参数
		public static final String DEFAULT_VALUE = "default";
		public static final String HOTEL_ID = "hotel_id";
		public static final String HOTEL_NAME = "hotel_name";
		public static final String MACHINE_ID = "machine_id";//机器id
		public static final String MACHINE_TOKEN = "machine_token";//登录标志
		public static final String MACHINE_NAME = "machine_name";//机器名
		public static final String ACCOUNT_REMAIN = "remain";//账户余额
	}

    public static final class CAMERA_SET{
		// 相机预览参数
		public final static int PREVIEW_WIDTH = 1280;
		public final static int PREVIEW_HEIGHT = 720 ;
	}


	//阿里热更新框架，没用上
	public static final class ALI_FIX{
		public final static String ID_SECRET = "24759745-1";
		public final static String APP_SECRET = "ca4978d3e6b3cb5ff530aacfe11ee90b";
		public final static String RSA_SECRET = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFI0gas09LLqMiJFptAspOkSxVFRec01XEBndFozSvLxJdTUVeDcI+07g8VnuoAMx7rYRt+Vg7HEhsXj3kU9YEFAkP4JOHYeTwMwLPVA5ADnwAydOX0FRUD0BxlkwCa3U9V/XlLjR/0OSgDkRuEC0RQAoCq25ApKIXx6KhP8wItTQtw2gjoWHqgABM+vMSVzXwv+lv0M6/MlIZSMLmzYTWIU8yzuMPp1lxKTEXimfS4odkHjrotZXF0K3EdYIRypYigShCEuclg0xX6y9hECl6j3RXiTeK9s5b/SYiE6HZsnNDlOaz8ohgrJ832z8aB4d6JYajGtSenSFGZ8ZF2FRXAgMBAAECggEAOlBex5scMwDlYRHtLWwYclsxwL5xTjZejXKLkUvPyp9ies1agbyjBOO1AXCbztUyu0UgCQ86rwFPU2/fyf9ScQzZf0PNOnINVYvyQh898p6lf67m58rO9NaNUnMTLcglXM4HHO+k/Qrg6J33KQIay0ByYgL2UogJm0LMn7YSxwj5zxzfL2Z+6ol5KgO5iOlGgKpek+3VryesT4+S4i1EWUXchcYXhkGqO5gaaQkG8wSYOQbsophcaNth+UJhYidgOGubijsD+OLuclRC2SatBIYcDZ0Bx1SIFHGCkgsYLMSGjuStybTw/Xu4DzmEp2aGueQOGrAguIJHTSWDAxvY0QKBgQDkAjZ5ldCzR9/0vYV0f0cp2NPu3TQCPUxFmFsL++q9cYSZGH0IcHeVBuMioL/WEL7Paola3I9REZNkTRnNhJAkUIr12SyjWeTHkPjbZ0QfEG+BuLwY4PAxF5Gy6Bb/NFVDn+2+7iIZOEbKy5ZfT0YRdhEnlNZTUJckvnD3JkACrwKBgQCVe38/nnNd+lC/CXZfWIZM4GXGIWHKNOLH1KC+3a93mJqgU9rs+0Q0IehTNsokNJnkmwKwdrJQAGmagpVNilvXIAHhWAMdHhi5JutKNyjDR8U1LhvmjlvFhlRlaoFkLKhLDOeaFYmQKSG09/OKUdb2P6EKMQCY81C28los6WZS2QKBgQDiHperqLnasnMWIkYRrJAEnjY/8zu65NOZSWkxz1mTGtsngTOuhOry7mufUdAuGVlCFiO16npvKYEEvbnTKZ5RhVbqTcCJF4NdrxY9cIIMVJ5hBbX07k8GQJuL08PKwDm24QpEiaFjZX4/a/vq9ZFFi9Ojb2XGJmxeUkdxs4tTnQKBgGh0mAL40l1FYc9c6V7PGWn3FKdTBGasdhx1tK4rc0X7WXHXpxAatRpI53PewXpzV2ar+0EccJX+88yIm5RHrs2xDB9PxT0/nr8jNx/xU4bCTEhRJCYELJTmvWrw9eHuYtuu9NhzXGFknK085u4tKi2BYSMiAuzeefSdfe4MWvhhAoGBAL7hIXaon+jGdiIJqti55s6XEAyXqU+fcSwfNGQ0ZofzTpnEqMRJdJ0vDEIK9DpyvaBWQEU/Dm0C3QOd5ZVu9VXotxqykv9XQ10m9YlBJUH0uPGCDzO8Kz6akQCT4QqCdrCqjEwCyli8VE5iJPIoTBcDuHs5nQM4FirIxAsqXtgA";
	}

}
